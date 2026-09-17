import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { createResourceApi, ordersApi } from '../api/resources';
import { toErrorMessage } from '../api/client';
import type { Article, Customer, Order, Provider } from '../api/types';
import { useToast } from '../components/Toast';

const articles = createResourceApi<Article, string>('/articles');
const customers = createResourceApi<Customer, number>('/customers');
const providers = createResourceApi<Provider, number>('/providers');

interface Stats {
  articles: number;
  customers: number;
  providers: number;
  orders: number;
}

export function DashboardPage() {
  const { notify } = useToast();
  const [stats, setStats] = useState<Stats | null>(null);
  const [recent, setRecent] = useState<Order[]>([]);

  useEffect(() => {
    (async () => {
      try {
        const [a, c, p, o] = await Promise.all([
          articles.list({ size: 1 }),
          customers.list({ size: 1 }),
          providers.list({ size: 1 }),
          ordersApi.list({ size: 5 }),
        ]);
        setStats({
          articles: a.totalElements,
          customers: c.totalElements,
          providers: p.totalElements,
          orders: o.totalElements,
        });
        setRecent(o.content);
      } catch (err) {
        notify(toErrorMessage(err), 'error');
      }
    })();
  }, [notify]);

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Dashboard</h1>
          <p>ATU Merlin — modernized order management (migrated from IBM i / AS-400).</p>
        </div>
      </div>

      <div className="stats">
        <Stat label="Articles" value={stats?.articles} to="/articles" />
        <Stat label="Customers" value={stats?.customers} to="/customers" />
        <Stat label="Providers" value={stats?.providers} to="/providers" />
        <Stat label="Orders" value={stats?.orders} to="/orders" />
      </div>

      <div className="page-header">
        <h1 style={{ fontSize: 18 }}>Recent orders</h1>
        <Link className="btn-secondary btn-sm" to="/orders">
          View all
        </Link>
      </div>
      <div className="card">
        <table>
          <thead>
            <tr>
              <th>Order</th>
              <th>Customer</th>
              <th>Date</th>
              <th className="num">Net</th>
              <th className="num">VAT</th>
              <th className="num">Total</th>
            </tr>
          </thead>
          <tbody>
            {recent.length > 0 ? (
              recent.map((o) => (
                <tr key={o.id}>
                  <td>#{o.id}</td>
                  <td>{o.customerName ?? o.customerId}</td>
                  <td>{o.orderDate}</td>
                  <td className="num">{o.totalNet.toFixed(2)}</td>
                  <td className="num">{o.totalVat.toFixed(2)}</td>
                  <td className="num">{o.totalGross.toFixed(2)}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={6} className="empty">
                  No orders yet.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function Stat({ label, value, to }: { label: string; value?: number; to: string }) {
  return (
    <Link to={to} className="stat">
      <div className="value">{value ?? '—'}</div>
      <div className="label">{label}</div>
    </Link>
  );
}
