import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Search, Trash2 } from 'lucide-react'
import api, { errMsg, money } from '../services/api'

export default function Assets() {
  const [items, setItems] = useState(null)
  const [q, setQ] = useState('')
  const [err, setErr] = useState('')

  const load = () => api.get('/assets', { params: { q } }).then((r) => setItems(r.data)).catch((e) => setErr(errMsg(e)))
  useEffect(() => { const t = setTimeout(load, 250); return () => clearTimeout(t) }, [q])

  const remove = async (a) => {
    if (!confirm(`Delete ${a.productName} and all its records?`)) return
    try { await api.delete(`/assets/${a.id}`); load() } catch (e) { setErr(errMsg(e)) }
  }

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-2xl font-bold text-navy-900">My products</h1>
        <Link to="/assets/new" className="btn">Add product</Link>
      </div>
      <div className="relative">
        <Search size={16} className="absolute left-3 top-3 text-slate-400" />
        <input className="input pl-9" placeholder="Search by name, brand or category" value={q} onChange={(e) => setQ(e.target.value)} />
      </div>
      {err && <p className="text-red-600">{err}</p>}
      {!items ? <p className="text-slate-500">Loading…</p> : items.length === 0 ? (
        <div className="card text-center text-slate-500">{q ? 'No products match your search.' : 'No products yet. Add one to get started.'}</div>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2">
          {items.map((a) => (
            <div key={a.id} className="card flex items-start justify-between">
              <Link to={`/assets/${a.id}`} className="block flex-1">
                <div className="font-semibold text-navy-900">{a.productName}</div>
                <div className="text-sm text-slate-500">{[a.brand, a.model].filter(Boolean).join(' · ') || a.category}</div>
                <div className="mt-2 text-sm">{money(a.purchasePrice)} <span className="text-slate-400">bought {a.purchaseDate || 'date not set'}</span></div>
              </Link>
              <button onClick={() => remove(a)} title="Delete" className="text-slate-400 hover:text-red-600"><Trash2 size={18} /></button>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
