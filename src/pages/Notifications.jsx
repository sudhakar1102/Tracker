import { useEffect, useState } from 'react'
import api, { errMsg } from '../services/api'

export default function Notifications() {
  const [items, setItems] = useState(null)
  const [err, setErr] = useState('')
  const load = () => api.get('/notifications').then((r) => setItems(r.data)).catch((e) => setErr(errMsg(e)))
  useEffect(() => { load() }, [])
  const read = async (id) => { await api.post(`/notifications/${id}/read`); load() }

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold text-navy-900">Notifications</h1>
      {err && <p className="text-red-600">{err}</p>}
      {!items ? <p className="text-slate-500">Loading…</p> : items.length === 0 ? (
        <div className="card text-center text-slate-500">You are all caught up. Warranty and maintenance reminders will show up here.</div>
      ) : items.map((n) => (
        <div key={n.id} className={`card flex items-center justify-between ${n.read ? 'opacity-60' : 'border-violet-200'}`}>
          <div><div className="text-sm">{n.message}</div><div className="text-xs text-slate-400">{new Date(n.createdAt).toLocaleString()}</div></div>
          {!n.read && <button className="btn-ghost" onClick={() => read(n.id)}>Mark read</button>}
        </div>
      ))}
    </div>
  )
}
