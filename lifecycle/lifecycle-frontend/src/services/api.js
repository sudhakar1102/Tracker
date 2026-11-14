import axios from 'axios'

const api = axios.create({ baseURL: '/api' })

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('lc_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  (r) => r,
  (err) => {
    if (err.response?.status === 401 && !err.config.url.startsWith('/auth/')) {
      localStorage.removeItem('lc_token')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

export const errMsg = (e) => e.response?.data?.message || 'Something went wrong. Please try again.'
export const money = (v) => '₹' + Number(v || 0).toLocaleString('en-IN', { maximumFractionDigits: 0 })
export const daysLeft = (date) => Math.ceil((new Date(date) - new Date().setHours(0, 0, 0, 0)) / 86400000)

export default api
