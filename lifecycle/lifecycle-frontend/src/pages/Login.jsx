import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { errMsg } from '../services/api'

export default function Login() {
  const { login } = useAuth()
  const nav = useNavigate()
  const [f, setF] = useState({ email: '', password: '' })
  const [err, setErr] = useState('')
  const [busy, setBusy] = useState(false)

  const submit = async (e) => {
    e.preventDefault(); setBusy(true); setErr('')
    try { await login(f); nav('/') } catch (x) { setErr(errMsg(x)) } finally { setBusy(false) }
  }
  return (
    <div className="flex min-h-screen items-center justify-center bg-navy-900 p-4">
      <form onSubmit={submit} className="w-full max-w-sm space-y-4 rounded-2xl bg-white p-8">
        <div><h1 className="text-2xl font-bold text-navy-900">Log in to LifeCycle</h1>
          <p className="text-sm text-slate-500">See what you own and what needs attention next.</p></div>
        {err && <p className="rounded-lg bg-red-50 p-2 text-sm text-red-700">{err}</p>}
        <div><label className="label">Email</label><input className="input" type="email" required value={f.email} onChange={(e) => setF({ ...f, email: e.target.value })} /></div>
        <div><label className="label">Password</label><input className="input" type="password" required value={f.password} onChange={(e) => setF({ ...f, password: e.target.value })} /></div>
        <button className="btn w-full" disabled={busy}>{busy ? 'Logging in…' : 'Log in'}</button>
        <p className="text-center text-sm text-slate-500">New here? <Link className="font-semibold text-violet-600" to="/register">Create an account</Link></p>
        <p className="rounded-lg bg-violet-50 p-2 text-xs text-slate-600">Sample account: demo@lifecycle.app / demo1234 (sample data only)</p>
      </form>
    </div>
  )
}
