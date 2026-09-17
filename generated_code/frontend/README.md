# ATU Merlin Frontend (React)

React + TypeScript single-page app for the [ATU Merlin API](../backend), migrated
from the IBM i (AS/400) 5250 screens to a modern web UI.

## Tech stack

- React 18 + TypeScript
- Vite 5 (dev server & build)
- React Router 6
- Axios

## Features

- Dashboard with counts and recent orders
- Full CRUD for Articles, Customers, Providers, Families, VAT codes, Countries and Parameters
- Order management: create multi-line orders (unit price & VAT default from the
  article), view computed VAT/totals, mark delivered, delete
- Search, pagination, inline validation errors and toasts

## Configuration

The API base URL is fully configurable through a Vite environment variable — no code
change needed to point at another backend.

```bash
cp .env.example .env
# then edit:
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

`FRONTEND_PORT` (optional) changes the dev/preview port.

## Run

```bash
npm install
npm run dev       # http://localhost:5173
# production build
npm run build
npm run preview
```

Make sure the backend is running and that the frontend origin is in the backend's
`CORS_ALLOWED_ORIGINS`.
