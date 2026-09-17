import { createResourceApi } from '../api/resources';
import type { Country, Family, Vat } from '../api/types';
import type { Option } from '../components/ResourcePage';

const families = createResourceApi<Family, string>('/families');
const vatCodes = createResourceApi<Vat, string>('/vat-codes');
const countries = createResourceApi<Country, string>('/countries');

export async function loadFamilyOptions(): Promise<Option[]> {
  const page = await families.list({ size: 200 });
  return page.content.map((f) => ({ value: f.id, label: `${f.id} — ${f.description}` }));
}

export async function loadVatOptions(): Promise<Option[]> {
  const page = await vatCodes.list({ size: 200 });
  return page.content.map((v) => ({ value: v.code, label: `${v.code} — ${v.rate}% ${v.description ?? ''}`.trim() }));
}

export async function loadCountryOptions(): Promise<Option[]> {
  const page = await countries.list({ size: 200 });
  return page.content.map((c) => ({ value: c.code, label: `${c.code} — ${c.name}` }));
}
