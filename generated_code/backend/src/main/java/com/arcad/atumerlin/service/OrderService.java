package com.arcad.atumerlin.service;

import com.arcad.atumerlin.common.BusinessRuleException;
import com.arcad.atumerlin.common.PageResponse;
import com.arcad.atumerlin.common.PageableFactory;
import com.arcad.atumerlin.common.ResourceNotFoundException;
import com.arcad.atumerlin.domain.Article;
import com.arcad.atumerlin.domain.Customer;
import com.arcad.atumerlin.domain.OrderHeader;
import com.arcad.atumerlin.domain.OrderLine;
import com.arcad.atumerlin.domain.Vat;
import com.arcad.atumerlin.dto.OrderDtos.OrderLineRequest;
import com.arcad.atumerlin.dto.OrderDtos.OrderLineResponse;
import com.arcad.atumerlin.dto.OrderDtos.OrderRequest;
import com.arcad.atumerlin.dto.OrderDtos.OrderResponse;
import com.arcad.atumerlin.dto.OrderDtos.OrderStatusRequest;
import com.arcad.atumerlin.repository.ArticleRepository;
import com.arcad.atumerlin.repository.CustomerRepository;
import com.arcad.atumerlin.repository.OrderRepository;
import com.arcad.atumerlin.repository.VatRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Customer order processing, migrating the business logic of the ORD1xx/ORD2xx
 * RPG programs, the VAT300 service program and the ORD701 insert trigger.
 */
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final ArticleService articleService;
    private final ArticleRepository articleRepository;
    private final VatRepository vatRepository;
    private final VatCalculator vatCalculator;
    private final PageableFactory pageableFactory;

    public OrderService(OrderRepository orderRepository,
                        CustomerService customerService,
                        CustomerRepository customerRepository,
                        ArticleService articleService,
                        ArticleRepository articleRepository,
                        VatRepository vatRepository,
                        VatCalculator vatCalculator,
                        PageableFactory pageableFactory) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.articleService = articleService;
        this.articleRepository = articleRepository;
        this.vatRepository = vatRepository;
        this.vatCalculator = vatCalculator;
        this.pageableFactory = pageableFactory;
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> list(Long customerId, Integer page, Integer size) {
        var pageable = pageableFactory.of(page, size, Sort.by("orderDate").descending());
        var result = customerId != null
                ? orderRepository.findByCustomerId(customerId, pageable)
                : orderRepository.findAll(pageable);
        return PageResponse.from(result, this::toResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse get(Long id) {
        return toResponse(find(id));
    }

    public OrderResponse create(OrderRequest request) {
        Customer customer = customerService.require(request.customerId());
        LocalDate orderDate = Optional.ofNullable(request.orderDate()).orElse(LocalDate.now());

        OrderHeader order = new OrderHeader();
        order.setCustomerId(customer.getId());
        order.setOrderDate(orderDate);
        order.setYear(orderDate.getYear());

        int lineNo = 1;
        for (OrderLineRequest lineRequest : request.lines()) {
            order.addLine(buildLine(lineNo++, lineRequest));
        }
        recalculateTotals(order);

        OrderHeader saved = orderRepository.save(order);

        // Mirrors trigger ORD701: keep the customer's last order date in sync.
        customer.setLastOrderDate(orderDate);
        customerRepository.save(customer);

        return toResponse(saved);
    }

    public OrderResponse updateStatus(Long id, OrderStatusRequest request) {
        OrderHeader order = find(id);
        if (request.deliveryDate() != null) {
            order.setDeliveryDate(request.deliveryDate());
        }
        if (request.closeDate() != null) {
            order.setCloseDate(request.closeDate());
        }
        // Mirrors option 8 (deliver): delivered qty = ordered qty, delivery date defaults to today.
        if (Boolean.TRUE.equals(request.markFullyDelivered())) {
            if (order.getDeliveryDate() == null) {
                order.setDeliveryDate(LocalDate.now());
            }
            order.getLines().forEach(line -> line.setDeliveredQuantity(line.getQuantity()));
        }
        return toResponse(orderRepository.save(order));
    }

    public void delete(Long id) {
        OrderHeader order = find(id);
        boolean anyDelivered = order.getLines().stream().anyMatch(l -> l.getDeliveredQuantity() > 0);
        if (anyDelivered) {
            throw new BusinessRuleException("Order " + id + " cannot be deleted: some lines are already delivered");
        }
        orderRepository.delete(order);
    }

    private OrderLine buildLine(int lineNo, OrderLineRequest request) {
        Article article = articleService.require(request.articleId());

        BigDecimal unitPrice = Optional.ofNullable(request.unitPrice())
                .orElse(Optional.ofNullable(article.getSalePrice()).orElse(BigDecimal.ZERO));
        String vatCode = Optional.ofNullable(request.vatCode()).orElse(article.getVatCode());

        OrderLine line = new OrderLine();
        line.setLine(lineNo);
        line.setArticleId(article.getId());
        line.setQuantity(request.quantity());
        line.setDeliveredQuantity(0);
        line.setUnitPrice(unitPrice);
        line.setVatCode(vatCode);
        applyLineAmounts(line);
        return line;
    }

    /** Line totals: net = qty * unitPrice, vat via VAT300 rate, gross = net + vat. */
    private void applyLineAmounts(OrderLine line) {
        BigDecimal net = line.getUnitPrice()
                .multiply(BigDecimal.valueOf(line.getQuantity()))
                .setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal rate = resolveRate(line.getVatCode());
        BigDecimal vat = vatCalculator.vatAmount(net, rate);
        line.setLineNet(net);
        line.setLineVat(vat);
        line.setLineGross(net.add(vat));
    }

    private BigDecimal resolveRate(String vatCode) {
        if (vatCode == null || vatCode.isBlank()) {
            return BigDecimal.ZERO;
        }
        return vatRepository.findById(vatCode)
                .filter(v -> !v.isDeleted())
                .map(Vat::getRate)
                .orElseThrow(() -> new BusinessRuleException("Unknown VAT code: " + vatCode));
    }

    private void recalculateTotals(OrderHeader order) {
        BigDecimal net = BigDecimal.ZERO;
        BigDecimal vat = BigDecimal.ZERO;
        BigDecimal gross = BigDecimal.ZERO;
        for (OrderLine line : order.getLines()) {
            net = net.add(line.getLineNet());
            vat = vat.add(line.getLineVat());
            gross = gross.add(line.getLineGross());
        }
        order.setTotalNet(net);
        order.setTotalVat(vat);
        order.setTotalGross(gross);
    }

    private OrderHeader find(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    private OrderResponse toResponse(OrderHeader order) {
        String customerName = customerRepository.findById(order.getCustomerId())
                .map(Customer::getName)
                .orElse(null);

        Map<String, String> descriptions = order.getLines().isEmpty()
                ? Map.of()
                : articleRepository.findAllById(
                        order.getLines().stream().map(OrderLine::getArticleId).distinct().toList())
                .stream()
                .collect(Collectors.toMap(Article::getId, Article::getDescription, (a, b) -> a));

        List<OrderLineResponse> lines = order.getLines().stream()
                .map(line -> new OrderLineResponse(
                        line.getLine(), line.getArticleId(), descriptions.get(line.getArticleId()),
                        line.getQuantity(), line.getDeliveredQuantity(), line.getUnitPrice(),
                        line.getVatCode(), line.getLineNet(), line.getLineVat(), line.getLineGross()))
                .toList();

        return new OrderResponse(
                order.getId(), order.getYear(), order.getCustomerId(), customerName,
                order.getOrderDate(), order.getDeliveryDate(), order.getCloseDate(),
                order.getTotalNet(), order.getTotalVat(), order.getTotalGross(), lines);
    }
}
