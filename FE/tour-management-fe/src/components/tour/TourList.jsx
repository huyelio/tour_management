import { useState, useEffect } from 'react'
import { tourApi } from '../../api/tourApi'
import TourCard from './TourCard'
import TourForm from './TourForm'
import LoadingSpinner from '../common/LoadingSpinner'
import ErrorAlert from '../common/ErrorAlert'
import SuccessAlert from '../common/SuccessAlert'

const TOUR_STATUSES = [
  { value: '', label: 'Tất cả trạng thái' },
  { value: 'PLANNING', label: '📋 Lên kế hoạch' },
  { value: 'OPEN', label: '✅ Mở bán' },
  { value: 'FULL', label: '🔵 Đã đủ khách' },
  { value: 'ONGOING', label: '🟣 Đang diễn ra' },
  { value: 'COMPLETED', label: '⚫ Hoàn thành' },
  { value: 'CANCELLED', label: '🔴 Đã hủy' },
]

const TourList = () => {
  const [tours, setTours] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(null)
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState('')
  const [assignmentFilter, setAssignmentFilter] = useState('all')
  const [showForm, setShowForm] = useState(false)
  const [editingTour, setEditingTour] = useState(null)

  useEffect(() => { fetchTours() }, [])

  const fetchTours = async () => {
    try {
      setLoading(true)
      setError(null)
      const res = await tourApi.getAll()
      setTours(res.data || [])
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const handleEdit = (tour) => {
    setEditingTour(tour)
    setShowForm(true)
  }

  const handleCancel = async (tourId, tourName) => {
    if (!window.confirm(`Bạn có chắc muốn hủy tour "${tourName}"?\nHành động này không thể hoàn tác.`)) return
    try {
      await tourApi.cancel(tourId)
      setSuccess(`Đã hủy tour "${tourName}" thành công`)
      await fetchTours()
    } catch (err) {
      setError(err.message)
    }
  }

  const handleFormSuccess = async () => {
    const msg = editingTour ? 'Cập nhật tour thành công!' : 'Tạo tour mới thành công!'
    setShowForm(false)
    setEditingTour(null)
    setSuccess(msg)
    await fetchTours()
  }

  const filtered = tours.filter(t => {
    const q = search.toLowerCase()
    const matchSearch = !q
      || t.name.toLowerCase().includes(q)
      || t.destination.toLowerCase().includes(q)
      || t.code.toLowerCase().includes(q)
    const matchStatus = !statusFilter || t.status === statusFilter
    const matchAssignment =
      assignmentFilter === 'all'
      || (assignmentFilter === 'assigned' && t.assignedGuideCount > 0)
      || (assignmentFilter === 'unassigned' && t.assignedGuideCount === 0)
    return matchSearch && matchStatus && matchAssignment
  })

  return (
    <div>
      {/* Toolbar */}
      <div style={{ display: 'flex', gap: '10px', marginBottom: '20px', flexWrap: 'wrap', alignItems: 'center' }}>
        <input
          type="text"
          placeholder="🔍 Tìm theo mã, tên, điểm đến..."
          value={search}
          onChange={e => setSearch(e.target.value)}
          style={{
            flex: 1, minWidth: '200px', padding: '10px 14px',
            border: '1px solid #d1d5db', borderRadius: '8px', fontSize: '14px', outline: 'none',
          }}
        />
        <select
          value={statusFilter}
          onChange={e => setStatusFilter(e.target.value)}
          style={{ padding: '10px 12px', border: '1px solid #d1d5db', borderRadius: '8px', fontSize: '14px', background: '#fff', cursor: 'pointer', outline: 'none' }}
        >
          {TOUR_STATUSES.map(s => <option key={s.value} value={s.value}>{s.label}</option>)}
        </select>
        <select
          value={assignmentFilter}
          onChange={e => setAssignmentFilter(e.target.value)}
          style={{ padding: '10px 12px', border: '1px solid #d1d5db', borderRadius: '8px', fontSize: '14px', background: '#fff', cursor: 'pointer', outline: 'none' }}
        >
          <option value="all">🎫 Tất cả HDV</option>
          <option value="assigned">✅ Đã phân công</option>
          <option value="unassigned">⚠ Chưa phân công</option>
        </select>
        <button
          onClick={fetchTours}
          title="Tải lại"
          style={{ padding: '10px 14px', background: '#6b7280', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '14px' }}
        >
          🔄
        </button>
        <button
          onClick={() => { setEditingTour(null); setShowForm(true) }}
          style={{ padding: '10px 18px', background: '#1d4ed8', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '14px', fontWeight: 700, whiteSpace: 'nowrap' }}
        >
          + Thêm tour
        </button>
      </div>

      {success && <SuccessAlert message={success} onClose={() => setSuccess(null)} />}
      {error && <ErrorAlert message={error} onClose={() => setError(null)} />}

      {loading ? (
        <LoadingSpinner text="Đang tải danh sách tour..." />
      ) : filtered.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '60px', color: '#9ca3af' }}>
          <div style={{ fontSize: '48px' }}>🗺️</div>
          <p style={{ marginTop: '12px' }}>
            {tours.length === 0 ? 'Chưa có tour nào. Nhấn "+ Thêm tour" để tạo mới.' : 'Không tìm thấy tour phù hợp.'}
          </p>
        </div>
      ) : (
        <>
          <p style={{ fontSize: '14px', color: '#6b7280', marginBottom: '12px' }}>
            Tìm thấy <strong>{filtered.length}</strong> / {tours.length} tour
          </p>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(340px, 1fr))', gap: '16px' }}>
            {filtered.map(tour => (
              <TourCard key={tour.id} tour={tour} onEdit={handleEdit} onCancel={handleCancel} />
            ))}
          </div>
        </>
      )}

      {showForm && (
        <TourForm
          tour={editingTour}
          onSuccess={handleFormSuccess}
          onClose={() => { setShowForm(false); setEditingTour(null) }}
        />
      )}
    </div>
  )
}

export default TourList
