import { createContext, useContext, useState } from 'react'
import api from '../services/api'

const AuthContext = createContext(null)
export const useAuth = () => useContext(AuthContext)

export function AuthProvider({ children }) {
  const [token, setToken] = useState(localStorage.getItem('lc_token'))
  const [name, setName] = useState(localStorage.getItem('lc_name'))

  const authenticate = async (path, body) => {
    const { data } = await api.post(path, body)
    localStorage.setItem('lc_token', data.token)
    localStorage.setItem('lc_name', data.name)
    setToken(data.token)
    setName(data.name)
  }
  const logout = () => {
    localStorage.removeItem('lc_token')
    localStorage.removeItem('lc_name')
    setToken(null)
    setName(null)
  }

  return (
    <AuthContext.Provider value={{ token, name, login: (b) => authenticate('/auth/login', b), register: (b) => authenticate('/auth/register', b), logout }}>
      {children}
    </AuthContext.Provider>
  )
}
