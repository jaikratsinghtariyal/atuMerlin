import { Navigate, Route, Routes } from 'react-router-dom';
import { Layout } from './components/Layout';
import { ResourcePage } from './components/ResourcePage';
import { ToastProvider } from './components/Toast';
import { DashboardPage } from './pages/DashboardPage';
import { OrdersPage } from './pages/OrdersPage';
import {
  articleConfig,
  countryConfig,
  customerConfig,
  familyConfig,
  parameterConfig,
  providerConfig,
  vatConfig,
} from './resources/configs';

export default function App() {
  return (
    <ToastProvider>
      <Routes>
        <Route element={<Layout />}>
          <Route index element={<DashboardPage />} />
          <Route path="orders" element={<OrdersPage />} />
          <Route path="articles" element={<ResourcePage config={articleConfig} />} />
          <Route path="customers" element={<ResourcePage config={customerConfig} />} />
          <Route path="providers" element={<ResourcePage config={providerConfig} />} />
          <Route path="families" element={<ResourcePage config={familyConfig} />} />
          <Route path="vat-codes" element={<ResourcePage config={vatConfig} />} />
          <Route path="countries" element={<ResourcePage config={countryConfig} />} />
          <Route path="parameters" element={<ResourcePage config={parameterConfig} />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </ToastProvider>
  );
}
