import { NavLink, Outlet } from 'react-router-dom';
import { API_BASE_URL } from '../api/config';

const NAV = [
  { to: '/', label: 'Dashboard', end: true },
  { to: '/orders', label: 'Orders' },
  { to: '/articles', label: 'Articles' },
  { to: '/customers', label: 'Customers' },
  { to: '/providers', label: 'Providers' },
  { to: '/families', label: 'Families' },
  { to: '/vat-codes', label: 'VAT codes' },
  { to: '/countries', label: 'Countries' },
  { to: '/parameters', label: 'Parameters' },
];

export function Layout() {
  return (
    <div className="app">
      <aside className="sidebar">
        <div className="brand">
          ATU <span>Merlin</span>
        </div>
        <nav>
          {NAV.map((item) => (
            <NavLink key={item.to} to={item.to} end={item.end}>
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="api-info">
          API:
          <br />
          {API_BASE_URL}
        </div>
      </aside>
      <main className="main">
        <Outlet />
      </main>
    </div>
  );
}
