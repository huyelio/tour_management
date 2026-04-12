import { useState, useEffect } from 'react'
import { tourApi } from '../../api/tourApi'
import TourCard from './TourCard'
import LoadingSpinner from '../common/LoadingSpinner'
import ErrorAlert from '../common/ErrorAlert'

const TourList = () => {
  const [tours, setTours] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [activeOnly, setActiveOnly] = useState(false)
  const [search, setSearch] = useState('')

  useEffect(() => {
    fetchTours()
  }, [activeOnly])

  const fetchTours = async () => {
    try {
      setLoading(true)
      setError(null)
      const res = await tourApi.getAll(activeOnly)
      setTours(res.data || [])
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const filtered = tours.filter(t =>
    t.name.toLowerCase().includes(search.toLowerCase()) ||
    t.destination.toLowerCase().includes(search.toLowerCase()) ||
    t.code.toLowerCase().includes(search.toLowerCase())
  )

  return (
    <div>
      {/* Thanh tìm kiếm và bộ lọc */}
      <div style={{ display: 'flex', gap: '12px', marginBottom: '20px', flexWrap: 'wrap' }}>
        <input
          type="text"
          placeholder="🔍 Tìm kiếm tour..."
          value={search}
          onChange={e => setSearch(e.target.value)}
          style={{
            flex: 1, minWidth: '200px', padding: '10px 14px',
            border: '1px solid #d1d5db', borderRadius: '8px', fontSize: '14px',
            outline: 'none',
          }}
        />
        <label style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', fontSize: '14px', color: '#374151' }}>
          <input
            type="checkbox"
            checked={activeOnly}
            onChange={e => setActiveOnly(e.target.checked)}
          />
          Chỉ tour đang hoạt động
        </label>
        <button
          onClick={fetchTours}
          style={{
            padding: '10px 18px', background: '#3b82f6', color: '#fff',
            border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '14px', fontWeight: 600,
          }}
        >
          🔄 Tải lại
        </button>
      </div>

      {error && <ErrorAlert message={error} onClose={() => setError(null)} />}

      {loading ? (
        <LoadingSpinner text="Đang tải danh sách tour..." />
      ) : filtered.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '60px', color: '#9ca3af' }}>
          <div style={{ fontSize: '48px' }}>🗺️</div>
          <p>Không tìm thấy tour nào</p>
        </div>
      ) : (
        <>
          <p style={{ fontSize: '14px', color: '#6b7280', marginBottom: '12px' }}>
            Tìm thấy <strong>{filtered.length}</strong> tour
          </p>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(340px, 1fr))', gap: '16px' }}>
            {filtered.map(tour => (
              <TourCard key={tour.id} tour={tour} />
            ))}
          </div>
        </>
      )}
    </div>
  )
}

export default TourList
