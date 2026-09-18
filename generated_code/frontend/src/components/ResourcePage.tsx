import { useCallback, useEffect, useState } from 'react';
import { createResourceApi, type ListParams } from '../api/resources';
import { toErrorMessage } from '../api/client';
import type { PageResponse } from '../api/types';
import { Modal } from './Modal';
import { useToast } from './Toast';

export interface Option {
  value: string;
  label: string;
}

export interface FieldDef {
  name: string;
  label: string;
  type?: 'text' | 'number' | 'textarea' | 'select';
  required?: boolean;
  step?: string;
  /** When true, the field is only editable while creating (natural-key ids). */
  keyOnCreate?: boolean;
  options?: Option[];
  loadOptions?: () => Promise<Option[]>;
}

export interface ColumnDef<T> {
  key: string;
  label: string;
  numeric?: boolean;
  render?: (row: T) => React.ReactNode;
}

export interface ResourceConfig<T> {
  title: string;
  description: string;
  path: string;
  idField: keyof T;
  /** true when the backend generates the id (surrogate key). */
  idGenerated: boolean;
  searchable?: boolean;
  columns: ColumnDef<T>[];
  fields: FieldDef[];
}

type FormValues = Record<string, string>;

export function ResourcePage<T>({ config }: { config: ResourceConfig<T> }) {
  const resource = createResourceApi<T, string | number>(config.path);
  const { notify } = useToast();

  const [data, setData] = useState<PageResponse<T> | null>(null);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);

  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<T | null>(null);
  const [form, setForm] = useState<FormValues>({});
  const [formError, setFormError] = useState('');
  const [saving, setSaving] = useState(false);
  const [optionsMap, setOptionsMap] = useState<Record<string, Option[]>>({});

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const params: ListParams = { page };
      if (config.searchable && search) params.search = search;
      setData(await resource.list(params));
    } catch (err) {
      notify(toErrorMessage(err), 'error');
    } finally {
      setLoading(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, search]);

  useEffect(() => {
    load();
  }, [load]);

  const openCreate = async () => {
    setEditing(null);
    setForm({});
    setFormError('');
    await loadFieldOptions();
    setModalOpen(true);
  };

  const openEdit = async (row: T) => {
    setEditing(row);
    const values: FormValues = {};
    for (const f of config.fields) {
      const v = row[f.name as keyof T];
      values[f.name] = v === null || v === undefined ? '' : String(v);
    }
    setForm(values);
    setFormError('');
    await loadFieldOptions();
    setModalOpen(true);
  };

  const loadFieldOptions = async () => {
    const map: Record<string, Option[]> = {};
    await Promise.all(
      config.fields
        .filter((f) => f.loadOptions)
        .map(async (f) => {
          try {
            map[f.name] = await f.loadOptions!();
          } catch {
            map[f.name] = [];
          }
        }),
    );
    setOptionsMap(map);
  };

  const submit = async () => {
    setSaving(true);
    setFormError('');
    try {
      const payload = buildPayload();
      if (editing) {
        await resource.update(editing[config.idField] as string | number, payload);
        notify(`${config.title} updated`);
      } else {
        await resource.create(payload);
        notify(`${config.title} created`);
      }
      setModalOpen(false);
      await load();
    } catch (err) {
      setFormError(toErrorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  const buildPayload = (): Partial<T> => {
    const payload: Record<string, unknown> = {};
    for (const f of config.fields) {
      if (f.keyOnCreate && editing) continue;
      const raw = form[f.name];
      if (raw === undefined || raw === '') {
        payload[f.name] = null;
        continue;
      }
      payload[f.name] = f.type === 'number' ? Number(raw) : raw;
    }
    return payload as Partial<T>;
  };

  const remove = async (row: T) => {
    if (!window.confirm(`Delete this ${config.title.toLowerCase()}?`)) return;
    try {
      await resource.remove(row[config.idField] as string | number);
      notify(`${config.title} deleted`);
      await load();
    } catch (err) {
      notify(toErrorMessage(err), 'error');
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>{config.title}</h1>
          <p>{config.description}</p>
        </div>
        <button className="btn-primary" onClick={openCreate}>
          + New {config.title.replace(/s$/, '')}
        </button>
      </div>

      {config.searchable && (
        <div className="toolbar">
          <input
            className="search-input"
            placeholder="Search…"
            value={search}
            onChange={(e) => {
              setPage(0);
              setSearch(e.target.value);
            }}
          />
        </div>
      )}

      <div className="card">
        <table>
          <thead>
            <tr>
              {config.columns.map((c) => (
                <th key={c.key} className={c.numeric ? 'num' : undefined}>
                  {c.label}
                </th>
              ))}
              <th className="num">Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={config.columns.length + 1} className="empty">
                  Loading…
                </td>
              </tr>
            ) : data && data.content.length > 0 ? (
              data.content.map((row, i) => (
                <tr key={i}>
                  {config.columns.map((c) => (
                    <td key={c.key} className={c.numeric ? 'num' : undefined}>
                      {c.render ? c.render(row) : formatCell(row[c.key as keyof T])}
                    </td>
                  ))}
                  <td>
                    <div className="row-actions">
                      <button className="btn-secondary btn-sm" onClick={() => openEdit(row)}>
                        Edit
                      </button>
                      <button className="btn-danger btn-sm" onClick={() => remove(row)}>
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={config.columns.length + 1} className="empty">
                  No {config.title.toLowerCase()} found.
                </td>
              </tr>
            )}
          </tbody>
        </table>
        {data && data.totalPages > 0 && (
          <div className="pagination">
            <span>
              {data.totalElements} item{data.totalElements === 1 ? '' : 's'}
            </span>
            <div className="pages">
              <button
                className="btn-secondary btn-sm"
                disabled={data.first}
                onClick={() => setPage((p) => Math.max(0, p - 1))}
              >
                Prev
              </button>
              <span>
                Page {data.page + 1} / {Math.max(1, data.totalPages)}
              </span>
              <button
                className="btn-secondary btn-sm"
                disabled={data.last}
                onClick={() => setPage((p) => p + 1)}
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>

      {modalOpen && (
        <Modal
          title={`${editing ? 'Edit' : 'New'} ${config.title.replace(/s$/, '')}`}
          onClose={() => setModalOpen(false)}
          footer={
            <>
              <button className="btn-secondary" onClick={() => setModalOpen(false)}>
                Cancel
              </button>
              <button className="btn-primary" onClick={submit} disabled={saving}>
                {saving ? 'Saving…' : 'Save'}
              </button>
            </>
          }
        >
          <div className="form-grid">
            {config.fields.map((f) => {
              const disabled = Boolean(f.keyOnCreate && editing);
              const isFull = f.type === 'textarea';
              const options = f.options ?? optionsMap[f.name] ?? [];
              return (
                <div key={f.name} className={`field ${isFull ? 'full' : ''}`}>
                  <label htmlFor={f.name}>
                    {f.label}
                    {f.required ? ' *' : ''}
                  </label>
                  {f.type === 'textarea' ? (
                    <textarea
                      id={f.name}
                      rows={3}
                      value={form[f.name] ?? ''}
                      onChange={(e) => setForm({ ...form, [f.name]: e.target.value })}
                    />
                  ) : f.type === 'select' ? (
                    <select
                      id={f.name}
                      disabled={disabled}
                      value={form[f.name] ?? ''}
                      onChange={(e) => setForm({ ...form, [f.name]: e.target.value })}
                    >
                      <option value="">—</option>
                      {options.map((o) => (
                        <option key={o.value} value={o.value}>
                          {o.label}
                        </option>
                      ))}
                    </select>
                  ) : (
                    <input
                      id={f.name}
                      type={f.type === 'number' ? 'number' : 'text'}
                      step={f.step}
                      disabled={disabled}
                      value={form[f.name] ?? ''}
                      onChange={(e) => setForm({ ...form, [f.name]: e.target.value })}
                    />
                  )}
                </div>
              );
            })}
          </div>
          {formError && <div className="form-error">{formError}</div>}
        </Modal>
      )}
    </div>
  );
}

function formatCell(value: unknown): React.ReactNode {
  if (value === null || value === undefined || value === '') return <span className="muted">—</span>;
  return String(value);
}
