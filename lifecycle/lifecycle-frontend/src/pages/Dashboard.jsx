import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Bar, BarChart, CartesianGrid, Cell, Legend, Pie, PieChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import api, { daysLeft, errMsg, money } from '../services/api'

const COLORS = ['#7c5cff', '#22336a', '#a99bff', '#4f6bb5', '#c9c0ff']

function Stat({ label, value, tone }) {
  return (
    <div className="card">
      <div className={`text-3xl font-bold ${tone || 'text-navy-900'}`}>{value}</div>
      <div className="mt-1 text-sm text-slate-500">{label}</div>
    </div>
  )
}

export default function Dashboard() {
  const [s, setS] = useState(null)
  const [expiring, setExpiring] = useState([])
  const [err, setErr] = useState('')

  useEffect(() => {
    Promise.all([api.get('/dashboard/summary'), api.get('/warranties/expiring')])
      .then(([a, b]) => { setS(a.data); setExpiring(b.data) })
      .catch((e) => setErr(errMsg(e)))
  }, [])

  if (err) return <p className="text-red-600">{err}</p>
  if (!s) return <p className="text-slate-500">Loading your dashboard…</p>

  const cat = Object.entries(s.costByCategory).map(([name, value]) => ({ name, value: Number(value) }))
  const prod = s.costByProduct.map((p) => ({ name: p.name, Purchase: Number(p.purchase), 'Repairs & other': Number(p.running) }))

  if (s.totalProducts === 0)
    return (
      <div className="card text-center">
        <h1 className="text-xl font-bold text-navy-900">Add your first product</h1>
        <p className="mx-auto mt-1 max-w-md text-sm text-slate-500">Register something you own to start tracking its warranty, maintenance, repairs and value.</p>
        <Link to="/assets/new" className="btn mt-4">Add product</Link>
      </div>
    )

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-navy-900">Dashboard</h1>
      <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
        <Stat label="Products" value={s.totalProducts} />
        <Stat label="Active warranties" value={s.activeWarranties} />
        <Stat label="Maintenance due in 30 days" value={s.upcomingMaintenance} />
        <Stat label="Total ownership cost" value={money(s.totalOwnershipCost)} />
      </div>
      {(s.overdueMaintenance > 0 || expiring.length > 0) && (
        <div className="card border-violet-200 bg-violet-50">
          <h2 className="font-semibold text-navy-900">Needs attention</h2>
          <ul className="mt-2 space-y-1 text-sm">
            {s.overdueMaintenance > 0 && <li><Link className="text-violet-600 underline" to="/maintenance">{s.overdueMaintenance} overdue maintenance task(s)</Link></li>}
            {expiring.map((w) => (
              <li key={w.assetId + w.expiryDate}><Link className="text-violet-600 underline" to={`/assets/${w.assetId}`}>{w.productName}</Link> warranty ends in {daysLeft(w.expiryDate)} days ({w.expiryDate})</li>
            ))}
          </ul>
        </div>
      )}
      <div className="grid gap-4 lg:grid-cols-2">
        <div className="card">
          <h2 className="mb-2 font-semibold text-navy-900">Cost by product</h2>
          <ResponsiveContainer width="100%" height={260}>
            <BarChart data={prod}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} />
              <XAxis dataKey="name" tick={{ fontSize: 11 }} /><YAxis tick={{ fontSize: 11 }} />
              <Tooltip formatter={(v) => money(v)} /><Legend />
              <Bar dataKey="Purchase" stackId="a" fill="#22336a" />
              <Bar dataKey="Repairs & other" stackId="a" fill="#7c5cff" />
            </BarChart>
          </ResponsiveContainer>
        </div>
        <div className="card">
          <h2 className="mb-2 font-semibold text-navy-900">Cost by category</h2>
          <ResponsiveContainer width="100%" height={260}>
            <PieChart>
              <Pie data={cat} dataKey="value" nameKey="name" outerRadius={90} label={(d) => d.name}>
                {cat.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
              </Pie>
              <Tooltip formatter={(v) => money(v)} />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  )
}
