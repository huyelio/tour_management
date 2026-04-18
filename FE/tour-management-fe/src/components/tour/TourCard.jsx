import { useNavigate } from 'react-router-dom'
import Badge from '../common/Badge'
import { formatCurrency, formatDate, getTourStatusColor, getTourStatusLabel } from '../../utils/helpers'

const TourCard = ({ tour, onEdit, onCancel }) => {
  const navigate = useNavigate()
  const guidesFilled = tour.assignedGuideCount >= (tour.minGuides || 1)
  const isCancelled = tour.status === 'CANCELLED'
  const isCompleted = tour.status === 'COMPLETED'
  const locked = isCancelled || isCompleted

  return (
    <div style={{
      background: '#fff', borderRadius: '12px', border: '1px solid #e5e7eb',
      overflow: 'hidden', boxShadow: '0 1px 3px rgba(0,0,0,0.06)',
      transition: 'box-shadow 0.2s', opacity: isCancelled ? 0.72 : 1,
    }}
      onMouseEnter={e => e.currentTarget.style.boxShadow = '0 4px 16px rgba(59,130,246,0.15)'}
      onMouseLeave={e => e.currentTarget.style.boxShadow = '0 1px 3px rgba(0,0,0,0.06)'}
    >
      {/* Clickable card body */}
      <div
        onClick={() => navigate(`/tours/${tour.id}`)}
        style={{ padding: '20px', cursor: 'pointer' }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '12px' }}>
          <div>
            <span style={{ fontSize: '12px', color: '#9ca3af', fontWeight: 500 }}>{tour.code}</span>
            <h3 style={{ margin: '4px 0 0', fontSize: '16px', fontWeight: 700, color: '#111827' }}>{tour.name}</h3>
          </div>
          <Badge label={getTourStatusLabel(tour.status)} color={getTourStatusColor(tour.status)} />
        </div>

        <div style={{ color: '#6b7280', fontSize: '14px', marginBottom: '12px' }}>
          <div>📍 {tour.destination}</div>
          <div style={{ marginTop: '4px' }}>
            📅 {formatDate(tour.startDate)} → {formatDate(tour.endDate)} ({tour.durationDays} ngày)
          </div>
        </div>

        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: '1px solid #f3f4f6', paddingTop: '12px' }}>
          <div style={{ fontSize: '14px', color: '#374151' }}>
            👥 {tour.currentGuests}/{tour.maxGuests} khách
          </div>
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontSize: '14px', color: guidesFilled ? '#10b981' : '#f59e0b', fontWeight: 600 }}>
              🎫 HDV: {tour.assignedGuideCount}/{tour.minGuides || 1}
              {guidesFilled ? ' ✓' : ' ⚠'}
            </div>
            <div style={{ fontSize: '15px', fontWeight: 700, color: '#1d4ed8' }}>{formatCurrency(tour.price)}</div>
          </div>
        </div>
      </div>

      {/* Action buttons — stopPropagation to avoid card click */}
      <div
        onClick={e => e.stopPropagation()}
        style={{ padding: '10px 16px', borderTop: '1px solid #f3f4f6', display: 'flex', gap: '8px', background: '#f9fafb' }}
      >
        <button
          onClick={() => onEdit && onEdit(tour)}
          disabled={locked}
          title={isCompleted ? 'Tour đã hoàn thành' : isCancelled ? 'Tour đã bị hủy' : 'Sửa tour'}
          style={{
            flex: 1, padding: '6px 10px', fontSize: '13px', fontWeight: 600,
            background: locked ? '#f3f4f6' : '#eff6ff',
            color: locked ? '#9ca3af' : '#1d4ed8',
            border: '1px solid ' + (locked ? '#e5e7eb' : '#bfdbfe'),
            borderRadius: '6px', cursor: locked ? 'not-allowed' : 'pointer',
          }}
        >
          ✏️ Sửa
        </button>
        <button
          onClick={() => onCancel && onCancel(tour.id, tour.name)}
          disabled={locked}
          title={isCompleted ? 'Không thể hủy tour đã hoàn thành' : isCancelled ? 'Tour đã bị hủy' : 'Hủy tour'}
          style={{
            flex: 1, padding: '6px 10px', fontSize: '13px', fontWeight: 600,
            background: locked ? '#f3f4f6' : '#fef2f2',
            color: locked ? '#9ca3af' : '#dc2626',
            border: '1px solid ' + (locked ? '#e5e7eb' : '#fecaca'),
            borderRadius: '6px', cursor: locked ? 'not-allowed' : 'pointer',
          }}
        >
          🚫 Hủy
        </button>
      </div>
    </div>
  )
}

export default TourCard
