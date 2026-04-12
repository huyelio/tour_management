import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { tourApi } from '../api/tourApi'
import AssignmentForm from '../components/assignment/AssignmentForm'
import AssignmentList from '../components/assignment/AssignmentList'
import Badge from '../components/common/Badge'
import LoadingSpinner from '../components/common/LoadingSpinner'
import ErrorAlert from '../components/common/ErrorAlert'
import { formatCurrency, formatDate, getTourStatusColor, getTourStatusLabel } from '../utils/helpers'

const InfoRow = ({ label, value }) => (
  <div style={{ display: 'flex', gap: '12px', padding: '8px 0', borderBottom: '1px solid #f3f4f6' }}>
    <span style={{ color: '#9ca3af', fontSize: '13px', minWidth: '160px' }}>{label}</span>
    <span style={{ color: '#111827', fontSize: '14px', fontWeight: 500 }}>{value || '—'}</span>
  </div>
)

const TourDetailPage = () => {
  const { id } = useParams()
  const navigate = useNavigate()

  const [tour, setTour] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [showAssignForm, setShowAssignForm] = useState(false)

  useEffect(() => {
    fetchTour()
  }, [id])

  const fetchTour = async () => {
    try {
      setLoading(true)
      setError(null)
      const res = await tourApi.getById(id)
      setTour(res.data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  if (loading) return <LoadingSpinner text="Đang tải thông tin tour..." />
  if (error) return <ErrorAlert message={error} />
  if (!tour) return null

  const canAssign = !['COMPLETED', 'CANCELLED'].includes(tour.status)

  return (
    <div style={{ maxWidth: '900px', margin: '0 auto' }}>
      {/* Nút quay lại */}
      <button
        onClick={() => navigate('/')}
        style={{
          background: 'none', border: '1px solid #d1d5db', borderRadius: '8px',
          padding: '8px 16px', cursor: 'pointer', fontSize: '14px', color: '#374151',
          marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '6px',
        }}
      >
        ← Quay lại danh sách tour
      </button>

      {/* Header tour */}
      <div style={{
        background: 'linear-gradient(135deg, #1e3a8a, #2563eb)',
        borderRadius: '16px', padding: '28px', color: '#fff', marginBottom: '24px',
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '12px' }}>
          <div>
            <div style={{ opacity: 0.7, fontSize: '13px', marginBottom: '6px' }}>{tour.code}</div>
            <h1 style={{ margin: 0, fontSize: '22px', fontWeight: 800 }}>{tour.name}</h1>
            <div style={{ marginTop: '8px', opacity: 0.9, fontSize: '15px' }}>
              📍 {tour.destination}
            </div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <Badge label={getTourStatusLabel(tour.status)} color={getTourStatusColor(tour.status)} size="md" />
            <div style={{ marginTop: '12px', fontSize: '22px', fontWeight: 800 }}>{formatCurrency(tour.price)}</div>
            <div style={{ opacity: 0.7, fontSize: '13px' }}>giá/khách</div>
          </div>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
        {/* Thông tin chi tiết */}
        <div style={{ background: '#fff', borderRadius: '12px', padding: '20px', border: '1px solid #e5e7eb' }}>
          <h3 style={{ margin: '0 0 16px', fontSize: '16px', fontWeight: 700, color: '#111827' }}>
            📋 Thông tin tour
          </h3>
          <InfoRow label="Ngày khởi hành" value={formatDate(tour.startDate)} />
          <InfoRow label="Ngày kết thúc" value={formatDate(tour.endDate)} />
          <InfoRow label="Thời gian" value={`${tour.durationDays} ngày`} />
          <InfoRow label="Khu vực khởi hành" value={tour.departureRegion} />
          <InfoRow label="Số khách" value={`${tour.currentGuests}/${tour.maxGuests} khách`} />
          <InfoRow label="Ngôn ngữ yêu cầu" value={tour.requiredLanguages} />
          <InfoRow label="Chuyên môn yêu cầu" value={tour.requiredSpecialization} />
          <InfoRow label="Số HDV tối thiểu" value={`${tour.minGuides} người`} />

          {tour.description && (
            <div style={{ marginTop: '12px', paddingTop: '12px', borderTop: '1px solid #f3f4f6' }}>
              <div style={{ fontSize: '13px', color: '#9ca3af', marginBottom: '6px' }}>Mô tả</div>
              <p style={{ margin: 0, fontSize: '14px', color: '#374151', lineHeight: 1.6 }}>{tour.description}</p>
            </div>
          )}
        </div>

        {/* Phân công hướng dẫn viên */}
        <div style={{ background: '#fff', borderRadius: '12px', padding: '20px', border: '1px solid #e5e7eb' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
            <h3 style={{ margin: 0, fontSize: '16px', fontWeight: 700, color: '#111827' }}>
              👥 Hướng dẫn viên
            </h3>
            {canAssign && (
              <button
                onClick={() => setShowAssignForm(true)}
                style={{
                  padding: '8px 16px', background: '#1d4ed8', color: '#fff',
                  border: 'none', borderRadius: '8px', cursor: 'pointer',
                  fontSize: '13px', fontWeight: 700,
                }}
              >
                + Phân công
              </button>
            )}
          </div>

          <AssignmentList
            assignments={tour.assignments || []}
            onRefresh={fetchTour}
          />
        </div>
      </div>

      {/* Modal phân công */}
      {showAssignForm && (
        <AssignmentForm
          tour={tour}
          existingAssignments={tour.assignments || []}
          onSuccess={() => {
            setShowAssignForm(false)
            fetchTour()
          }}
          onClose={() => setShowAssignForm(false)}
        />
      )}
    </div>
  )
}

export default TourDetailPage
