import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ScanText } from 'lucide-react'
import api, { errMsg } from '../services/api'

const num = (v) => (v === '' || v == null ? null : Number(v))

export default function AddAsset() {
  const nav = useNavigate()
  const [f, setF] = useState({
    productName: '', category: 'Appliance', brand: '', model: '', serialNumber: '', purchaseLocation: '',
    purchaseDate: '', purchasePrice: '', replacementCost: '', condition: 'Good', usefulLifeYears: '', warrantyMonths: '',
  })
  const [invoice, setInvoice] = useState('')
  const [note, setNote] = useState('')
  const [err, setErr] = useState('')
  const [busy, setBusy] = useState(false)
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value })

  const extract = async () => {
    setNote(''); setErr('')
    try {
      const { data } = await api.post('/documents/extract', { text: invoice })
      const found = Object.keys(data).length
      setF((p) => ({
        ...p,
        purchaseDate: data.purchaseDate || p.purchaseDate, purchasePrice: data.purchasePrice || p.purchasePrice,
        serialNumber: data.serialNumber || p.serialNumber, model: data.model || p.model,
        purchaseLocation: data.purchaseLocation || p.purchaseLocation,
      }))
      setNote(found ? 'Details filled in below. Please check them before saving.' : 'Nothing could be detected in that text.')
    } catch (e) { setErr(errMsg(e)) }
  }

  const submit = async (e) => {
    e.preventDefault(); setBusy(true); setErr('')
    try {
      const { data } = await api.post('/assets', {
        productName: f.productName, category: f.category, brand: f.brand, model: f.model, serialNumber: f.serialNumber,
        purchaseLocation: f.purchaseLocation, purchaseDate: f.purchaseDate || null, purchasePrice: num(f.purchasePrice),
        replacementCost: num(f.replacementCost), condition: f.condition, usefulLifeYears: num(f.usefulLifeYears),
      })
      if (f.warrantyMonths) await api.post('/warranties', { assetId: data.id, months: Number(f.warrantyMonths), warrantyType: 'Manufacturer', provider: f.brand })
      nav(`/assets/${data.id}`)
    } catch (x) { setErr(errMsg(x)); setBusy(false) }
  }

  const field = (k, label, type = 'text', props = {}) => (
    <div><label className="label">{label}</label><input className="input" type={type} value={f[k]} onChange={set(k)} {...props} /></div>
  )

  return (
    <form onSubmit={submit} className="space-y-6">
      <h1 className="text-2xl font-bold text-navy-900">Add product</h1>
      <div className="card space-y-3">
        <h2 className="font-semibold text-navy-900">Paste invoice text (optional)</h2>
        <p className="text-sm text-slate-500">Paste the text from a bill and we will suggest the date, price, model and seller. You confirm everything before saving.</p>
        <textarea className="input h-28" value={invoice} onChange={(e) => setInvoice(e.target.value)} placeholder={'Reliance Digital\nInvoice No: RD-2291\nDate: 15-11-2023\nModel: RT28\nTotal ₹28,000'} />
        <button type="button" className="btn-ghost" onClick={extract} disabled={!invoice.trim()}><ScanText size={16} /> Detect details</button>
        {note && <p className="text-sm text-violet-700">{note}</p>}
      </div>
      <div className="card grid gap-4 sm:grid-cols-2">
        {field('productName', 'Product name', 'text', { required: true })}
        <div><label className="label">Category</label>
          <select className="input" value={f.category} onChange={set('category')}>{['Appliance', 'Electronics', 'Vehicle', 'Furniture', 'Other'].map((c) => <option key={c}>{c}</option>)}</select></div>
        {field('brand', 'Brand')}{field('model', 'Model')}
        {field('serialNumber', 'Serial number (if any)')}{field('purchaseLocation', 'Bought from')}
        {field('purchaseDate', 'Purchase date', 'date')}{field('purchasePrice', 'Purchase price (₹)', 'number', { min: 0 })}
        {field('replacementCost', 'Price of a new one today (₹)', 'number', { min: 0 })}
        <div><label className="label">Condition</label>
          <select className="input" value={f.condition} onChange={set('condition')}>{['Excellent', 'Good', 'Fair', 'Poor'].map((c) => <option key={c}>{c}</option>)}</select></div>
        {field('usefulLifeYears', 'Expected useful life (years)', 'number', { min: 1 })}
        {field('warrantyMonths', 'Warranty length (months)', 'number', { min: 1 })}
      </div>
      {err && <p className="text-red-600">{err}</p>}
      <button className="btn" disabled={busy}>{busy ? 'Saving…' : 'Save product'}</button>
    </form>
  )
}
