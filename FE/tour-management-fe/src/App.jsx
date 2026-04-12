import { BrowserRouter, Routes, Route, NavLink } from 'react-router-dom'
import TourListPage from './pages/TourListPage'
import TourDetailPage from './pages/TourDetailPage'

const navLinkStyle = ({ isActive }) => ({
  color: isActive ? '#fff' : 'rgba(255,255,255,0.7)',
  textDecoration: 'none',
  fontWeight: isActive ? 700 : 400,
  padding: '6px 14px',
  borderRadius: '6px',
  background: isActive ? 'rgba(255,255,255,0.15)' : 'transparent',
  fontSize: '14px',
  transition: 'all 0.2s',
})

const App = () => (
  <BrowserRouter>
    {/* Navigation Header */}
    <header style={{
      background: 'linear-gradient(135deg, #0f172a, #1e3a8a)',
      color: '#fff', padding: '0 24px',
      display: 'flex', alignItems: 'center', gap: '24px',
      height: '60px', boxShadow: '0 2px 8px rgba(0,0,0,0.2)',
      position: 'sticky', top: 0, zIndex: 100,
    }}>
      <div style={{ fontWeight: 800, fontSize: '18px', letterSpacing: '-0.5px', marginRight: '16px' }}>
        ✈️ TourManager
      </div>
      <nav style={{ display: 'flex', gap: '4px' }}>
        <NavLink to="/" end style={navLinkStyle}>🗺️ Danh sách Tour</NavLink>
      </nav>
    </header>

    {/* Main Content */}
    <main style={{
      minHeight: 'calc(100vh - 60px)',
      background: '#f8fafc',
      padding: '28px 24px',
    }}>
      <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
        <Routes>
          <Route path="/" element={<TourListPage />} />
          <Route path="/tours/:id" element={<TourDetailPage />} />
        </Routes>
      </div>
    </main>
  </BrowserRouter>
)

export default App
