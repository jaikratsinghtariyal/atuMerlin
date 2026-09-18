package com.arcad.atumerlin.bootstrap;

import com.arcad.atumerlin.config.AppProperties;
import com.arcad.atumerlin.domain.Article;
import com.arcad.atumerlin.domain.Country;
import com.arcad.atumerlin.domain.Customer;
import com.arcad.atumerlin.domain.Family;
import com.arcad.atumerlin.domain.Parameter;
import com.arcad.atumerlin.domain.Provider;
import com.arcad.atumerlin.domain.Vat;
import com.arcad.atumerlin.dto.OrderDtos.OrderLineRequest;
import com.arcad.atumerlin.dto.OrderDtos.OrderRequest;
import com.arcad.atumerlin.repository.ArticleRepository;
import com.arcad.atumerlin.repository.CountryRepository;
import com.arcad.atumerlin.repository.CustomerRepository;
import com.arcad.atumerlin.repository.FamilyRepository;
import com.arcad.atumerlin.repository.ParameterRepository;
import com.arcad.atumerlin.repository.ProviderRepository;
import com.arcad.atumerlin.repository.VatRepository;
import com.arcad.atumerlin.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Seeds reference and demo data on startup when the database is empty.
 * Controlled by the configurable flag {@code app.seed.enabled}.
 */
@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final AppProperties properties;
    private final CountryRepository countryRepository;
    private final VatRepository vatRepository;
    private final FamilyRepository familyRepository;
    private final ArticleRepository articleRepository;
    private final CustomerRepository customerRepository;
    private final ProviderRepository providerRepository;
    private final ParameterRepository parameterRepository;
    private final OrderService orderService;

    public DataSeeder(AppProperties properties,
                      CountryRepository countryRepository,
                      VatRepository vatRepository,
                      FamilyRepository familyRepository,
                      ArticleRepository articleRepository,
                      CustomerRepository customerRepository,
                      ProviderRepository providerRepository,
                      ParameterRepository parameterRepository,
                      OrderService orderService) {
        this.properties = properties;
        this.countryRepository = countryRepository;
        this.vatRepository = vatRepository;
        this.familyRepository = familyRepository;
        this.articleRepository = articleRepository;
        this.customerRepository = customerRepository;
        this.providerRepository = providerRepository;
        this.parameterRepository = parameterRepository;
        this.orderService = orderService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.seed().enabled()) {
            log.info("Data seeding disabled (app.seed.enabled=false)");
            return;
        }
        if (articleRepository.count() > 0 || customerRepository.count() > 0) {
            log.info("Data already present, skipping seeding");
            return;
        }
        log.info("Seeding reference and demo data...");

        countryRepository.save(country("BE", "Belgium", "BEL"));
        countryRepository.save(country("FR", "France", "FRA"));
        countryRepository.save(country("US", "United States", "USA"));

        vatRepository.save(vat("0", "0.00", "Exempt"));
        vatRepository.save(vat("1", "6.00", "Reduced"));
        vatRepository.save(vat("2", "21.00", "Standard"));

        familyRepository.save(family("ACC", "Accessories", "2"));
        familyRepository.save(family("HRD", "Hardware", "2"));
        familyRepository.save(family("SFT", "Software", "1"));

        articleRepository.save(article("A00001", "Wireless Mouse", "24.90", "12.00", "ACC", 150, "2"));
        articleRepository.save(article("A00002", "Mechanical Keyboard", "79.00", "45.00", "ACC", 60, "2"));
        articleRepository.save(article("A00003", "27\" Monitor", "229.00", "160.00", "HRD", 25, "2"));
        articleRepository.save(article("A00004", "Office Suite License", "99.00", "60.00", "SFT", 999, "1"));

        Customer acme = customerRepository.save(customer("ACME Corporation", "acme@example.com", "BE", "BE0123456789"));
        customerRepository.save(customer("Globex SA", "contact@globex.fr", "FR", "FR7612345678"));

        providerRepository.save(provider("TechDistribution Ltd", "John Carter", "BE"));
        providerRepository.save(provider("Nordic Supplies", "Erik Olsen", "FR"));

        parameterRepository.save(parameter("COMPANY", "NAME", "ATU Merlin Demo Company"));
        parameterRepository.save(parameter("ORDER", "PREFIX", "ORD"));

        // Demo order exercising the VAT/total business logic and last-order-date update.
        orderService.create(new OrderRequest(
                acme.getId(),
                LocalDate.now(),
                List.of(
                        new OrderLineRequest("A00001", 3, null, null),
                        new OrderLineRequest("A00003", 1, null, null))));

        log.info("Seeding complete");
    }

    private Country country(String code, String name, String iso) {
        Country c = new Country();
        c.setCode(code);
        c.setName(name);
        c.setIsoCode(iso);
        return c;
    }

    private Vat vat(String code, String rate, String description) {
        Vat v = new Vat();
        v.setCode(code);
        v.setRate(new BigDecimal(rate));
        v.setDescription(description);
        return v;
    }

    private Family family(String id, String description, String vatCode) {
        Family f = new Family();
        f.setId(id);
        f.setDescription(description);
        f.setDefaultVatCode(vatCode);
        return f;
    }

    private Article article(String id, String description, String sale, String whs, String family, int stock, String vat) {
        Article a = new Article();
        a.setId(id);
        a.setDescription(description);
        a.setSalePrice(new BigDecimal(sale));
        a.setWarehousePrice(new BigDecimal(whs));
        a.setFamilyId(family);
        a.setStock(stock);
        a.setMinStock(10);
        a.setVatCode(vat);
        return a;
    }

    private Customer customer(String name, String email, String country, String vatNumber) {
        Customer c = new Customer();
        c.setName(name);
        c.setEmail(email);
        c.setCountryCode(country);
        c.setVatNumber(vatNumber);
        c.setCreditLimit(new BigDecimal("10000.00"));
        return c;
    }

    private Provider provider(String name, String contact, String country) {
        Provider p = new Provider();
        p.setName(name);
        p.setContact(contact);
        p.setCountryCode(country);
        return p;
    }

    private Parameter parameter(String code, String subCode, String value2) {
        Parameter p = new Parameter();
        p.setCode(code);
        p.setSubCode(subCode);
        p.setValue2(value2);
        return p;
    }
}
