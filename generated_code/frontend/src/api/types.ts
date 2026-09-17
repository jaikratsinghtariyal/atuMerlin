export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface Country {
  code: string;
  name: string;
  isoCode?: string;
}

export interface Vat {
  code: string;
  rate: number;
  description?: string;
}

export interface Family {
  id: string;
  description: string;
  defaultVatCode?: string;
}

export interface Article {
  id: string;
  description: string;
  salePrice?: number;
  warehousePrice?: number;
  familyId?: string;
  stock?: number;
  minStock?: number;
  customerOrderQty?: number;
  purchaseOrderQty?: number;
  vatCode?: string;
  information?: string;
}

export interface Customer {
  id: number;
  name: string;
  phone?: string;
  vatNumber?: string;
  email?: string;
  addressLine1?: string;
  zipCode?: string;
  city?: string;
  countryCode?: string;
  creditLimit?: number;
  credit?: number;
  lastOrderDate?: string;
}

export interface Provider {
  id: number;
  name: string;
  contact?: string;
  phone?: string;
  vatNumber?: string;
  email?: string;
  city?: string;
  countryCode?: string;
}

export interface Parameter {
  id: number;
  code: string;
  subCode: string;
  value1?: string;
  value2?: string;
  value3?: string;
  value4?: number;
  value5?: number;
}

export interface OrderLine {
  line: number;
  articleId: string;
  articleDescription?: string;
  quantity: number;
  deliveredQuantity: number;
  unitPrice: number;
  vatCode?: string;
  lineNet: number;
  lineVat: number;
  lineGross: number;
}

export interface Order {
  id: number;
  year: number;
  customerId: number;
  customerName?: string;
  orderDate: string;
  deliveryDate?: string;
  closeDate?: string;
  totalNet: number;
  totalVat: number;
  totalGross: number;
  lines: OrderLine[];
}
