import type { Article, Country, Customer, Family, Parameter, Provider, Vat } from '../api/types';
import type { ResourceConfig } from '../components/ResourcePage';
import { loadCountryOptions, loadFamilyOptions, loadVatOptions } from './options';

const money = (v: unknown) => (v === null || v === undefined ? '—' : Number(v).toFixed(2));

export const articleConfig: ResourceConfig<Article> = {
  title: 'Articles',
  description: 'Article master file (migrated from ARTICLE.PF / FARTI).',
  path: '/articles',
  idField: 'id',
  idGenerated: false,
  searchable: true,
  columns: [
    { key: 'id', label: 'ID' },
    { key: 'description', label: 'Description' },
    { key: 'familyId', label: 'Family' },
    { key: 'salePrice', label: 'Sale price', numeric: true, render: (r) => money(r.salePrice) },
    { key: 'stock', label: 'Stock', numeric: true },
    { key: 'vatCode', label: 'VAT' },
  ],
  fields: [
    { name: 'id', label: 'Article ID', required: true, keyOnCreate: true },
    { name: 'description', label: 'Description', required: true },
    { name: 'salePrice', label: 'Sale price', type: 'number', step: '0.01' },
    { name: 'warehousePrice', label: 'Warehouse price', type: 'number', step: '0.01' },
    { name: 'familyId', label: 'Family', type: 'select', loadOptions: loadFamilyOptions },
    { name: 'vatCode', label: 'VAT code', type: 'select', loadOptions: loadVatOptions },
    { name: 'stock', label: 'Stock', type: 'number' },
    { name: 'minStock', label: 'Minimum stock', type: 'number' },
    { name: 'information', label: 'Information', type: 'textarea' },
  ],
};

export const customerConfig: ResourceConfig<Customer> = {
  title: 'Customers',
  description: 'Customer master file (migrated from CUSTOMER.PF / FCUST).',
  path: '/customers',
  idField: 'id',
  idGenerated: true,
  searchable: true,
  columns: [
    { key: 'id', label: 'ID' },
    { key: 'name', label: 'Name' },
    { key: 'email', label: 'Email' },
    { key: 'countryCode', label: 'Country' },
    { key: 'creditLimit', label: 'Credit limit', numeric: true, render: (r) => money(r.creditLimit) },
    { key: 'lastOrderDate', label: 'Last order' },
  ],
  fields: [
    { name: 'name', label: 'Name', required: true },
    { name: 'email', label: 'Email' },
    { name: 'phone', label: 'Phone' },
    { name: 'vatNumber', label: 'VAT number' },
    { name: 'addressLine1', label: 'Address' },
    { name: 'zipCode', label: 'Zip code' },
    { name: 'city', label: 'City' },
    { name: 'countryCode', label: 'Country', type: 'select', loadOptions: loadCountryOptions },
    { name: 'creditLimit', label: 'Credit limit', type: 'number', step: '0.01' },
    { name: 'credit', label: 'Credit', type: 'number', step: '0.01' },
  ],
};

export const providerConfig: ResourceConfig<Provider> = {
  title: 'Providers',
  description: 'Provider (supplier) master file (migrated from PROVIDER.PF / FPROV).',
  path: '/providers',
  idField: 'id',
  idGenerated: true,
  searchable: true,
  columns: [
    { key: 'id', label: 'ID' },
    { key: 'name', label: 'Name' },
    { key: 'contact', label: 'Contact' },
    { key: 'email', label: 'Email' },
    { key: 'countryCode', label: 'Country' },
  ],
  fields: [
    { name: 'name', label: 'Name', required: true },
    { name: 'contact', label: 'Contact person' },
    { name: 'email', label: 'Email' },
    { name: 'phone', label: 'Phone' },
    { name: 'vatNumber', label: 'VAT number' },
    { name: 'addressLine1', label: 'Address' },
    { name: 'zipCode', label: 'Zip code' },
    { name: 'city', label: 'City' },
    { name: 'countryCode', label: 'Country', type: 'select', loadOptions: loadCountryOptions },
  ],
};

export const familyConfig: ResourceConfig<Family> = {
  title: 'Families',
  description: 'Article family reference (migrated from FAMILLY.PF / FFAMI).',
  path: '/families',
  idField: 'id',
  idGenerated: false,
  searchable: true,
  columns: [
    { key: 'id', label: 'ID' },
    { key: 'description', label: 'Description' },
    { key: 'defaultVatCode', label: 'Default VAT' },
  ],
  fields: [
    { name: 'id', label: 'Family ID', required: true, keyOnCreate: true },
    { name: 'description', label: 'Description', required: true },
    { name: 'defaultVatCode', label: 'Default VAT code', type: 'select', loadOptions: loadVatOptions },
  ],
};

export const vatConfig: ResourceConfig<Vat> = {
  title: 'VAT codes',
  description: 'VAT code and rate reference (migrated from VATDEF.PF / FVAT).',
  path: '/vat-codes',
  idField: 'code',
  idGenerated: false,
  searchable: false,
  columns: [
    { key: 'code', label: 'Code' },
    { key: 'rate', label: 'Rate %', numeric: true, render: (r) => Number(r.rate).toFixed(2) },
    { key: 'description', label: 'Description' },
  ],
  fields: [
    { name: 'code', label: 'VAT code', required: true, keyOnCreate: true },
    { name: 'rate', label: 'Rate %', type: 'number', step: '0.01', required: true },
    { name: 'description', label: 'Description' },
  ],
};

export const countryConfig: ResourceConfig<Country> = {
  title: 'Countries',
  description: 'Country reference (migrated from COUNTRY.PF / FCOUN).',
  path: '/countries',
  idField: 'code',
  idGenerated: false,
  searchable: true,
  columns: [
    { key: 'code', label: 'Code' },
    { key: 'name', label: 'Name' },
    { key: 'isoCode', label: 'ISO' },
  ],
  fields: [
    { name: 'code', label: 'Country code', required: true, keyOnCreate: true },
    { name: 'name', label: 'Name', required: true },
    { name: 'isoCode', label: 'ISO code' },
  ],
};

export const parameterConfig: ResourceConfig<Parameter> = {
  title: 'Parameters',
  description: 'Generic application parameters (migrated from PARAMETER.PF / FPARAM).',
  path: '/parameters',
  idField: 'id',
  idGenerated: true,
  searchable: true,
  columns: [
    { key: 'code', label: 'Code' },
    { key: 'subCode', label: 'Sub-code' },
    { key: 'value1', label: 'Value 1' },
    { key: 'value2', label: 'Value 2' },
  ],
  fields: [
    { name: 'code', label: 'Code', required: true },
    { name: 'subCode', label: 'Sub-code', required: true },
    { name: 'value1', label: 'Value 1' },
    { name: 'value2', label: 'Value 2' },
    { name: 'value3', label: 'Value 3' },
    { name: 'value4', label: 'Value 4 (num)', type: 'number' },
    { name: 'value5', label: 'Value 5 (num)', type: 'number' },
  ],
};
