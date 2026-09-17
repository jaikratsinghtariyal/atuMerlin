import { useCallback, useEffect, useState } from 'react';
import { createResourceApi, ordersApi, type OrderLineInput } from '../api/resources';
import { toErrorMessage } from '../api/client';
import type { Article, Customer, Order, PageResponse } from '../api/types';
import { Modal } from '../components/Modal';
import { useToast } from '../components/Toast';

const articlesApi = createResourceApi<Article, string>('/articles');
const customersApi = createResourceApi<Customer, number>('/customers');

interface DraftLine {
  articleId: string;
  quantity: string;
}

export function OrdersPage() {
  const { notify } = useToast();
  const [data, setData] = useState<PageResponse<Order> | null>(null);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);

  const [createOpen, setCreateOpen] = useState(false);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [articles, setArticles] = useState<Article[]>([]);
  const [customerId, setCustomerId] = useState('');
  const [lines, setLines] = useState<DraftLine[]>([{ articleId: '', quantity: '1' }]);
  const [formError, setFormError] = useState('');
  const [saving, setSaving] = useState(false);

  const [detail, setDetail] = useState<Order | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      setData(await ordersApi.list({ page }));
    } catch (err) {
      notify(toErrorMessage(err), 'error');
    } finally {
      setLoading(false);
    }
  }, [page, notify]);

  useEffect(() => {
    load();
  }, [load]);

  const openCreate = async () => {
    setFormError('');
    setCustomerId('');
    setLines([{ articleId: '', quantity: '1' }]);
    try {
      const [c, a] = await Promise.all([
        customersApi.list({ size: 200 }),
        articlesApi.list({ size: 200 }),
      ]);
      setCustomers(c.content);
      setArticles(a.content);
      setCreateOpen(true);
    } catch (err) {
      notify(toErrorMessage(err), 'error');
    }
  };

  const submit = async () => {
    setSaving(true);
    setFormError('');
    try {
      const payloadLines: OrderLineInput[] = lines
        .filter((l) => l.articleId && Number(l.quantity) > 0)
        .map((l) => ({ articleId: l.articleId, quantity: Number(l.quantity) }));
      if (!customerId) throw new Error('Please select a customer');
      if (payloadLines.length === 0) throw new Error('Please add at least one line');
      const created = await ordersApi.create({ customerId: Number(customerId), lines: payloadLines });
      notify(`Order #${created.id} created (total ${created.totalGross.toFixed(2)})`);
      setCreateOpen(false);
      await load();
    } catch (err) {
      setFormError(toErrorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  const deliver = async (order: Order) => {
    try {
      await ordersApi.markDelivered(order.id);
      notify(`Order #${order.id} marked delivered`);
      await load();
    } catch (err) {
      notify(toErrorMessage(err), 'error');
    }
  };

  const remove = async (order: Order) => {
    if (!window.confirm(`Delete order #${order.id}?`)) return;
    try {
      await ordersApi.remove(order.id);
      notify(`Order #${order.id} deleted`);
      await load();
    } catch (err) {
      notify(toErrorMessage(err), 'error');
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Orders</h1>
          <p>Customer orders with automatic VAT and totals (migrated from ORD*/DETORD).</p>
        </div>
        <button className="btn-primary" onClick={openCreate}>
          + New Order
        </button>
      </div>

      <div className="card">
        <table>
          <thead>
            <tr>
              <th>Order</th>
              <th>Customer</th>
              <th>Date</th>
              <th>Status</th>
              <th className="num">Net</th>
              <th className="num">VAT</th>
              <th className="num">Total</th>
              <th className="num">Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={8} className="empty">
                  Loading…
                </td>
              </tr>
            ) : data && data.content.length > 0 ? (
              data.content.map((o) => (
                <tr key={o.id}>
                  <td>#{o.id}</td>
                  <td>{o.customerName ?? o.customerId}</td>
                  <td>{o.orderDate}</td>
                  <td>
                    {o.deliveryDate ? (
                      <span className="badge green">Delivered</span>
                    ) : (
                      <span className="badge blue">Open</span>
                    )}
                  </td>
                  <td className="num">{o.totalNet.toFixed(2)}</td>
                  <td className="num">{o.totalVat.toFixed(2)}</td>
                  <td className="num">{o.totalGross.toFixed(2)}</td>
                  <td>
                    <div className="row-actions">
                      <button className="btn-secondary btn-sm" onClick={() => setDetail(o)}>
                        View
                      </button>
                      <button
                        className="btn-secondary btn-sm"
                        disabled={Boolean(o.deliveryDate)}
                        onClick={() => deliver(o)}
                      >
                        Deliver
                      </button>
                      <button className="btn-danger btn-sm" onClick={() => remove(o)}>
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={8} className="empty">
                  No orders yet.
                </td>
              </tr>
            )}
          </tbody>
        </table>
        {data && (
          <div className="pagination">
            <span>
              {data.totalElements} order{data.totalElements === 1 ? '' : 's'}
            </span>
            <div className="pages">
              <button className="btn-secondary btn-sm" disabled={data.first} onClick={() => setPage((p) => p - 1)}>
                Prev
              </button>
              <span>
                Page {data.page + 1} / {Math.max(1, data.totalPages)}
              </span>
              <button className="btn-secondary btn-sm" disabled={data.last} onClick={() => setPage((p) => p + 1)}>
                Next
              </button>
            </div>
          </div>
        )}
      </div>

      {createOpen && (
        <Modal
          title="New Order"
          onClose={() => setCreateOpen(false)}
          footer={
            <>
              <button className="btn-secondary" onClick={() => setCreateOpen(false)}>
                Cancel
              </button>
              <button className="btn-primary" onClick={submit} disabled={saving}>
                {saving ? 'Creating…' : 'Create order'}
              </button>
            </>
          }
        >
          <div className="field full">
            <label>Customer *</label>
            <select value={customerId} onChange={(e) => setCustomerId(e.target.value)}>
              <option value="">— Select customer —</option>
              {customers.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
          </div>

          <div className="order-lines">
            <label className="field">Lines</label>
            {lines.map((line, idx) => (
              <div className="line-row" key={idx}>
                <select
                  value={line.articleId}
                  onChange={(e) => updateLine(setLines, lines, idx, { articleId: e.target.value })}
                >
                  <option value="">— Select article —</option>
                  {articles.map((a) => (
                    <option key={a.id} value={a.id}>
                      {a.id} — {a.description} ({(a.salePrice ?? 0).toFixed(2)})
                    </option>
                  ))}
                </select>
                <input
                  type="number"
                  min={1}
                  value={line.quantity}
                  onChange={(e) => updateLine(setLines, lines, idx, { quantity: e.target.value })}
                  placeholder="Qty"
                />
                <span className="muted">
                  {articlePrice(articles, line)}
                </span>
                <button
                  className="btn-secondary btn-sm"
                  onClick={() => setLines(lines.filter((_, i) => i !== idx))}
                  disabled={lines.length === 1}
                >
                  ✕
                </button>
              </div>
            ))}
            <button
              className="btn-secondary btn-sm"
              onClick={() => setLines([...lines, { articleId: '', quantity: '1' }])}
            >
              + Add line
            </button>
          </div>
          <p className="muted" style={{ fontSize: 13, marginTop: 12 }}>
            Unit price and VAT default from the selected article; VAT and totals are computed by the API.
          </p>
          {formError && <div className="form-error">{formError}</div>}
        </Modal>
      )}

      {detail && (
        <Modal title={`Order #${detail.id}`} onClose={() => setDetail(null)}>
          <p>
            <strong>{detail.customerName}</strong> · {detail.orderDate}
            {detail.deliveryDate ? ` · delivered ${detail.deliveryDate}` : ''}
          </p>
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Article</th>
                <th className="num">Qty</th>
                <th className="num">Unit</th>
                <th>VAT</th>
                <th className="num">Net</th>
                <th className="num">Gross</th>
              </tr>
            </thead>
            <tbody>
              {detail.lines.map((l) => (
                <tr key={l.line}>
                  <td>{l.line}</td>
                  <td>{l.articleDescription ?? l.articleId}</td>
                  <td className="num">{l.quantity}</td>
                  <td className="num">{l.unitPrice.toFixed(2)}</td>
                  <td>{l.vatCode}</td>
                  <td className="num">{l.lineNet.toFixed(2)}</td>
                  <td className="num">{l.lineGross.toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>
          <div style={{ textAlign: 'right', marginTop: 16 }}>
            <div>Net: {detail.totalNet.toFixed(2)}</div>
            <div>VAT: {detail.totalVat.toFixed(2)}</div>
            <div style={{ fontWeight: 700, fontSize: 18 }}>Total: {detail.totalGross.toFixed(2)}</div>
          </div>
        </Modal>
      )}
    </div>
  );
}

function updateLine(
  setLines: (l: DraftLine[]) => void,
  lines: DraftLine[],
  idx: number,
  patch: Partial<DraftLine>,
) {
  setLines(lines.map((l, i) => (i === idx ? { ...l, ...patch } : l)));
}

function articlePrice(articles: Article[], line: DraftLine): string {
  const a = articles.find((x) => x.id === line.articleId);
  if (!a) return '';
  const qty = Number(line.quantity) || 0;
  return `≈ ${((a.salePrice ?? 0) * qty).toFixed(2)}`;
}
