import { useState, useEffect, useCallback } from 'react'
import { itineraryApi } from '../../api/itineraryApi'
import ItineraryForm from './ItineraryForm'
import LoadingSpinner from '../common/LoadingSpinner'
import ErrorAlert from '../common/ErrorAlert'

const ItineraryList = ({ tourId, durationDays, tourStatus }) => {
  const [itineraries, setItineraries] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [editingItem, setEditingItem] = useState(null)

  const canEdit = !['COMPLETED', 'CANCELLED'].includes(tourStatus)

  const fetchItineraries = useCallback(async () => {
    try {
      setLoading(true)
      setError(null)
      const res = await itineraryApi.getByTour(tourId)
      setItineraries(res.data || [])
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }, [tourId])

  useEffect(() => { fetchItineraries() }, [fetchItineraries])

  const handleDelete = async (item) => {
    if (!window.confirm(`Bạn có chắc muốn xóa hoạt động "${item.title}"?`)) return
    try {
      await itineraryApi.delete(tourId, item.id)
      await fetchItineraries()
    } catch (err) {
      alert('Lỗi: ' + err.message)
    }
  }

  const handleFormSuccess = () => {
    setShowForm(false)
    setEditingItem(null)
    fetchItineraries()
  }

  // Group by dayNumber
  const grouped = itineraries.reduce((acc, item) => {
    if (!acc[item.dayNumber]) acc[item.dayNumber] = []
    acc[item.dayNumber].push(item)
    return acc
  }, {})
  const sortedDays = Object.keys(grouped).map(Number).sort((a, b) => a - b)

  return (
    <div style={{
      background: '#fff', borderRadius: '12px', padding: '20px',
      border: '1px solid #e5e7eb', marginTop: '20px',
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
        <h3 style={{ margin: 0, fontSize: '16px', fontWeight: 700, color: '#111827' }}>
          📅 Lịch trình tour
          {itineraries.length > 0 && (
            <span style={{ fontWeight: 400, color: '#6b7280', fontSize: '14px', marginLeft: '8px' }}>
              ({itineraries.length} hoạt động / {durationDays} ngày)
            </span>
          )}
        </h3>
        {canEdit && (
          <button
            onClick={() => { setEditingItem(null); setShowForm(true) }}
            style={{
              padding: '8px 16px', background: '#059669', color: '#fff',
              border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '13px', fontWeight: 700,
            }}
          >
            + Thêm hoạt động
          </button>
        )}
      </div>

      {!canEdit && (
        <div style={{
          background: '#fef9c3', border: '1px solid #fde047', borderRadius: '8px',
          padding: '8px 14px', marginBottom: '12px', fontSize: '13px', color: '#854d0e',
        }}>
          🔒 Tour đã {tourStatus === 'COMPLETED' ? 'hoàn thành' : 'bị hủy'} — không thể chỉnh sửa lịch trình
        </div>
      )}

      {loading && <LoadingSpinner text="Đang tải lịch trình..." />}
      {error && <ErrorAlert message={error} onClose={() => setError(null)} />}

      {!loading && !error && itineraries.length === 0 && (
        <div style={{ textAlign: 'center', padding: '40px 20px', color: '#9ca3af' }}>
          <div style={{ fontSize: '40px', marginBottom: '8px' }}>📋</div>
          <p style={{ margin: 0 }}>
            Chưa có lịch trình nào.
            {canEdit && ' Nhấn "+ Thêm hoạt động" để bắt đầu lên lịch.'}
          </p>
        </div>
      )}

      {!loading && sortedDays.map(day => (
        <div key={day} style={{ marginBottom: '20px' }}>
          {/* Day header */}
          <div style={{
            background: 'linear-gradient(135deg, #1e3a8a, #2563eb)',
            color: '#fff', borderRadius: '8px', padding: '8px 16px',
            fontWeight: 700, fontSize: '14px', marginBottom: '8px',
            display: 'flex', alignItems: 'center', gap: '8px',
          }}>
            <span>📆</span>
            <span>Ngày {day}</span>
            <span style={{ fontWeight: 400, opacity: 0.8, fontSize: '12px' }}>
              ({grouped[day].length} hoạt động)
            </span>
          </div>

          {/* Activities */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', paddingLeft: '4px' }}>
            {grouped[day].map((item, idx) => (
              <div key={item.id} style={{
                border: '1px solid #e5e7eb', borderRadius: '8px', padding: '12px 16px',
                background: item.isOptional ? '#f9fafb' : '#fff',
                display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start',
                borderLeft: '3px solid ' + (item.isOptional ? '#d1d5db' : '#2563eb'),
              }}>
                <div style={{ flex: 1, minWidth: 0 }}>
                  {/* Title row */}
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px', flexWrap: 'wrap', marginBottom: '4px' }}>
                    <span style={{ fontSize: '13px', color: '#9ca3af', fontWeight: 500, minWidth: '20px' }}>
                      {idx + 1}.
                    </span>
                    <span style={{ fontWeight: 700, fontSize: '14px', color: '#111827' }}>{item.title}</span>
                    {item.isOptional && (
                      <span style={{ fontSize: '11px', background: '#f3f4f6', color: '#6b7280', padding: '2px 6px', borderRadius: '4px', flexShrink: 0 }}>
                        tùy chọn
                      </span>
                    )}
                    {item.activityType && (
                      <span style={{ fontSize: '11px', background: '#dbeafe', color: '#1d4ed8', padding: '2px 6px', borderRadius: '4px', flexShrink: 0 }}>
                        {item.activityType}
                      </span>
                    )}
                  </div>

                  {/* Meta row */}
                  <div style={{ fontSize: '13px', color: '#6b7280', display: 'flex', gap: '16px', flexWrap: 'wrap', marginBottom: item.description ? '6px' : 0 }}>
                    {(item.startTime || item.endTime) && (
                      <span>🕐 {item.startTime || '?'} – {item.endTime || '?'}</span>
                    )}
                    {item.location && <span>📍 {item.location}</span>}
                  </div>

                  {item.description && (
                    <p style={{ margin: '4px 0 0', fontSize: '13px', color: '#374151', lineHeight: 1.5 }}>{item.description}</p>
                  )}
                  {item.note && (
                    <p style={{ margin: '4px 0 0', fontSize: '12px', color: '#f59e0b', fontStyle: 'italic' }}>
                      📝 {item.note}
                    </p>
                  )}
                </div>

                {/* Action buttons */}
                {canEdit && (
                  <div style={{ display: 'flex', gap: '6px', marginLeft: '12px', flexShrink: 0 }}>
                    <button
                      onClick={() => { setEditingItem(item); setShowForm(true) }}
                      style={{
                        padding: '4px 10px', background: '#eff6ff', color: '#1d4ed8',
                        border: '1px solid #bfdbfe', borderRadius: '6px',
                        cursor: 'pointer', fontSize: '12px', fontWeight: 600,
                      }}
                    >
                      Sửa
                    </button>
                    <button
                      onClick={() => handleDelete(item)}
                      style={{
                        padding: '4px 10px', background: '#fef2f2', color: '#dc2626',
                        border: '1px solid #fecaca', borderRadius: '6px',
                        cursor: 'pointer', fontSize: '12px', fontWeight: 600,
                      }}
                    >
                      Xóa
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      ))}

      {showForm && (
        <ItineraryForm
          tourId={tourId}
          durationDays={durationDays}
          itinerary={editingItem}
          onSuccess={handleFormSuccess}
          onClose={() => { setShowForm(false); setEditingItem(null) }}
        />
      )}
    </div>
  )
}

export default ItineraryList
