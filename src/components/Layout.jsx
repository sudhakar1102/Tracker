import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { Bell, LayoutDashboard, LogOut, Package, Plus, Wrench } from 'lucide-react'
import { useAuth } from '../context/AuthContext'

const links = [
  { to: '/', label: 'Dashboard', icon: LayoutDashboard, end: true },
  { to: '/assets', label: 'My products', icon: Package },
  { to: '/maintenance', label: 'Maintenance', icon: Wrench },
  { to: '/notifications', label: 'Notifications', icon: Bell },
]

export default function Layout() {
  const { name, logout } = useAuth()
  const nav = useNavigate()
  return (
    <div className="min-h-screen md:flex">
      <aside className="bg-navy-900 text-slate-200 md:fixed md:inset-y-0 md:w-60 md:flex md:flex-col">
        <div className="flex items-center justify-between px-5 py-4 md:block">
          <div className="text-xl font-bold text-white">LifeCycle</div>
          <div className="hidden text-xs text-slate-400 md:block">Own smarter. Maintain better. Waste less.</div>
        </div>
        <nav className="flex gap-1 overflow-x-auto px-3 pb-3 md:flex-1 md:flex-col md:overflow-visible md:pb-0">
          {links.map(({ to, label, icon: Icon, end }) => (
            <NavLink key={to} to={to} end={end}
              className={({ isActive }) => `flex items-center gap-3 whitespace-nowrap rounded-lg px-3 py-2 text-sm font-medium ${isActive ? 'bg-violet-600 text-white' : 'hover:bg-navy-800'}`}>
              <Icon size={18} /> {label}
            </NavLink>
          ))}
          <button onClick={() => nav('/assets/new')} className="btn mt-2 hidden md:flex"><Plus size={16} /> Add product</button>
        </nav>
        <div className="hidden items-center justify-between border-t border-navy-700 px-5 py-4 text-sm md:flex">
          <span className="truncate">{name}</span>
          <button onClick={logout} title="Log out" className="text-slate-400 hover:text-white"><LogOut size={18} /></button>
        </div>
      </aside>
      <main className="flex-1 p-4 md:ml-60 md:p-8">
        <div className="mx-auto max-w-5xl"><Outlet /></div>
      </main>
    </div>
  )
}
