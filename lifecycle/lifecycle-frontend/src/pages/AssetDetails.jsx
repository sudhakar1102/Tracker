import { useCallback, useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import api, { daysLeft, errMsg, money } from '../services/api'

function Section({ title, children }) {
  return <section className="card space-y-3"><h2 className="font-semibold text-navy-900">{title}</h2>{children}</section>
}
const Row = ({ k, v }) => <div className="flex justify-between text-sm"><span className="text-slate-500">{k}</span><span className="font-medium">{v}</span></div>
const today = () => new Date().toISOString().slice(0, 10)

export default function AssetDetails() {
  const { id } = useParams()
  const [d, setD] = useState(null)
  const [err, setErr] = useState('')
  const [rec, setRec] = useState(null)
  const [repairCost, setRepairCost] = useState('')
  const [suggest, setSuggest] = useState([])
  const [w, setW] = useState({ months: '', warrantyType: 'Manufacturer', provider: '' })
  const [m, setM] = useState({ title: '', frequencyDays: '' })
  const [s, setS] = useState({ serviceType: '', serviceProvider: '', repairCost: '', serviceDate: today() })
  const [x, setX] = useState({ expenseType: 'Maintenance', amount: '', description: '', expenseDate: today() })

  const load = useCallback(async () => {
    try {
      const [asset, warranties, maint, services, expenses, valuation, costs] = await Promise.all(
        ['', '/warranty', '/maintenance', '/services', '/expenses', '/valuation', '/costs'].map((p) => api.get(`/assets/${id}${p}`)))
      setD({ asset: asset.data, warranties: warranties.data, maint: maint.data, services: services.data, expenses: expenses.data, val: valuation.data, costs: costs.data })
    } catch (e) { setErr(errMsg(e)) }
  }, [id])
  useEffect(() => { load() }, [load])

  const act = (fn) => async (e) => { e?.preventDefault(); setErr(''); try { await fn(); await load() } catch (x2) { setErr(errMsg(x2)) } }

  if (err && !d) return <p className="text-red-600">{err}</p>
  if (!d) return <p className="text-slate-500">Loading…</p>
  const { asset: a, val, costs } = d

  return (
    <div className="space-y-5">
      <div>
        <h1 className="text-2xl font-bold text-navy-900">{a.productName}</h1>
        <p className="text-sm text-slate-500">{[a.brand, a.model, a.category].filter(Boolean).join(' · ')} {a.serialNumber && `· Serial ${a.serialNumber}`}</p>
      </div>
      {err && <p className="rounded-lg bg-red-50 p-2 text-sm text-red-700">{err}</p>}

      <div className="grid gap-4 md:grid-cols-2">
        <Section title="Value">
          <Row k="Bought for" v={money(val.purchasePrice)} />
          <Row k="Estimated value today" v={money(val.currentValue)} />
          <Row k="Estimated depreciation" v={money(val.depreciation)} />
          <Row k="Age" v={`${val.ageYears} years`} />
          <p className="text-xs text-slate-400">Estimate from a {Math.round(val.annualRate * 100)}% yearly rule for this category and the product's condition ({a.condition || 'Good'}).</p>
        </Section>
        <Section title="Total ownership cost">
          <Row k="Purchase" v={money(costs.purchase)} />
          <Row k="Repairs" v={money(costs.repairs)} />
          <Row k="Other expenses" v={money(costs.otherExpenses)} />
          <div className="border-t pt-2"><Row k="Total" v={money(costs.total)} /></div>
        </Section>
      </div>

      <Section title="Warranty">
        {d.warranties.length === 0 && <p className="text-sm text-slate-500">No warranty recorded yet.</p>}
        {d.warranties.map((r) => {
          const left = daysLeft(r.expiryDate)
          return (
            <div key={r.id} className="flex items-center justify-between rounded-lg bg-slate-50 p-3 text-sm">
              <span>{r.warrantyType}{r.provider && ` · ${r.provider}`} · ends {r.expiryDate}</span>
              <span className={`rounded-full px-2 py-0.5 text-xs font-semibold ${left < 0 ? 'bg-slate-200 text-slate-600' : left <= 30 ? 'bg-amber-100 text-amber-800' : 'bg-violet-100 text-violet-700'}`}>
                {left < 0 ? 'Expired' : `${left} days left`}
              </span>
            </div>
          )
        })}
        <form className="flex flex-wrap gap-2" onSubmit={act(async () => { await api.post('/warranties', { assetId: Number(id), months: Number(w.months), warrantyType: w.warrantyType, provider: w.provider }); setW({ ...w, months: '' }) })}>
          <select className="input w-auto" value={w.warrantyType} onChange={(e) => setW({ ...w, warrantyType: e.target.value })}><option>Manufacturer</option><option>Extended</option></select>
          <input className="input w-36" type="number" min="1" required placeholder="Months" value={w.months} onChange={(e) => setW({ ...w, months: e.target.value })} />
          <input className="input w-40" placeholder="Provider" value={w.provider} onChange={(e) => setW({ ...w, provider: e.target.value })} />
          <button className="btn">Add warranty</button>
        </form>
      </Section>

      <Section title="Maintenance">
        {d.maint.length === 0 && <p className="text-sm text-slate-500">No maintenance schedule yet.</p>}
        {d.maint.map((r) => (
          <div key={r.id} className="flex items-center justify-between rounded-lg bg-slate-50 p-3 text-sm">
            <span><b>{r.title}</b> · every {r.frequencyDays} days · next {r.nextServiceDate} <span className="text-xs text-slate-400">({r.source})</span></span>
            <button className="btn-ghost" onClick={act(() => api.post(`/maintenance/${r.id}/complete`))}>Mark done</button>
          </div>
        ))}
        <form className="flex flex-wrap gap-2" onSubmit={act(async () => { await api.post('/maintenance', { assetId: Number(id), title: m.title, frequencyDays: Number(m.frequencyDays) }); setM({ title: '', frequencyDays: '' }) })}>
          <input className="input w-52" required placeholder="Task, e.g. Clean filters" value={m.title} onChange={(e) => setM({ ...m, title: e.target.value })} />
          <input className="input w-36" type="number" min="1" required placeholder="Every N days" value={m.frequencyDays} onChange={(e) => setM({ ...m, frequencyDays: e.target.value })} />
          <button className="btn">Add task</button>
          <button type="button" className="btn-ghost" onClick={act(async () => setSuggest((await api.get(`/assets/${id}/maintenance/suggestions`)).data))}>Suggest tasks</button>
        </form>
        {suggest.length > 0 && (
          <div className="space-y-1 text-sm">
            <p className="text-xs text-slate-500">General suggestions for this category. These are not from the manufacturer, so check your manual.</p>
            {suggest.map((sg) => (
              <button key={sg.title} className="btn-ghost mr-2" onClick={act(() => api.post('/maintenance', { assetId: Number(id), title: sg.title, frequencyDays: sg.frequencyDays, source: 'General recommendation' }))}>
                + {sg.title} ({sg.frequencyDays}d)
              </button>
            ))}
          </div>
        )}
      </Section>

      <Section title="Service and repair history">
        {d.services.length === 0 ? <p className="text-sm text-slate-500">No repairs or services recorded.</p> : (
          <table className="w-full text-left text-sm">
            <thead className="text-xs text-slate-500"><tr><th>Date</th><th>Work done</th><th>Provider</th><th className="text-right">Cost</th></tr></thead>
            <tbody>{d.services.map((r) => <tr key={r.id} className="border-t"><td className="py-1.5">{r.serviceDate}</td><td>{r.serviceType}</td><td>{r.serviceProvider}</td><td className="text-right">{money(r.repairCost)}</td></tr>)}</tbody>
          </table>
        )}
        <form className="flex flex-wrap gap-2" onSubmit={act(async () => { await api.post('/services', { assetId: Number(id), serviceType: s.serviceType, serviceProvider: s.serviceProvider, serviceDate: s.serviceDate, repairCost: Number(s.repairCost || 0) }); setS({ ...s, serviceType: '', serviceProvider: '', repairCost: '' }) })}>
          <input className="input w-40" type="date" value={s.serviceDate} onChange={(e) => setS({ ...s, serviceDate: e.target.value })} />
          <input className="input w-48" required placeholder="Work done" value={s.serviceType} onChange={(e) => setS({ ...s, serviceType: e.target.value })} />
          <input className="input w-40" placeholder="Service centre" value={s.serviceProvider} onChange={(e) => setS({ ...s, serviceProvider: e.target.value })} />
          <input className="input w-28" type="number" min="0" placeholder="Cost ₹" value={s.repairCost} onChange={(e) => setS({ ...s, repairCost: e.target.value })} />
          <button className="btn">Add record</button>
        </form>
      </Section>

      <Section title="Other expenses">
        {d.expenses.map((r) => <Row key={r.id} k={`${r.expenseDate} · ${r.expenseType}${r.description ? ' · ' + r.description : ''}`} v={money(r.amount)} />)}
        <form className="flex flex-wrap gap-2" onSubmit={act(async () => { await api.post('/expenses', { assetId: Number(id), expenseType: x.expenseType, amount: Number(x.amount), description: x.description, expenseDate: x.expenseDate }); setX({ ...x, amount: '', description: '' }) })}>
          <select className="input w-auto" value={x.expenseType} onChange={(e) => setX({ ...x, expenseType: e.target.value })}>{['Maintenance', 'Accessories', 'Insurance', 'Other'].map((t) => <option key={t}>{t}</option>)}</select>
          <input className="input w-28" type="number" min="0" required placeholder="Amount ₹" value={x.amount} onChange={(e) => setX({ ...x, amount: e.target.value })} />
          <input className="input w-48" placeholder="Note" value={x.description} onChange={(e) => setX({ ...x, description: e.target.value })} />
          <button className="btn">Add expense</button>
        </form>
      </Section>

      <Section title="Repair or replace?">
        <p className="text-sm text-slate-500">Enter a repair quote to compare it with this product's value and the cost of a new one.</p>
        <form className="flex gap-2" onSubmit={act(async () => setRec((await api.get(`/assets/${id}/recommendation`, { params: { repairCost } })).data))}>
          <input className="input w-40" type="number" min="0" required placeholder="Repair quote ₹" value={repairCost} onChange={(e) => setRepairCost(e.target.value)} />
          <button className="btn">Compare</button>
        </form>
        {rec && (
          <div className="rounded-lg border border-violet-200 bg-violet-50 p-4 text-sm">
            <div className="text-lg font-bold text-navy-900">{rec.verdict}</div>
            <p className="mt-1">{rec.explanation}</p>
            <div className="mt-3 space-y-1">
              <Row k="Repair quote" v={money(rec.repairCost)} /><Row k="Estimated value today" v={money(rec.currentValue)} />
              <Row k="New replacement" v={money(rec.replacementCost)} /><Row k="Spent on repairs so far" v={money(rec.previousRepairs)} />
              <Row k="Estimated useful life left" v={`${rec.remainingLifeYears} years`} />
            </div>
            <p className="mt-3 text-xs text-slate-500">{rec.disclaimer}</p>
          </div>
        )}
      </Section>
    </div>
  )
}
