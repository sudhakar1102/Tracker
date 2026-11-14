import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { errMsg } from '../services/api'

export default function Register() {
  const { register } = useAuth()
  const nav = useNavigate()
  const [f, setF] = useState({ name: '', email: '', phone: '', password: '' })
  const [err, setErr] = useState('')
  const [busy, setBusy] = useState(false)
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value })

  const submit = async (e) => {
    e.preventDefault(); setBusy(true); setErr('')
    try { await register(f); nav('/') } catch (x) { setErr(errMsg(x)) } finally { setBusy(false) }
  }
  return (
    <div className="flex min-h-screen items-center justify-center bg-navy-900 p-4">
      <form onSubmit={submit} className="w-full max-w-sm space-y-4 rounded-2xl bg-white p-8">
        <h1 className="text-2xl font-bold text-navy-900">Create your account</h1>
        {err && <p className="rounded-lg bg-red-50 p-2 text-sm text-red-700">{err}</p>}
        <div><label className="label">Full name</label><input className="input" required value={f.name} onChange={set('name')} /></div>
        <div><label className="label">Email</label><input className="input" type="email" required value={f.email} onChange={set('email')} /></div>
        <div><label className="label">Mobile number</label><input className="input" value={f.phone} onChange={set('phone')} /></div>
        <div><label className="label">Password (6+ characters)</label><input className="input" type="password" required minLength={6} value={f.password} onChange={set('password')} /></div>
        <button className="btn w-full" disabled={busy}>{busy ? 'Creating…' : 'Create account'}</button>
        <p className="text-center text-sm text-slate-500">Already registered? <Link className="font-semibold text-violet-600" to="/login">Log in</Link></p>
      </form>
    </div>
  )
}
