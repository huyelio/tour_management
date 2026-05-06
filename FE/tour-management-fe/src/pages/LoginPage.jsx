import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { login } from '../api/authApi'

const inputStyle = {
  width: '100%',
  padding: '10px 14px',
  border: '1px solid #d1d5db',
  borderRadius: '8px',
  fontSize: '14px',
  outline: 'none',
  boxSizing: 'border-box',
  transition: 'border-color 0.2s',
}

const LoginPage = () => {
  const navigate = useNavigate()
  const [form, setForm] = useState({ username: '', password: '' })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const handleChange = (e) =>
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }))

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSuccess('')

    if (!form.username.trim() || !form.password.trim()) {
      setError('Vui lòng nhập đầy đủ Username và Password.')
      return
    }

    setLoading(true)
    try {
      const res = await login(form)
      const user = res.data.data
      setSuccess(`Chào mừng, ${user.username}! Đăng nhập thành công.`)
      // Lưu thông tin user vào sessionStorage để các trang khác dùng
      sessionStorage.setItem('currentUser', JSON.stringify(user))
      setTimeout(() => navigate('/'), 1500)
    } catch (err) {
      setError(err.message || 'Đăng nhập thất bại')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div
      style={{
        minHeight: 'calc(100vh - 60px)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '24px',
      }}
    >
      <div
        style={{
          background: '#fff',
          borderRadius: '16px',
          boxShadow: '0 4px 24px rgba(0,0,0,0.10)',
          padding: '40px 36px',
          width: '100%',
          maxWidth: '420px',
        }}
      >
        <h2
          style={{
            margin: '0 0 6px',
            fontSize: '22px',
            fontWeight: 800,
            color: '#111827',
          }}
        >
          Đăng nhập
        </h2>
        <p style={{ margin: '0 0 28px', color: '#6b7280', fontSize: '14px' }}>
          Nhập thông tin tài khoản để tiếp tục
        </p>

        {error && (
          <div
            style={{
              background: '#fef2f2',
              border: '1px solid #fecaca',
              color: '#dc2626',
              borderRadius: '8px',
              padding: '10px 14px',
              fontSize: '13px',
              marginBottom: '18px',
            }}
          >
            {error}
          </div>
        )}

        {success && (
          <div
            style={{
              background: '#f0fdf4',
              border: '1px solid #bbf7d0',
              color: '#16a34a',
              borderRadius: '8px',
              padding: '10px 14px',
              fontSize: '13px',
              marginBottom: '18px',
            }}
          >
            {success}
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div>
            <label style={{ display: 'block', fontWeight: 600, fontSize: '13px', marginBottom: '6px', color: '#374151' }}>
              Username <span style={{ color: '#ef4444' }}>*</span>
            </label>
            <input
              style={inputStyle}
              name="username"
              value={form.username}
              onChange={handleChange}
              placeholder="Nhập username"
              autoComplete="username"
            />
          </div>

          <div>
            <label style={{ display: 'block', fontWeight: 600, fontSize: '13px', marginBottom: '6px', color: '#374151' }}>
              Password <span style={{ color: '#ef4444' }}>*</span>
            </label>
            <input
              style={inputStyle}
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              placeholder="Nhập password"
              autoComplete="current-password"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            style={{
              marginTop: '8px',
              padding: '11px',
              background: loading ? '#93c5fd' : 'linear-gradient(135deg, #1e3a8a, #2563eb)',
              color: '#fff',
              border: 'none',
              borderRadius: '8px',
              fontWeight: 700,
              fontSize: '15px',
              cursor: loading ? 'not-allowed' : 'pointer',
              transition: 'background 0.2s',
            }}
          >
            {loading ? 'Đang xử lý…' : 'Đăng nhập'}
          </button>
        </form>

        <p style={{ textAlign: 'center', marginTop: '20px', fontSize: '13px', color: '#6b7280' }}>
          Chưa có tài khoản?{' '}
          <Link to="/register" style={{ color: '#2563eb', fontWeight: 600, textDecoration: 'none' }}>
            Đăng ký ngay
          </Link>
        </p>
      </div>
    </div>
  )
}

export default LoginPage
