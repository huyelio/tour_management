import { useNavigate } from 'react-router-dom'
import Badge from '../common/Badge'
import { formatCurrency, formatDate, getTourStatusColor, getTourStatusLabel } from '../../utils/helpers'

const TourCard = ({ tour }) => {
  const navigate = useNavigate()

  const guidesFilled = tour.assignedGuideCount >= (tour.minGuides || 1)

  return (
    <div
      onClick={() => navigate(`/tours/${tour.id}`)}
      style={{
        background: '#fff',
        borderRadius: '12px',
        border: '1px solid #e5e7eb',
        padding: '20px',
        cursor: 'pointer',
        transition: 'all 0.2s',
        boxShadow: '0 1px 3px rgba(0,0,0,0.06)',
      }}
      onMouseEnter={e => e.currentTarget.style.boxShadow = '0 4px 16px rgba(59,130,246,0.15)'}
      onMouseLeave={e => e.currentTarget.style.boxShadow = '0 1px 3px rgba(0,0,0,0.06)'}
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
        <div style={{ marginTop: '4px' }}>📅 {formatDate(tour.startDate)} → {formatDate(tour.endDate)} ({tour.durationDays} ngày)</div>
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
  )
}

export default TourCard
