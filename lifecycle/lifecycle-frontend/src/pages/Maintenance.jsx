import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api, { daysLeft, errMsg } from '../services/api'

export default function Maintenance() {
  const [items, setItems] = useState(null)
  const [err, setErr] = useState('')
  const load = () => api.get('/maintenance/upcoming').then((r) => setItems(r.data)).catch((e) => setErr(errMsg(e)))
  useEffect(() => { load() }, [])
  const done = async (id) => { try { await api.post(`/maintenance/${id}/complete`); load() } catch (e) { setErr(errMsg(e)) } }

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold text-navy-900">Maintenance</h1>
      <p className="text-sm text-slate-500">Everything due in the next 90 days, including overdue tasks.</p>
      {err && <p className="text-red-600">{err}</p>}
      {!items ? <p className="text-slate-500">Loading…</p> : items.length === 0 ? (
        <div className="card text-center text-slate-500">Nothing due soon. Add maintenance tasks from a product's page.</div>
      ) : items.map((m) => (
        <div key={m.id} className="card flex items-center justify-between">
          <div>
            <div className="font-semibold text-navy-900">{m.title}</div>
            <div className="text-sm text-slate-500"><Link className="text-violet-600" to={`/assets/${m.assetId}`}>{m.productName}</Link> · {m.nextServiceDate} · {m.source}</div>
          </div>
          <div className="flex items-center gap-3">
            <span className={`rounded-full px-2 py-0.5 text-xs font-semibold ${m.overdue ? 'bg-red-100 text-red-700' : 'bg-violet-100 text-violet-700'}`}>
              {m.overdue ? `Overdue by ${-daysLeft(m.nextServiceDate)} days` : `In ${daysLeft(m.nextServiceDate)} days`}
            </span>
            <button className="btn-ghost" onClick={() => done(m.id)}>Mark done</button>
          </div>
        </div>
      ))}
    </div>
  )
}
