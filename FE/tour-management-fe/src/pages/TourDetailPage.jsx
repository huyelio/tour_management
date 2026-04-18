import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { tourApi } from '../api/tourApi'
import AssignmentForm from '../components/assignment/AssignmentForm'
import AssignmentList from '../components/assignment/AssignmentList'
import TourForm from '../components/tour/TourForm'
import ItineraryList from '../components/itinerary/ItineraryList'
import Badge from '../components/common/Badge'
import LoadingSpinner from '../components/common/LoadingSpinner'
import ErrorAlert from '../components/common/ErrorAlert'
import SuccessAlert from '../components/common/SuccessAlert'
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
  const [success, setSuccess] = useState(null)
  const [showAssignForm, setShowAssignForm] = useState(false)
  const [showTourForm, setShowTourForm] = useState(false)

  useEffect(() => { fetchTour() }, [id])

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

  const handleCancelTour = async () => {
    if (!window.confirm(`Bạn có chắc muốn hủy tour "${tour.name}"?\nHành động này không thể hoàn tác.`)) return
    try {
      await tourApi.cancel(id)
      setSuccess('Đã hủy tour thành công')
      fetchTour()
    } catch (err) {
      setError(err.message)
    }
  }

  if (loading) return <LoadingSpinner text="Đang tải thông tin tour..." />
  if (error && !tour) return <ErrorAlert message={error} />
  if (!tour) return null

  const canAssign = ['PLANNING', 'OPEN'].includes(tour.status)
  const canEdit = tour.status !== 'COMPLETED'
  const canCancel = !['COMPLETED', 'CANCELLED'].includes(tour.status)

  const cannotAssignReason = !canAssign
    ? ({
        FULL: 'Tour đã đủ khách, không thể phân công thêm.',
        ONGOING: 'Tour đang diễn ra, không thể thay đổi phân công.',
        COMPLETED: 'Tour đã hoàn thành.',
        CANCELLED: 'Tour đã bị hủy.',
      }[tour.status] || 'Tour này không thể phân công hướng dẫn viên.')
    : null

  return (
    <div style={{ maxWidth: '900px', margin: '0 auto' }}>
      {/* Top bar */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', flexWrap: 'wrap', gap: '10px' }}>
        <button
          onClick={() => navigate('/')}
          style={{
            background: 'none', border: '1px solid #d1d5db', borderRadius: '8px',
            padding: '8px 16px', cursor: 'pointer', fontSize: '14px', color: '#374151',
            display: 'flex', alignItems: 'center', gap: '6px',
          }}
        >
          ← Quay lại danh sách
        </button>
        <div style={{ display: 'flex', gap: '8px' }}>
          {canEdit && (
            <button
              onClick={() => setShowTourForm(true)}
              style={{
                padding: '8px 16px', background: '#eff6ff', color: '#1d4ed8',
                border: '1px solid #bfdbfe', borderRadius: '8px',
                cursor: 'pointer', fontSize: '14px', fontWeight: 600,
              }}
            >
              ✏️ Sửa tour
            </button>
          )}
          {canCancel && (
            <button
              onClick={handleCancelTour}
              style={{
                padding: '8px 16px', background: '#fef2f2', color: '#dc2626',
                border: '1px solid #fecaca', borderRadius: '8px',
                cursor: 'pointer', fontSize: '14px', fontWeight: 600,
              }}
            >
              🚫 Hủy tour
            </button>
          )}
        </div>
      </div>

      {success && <SuccessAlert message={success} onClose={() => setSuccess(null)} />}
      {error && <ErrorAlert message={error} onClose={() => setError(null)} />}

      {/* Hero header */}
      <div style={{
        background: 'linear-gradient(135deg, #1e3a8a, #2563eb)',
        borderRadius: '16px', padding: '28px', color: '#fff', marginBottom: '24px',
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '12px' }}>
          <div>
            <div style={{ opacity: 0.7, fontSize: '13px', marginBottom: '6px' }}>{tour.code}</div>
            <h1 style={{ margin: 0, fontSize: '22px', fontWeight: 800 }}>{tour.name}</h1>
            <div style={{ marginTop: '8px', opacity: 0.9, fontSize: '15px' }}>📍 {tour.destination}</div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <Badge label={getTourStatusLabel(tour.status)} color={getTourStatusColor(tour.status)} size="md" />
            <div style={{ marginTop: '12px', fontSize: '22px', fontWeight: 800 }}>{formatCurrency(tour.price)}</div>
            <div style={{ opacity: 0.7, fontSize: '13px' }}>giá/khách</div>
          </div>
        </div>
      </div>

      {/* Info + Assignment */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
        {/* Tour info */}
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

        {/* Assignment */}
        <div style={{ background: '#fff', borderRadius: '12px', padding: '20px', border: '1px solid #e5e7eb' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
            <h3 style={{ margin: 0, fontSize: '16px', fontWeight: 700, color: '#111827' }}>👥 Hướng dẫn viên</h3>
            {canAssign ? (
              <button
                onClick={() => setShowAssignForm(true)}
                style={{
                  padding: '8px 16px', background: '#1d4ed8', color: '#fff',
                  border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '13px', fontWeight: 700,
                }}
              >
                + Phân công
              </button>
            ) : (
              <div
                title={cannotAssignReason}
                style={{
                  padding: '8px 14px', background: '#f3f4f6', color: '#9ca3af',
                  border: '1px solid #e5e7eb', borderRadius: '8px',
                  fontSize: '13px', fontWeight: 600, cursor: 'not-allowed',
                }}
              >
                🔒 Phân công
              </div>
            )}
          </div>

          {cannotAssignReason && (
            <div style={{
              background: '#fef9c3', border: '1px solid #fde047', borderRadius: '8px',
              padding: '8px 12px', marginBottom: '12px',
              fontSize: '13px', color: '#854d0e', display: 'flex', alignItems: 'center', gap: '6px',
            }}>
              🔒 {cannotAssignReason}
            </div>
          )}

          <AssignmentList assignments={tour.assignments || []} onRefresh={fetchTour} />
        </div>
      </div>

      {/* Itinerary section */}
      <ItineraryList
        tourId={tour.id}
        durationDays={tour.durationDays}
        tourStatus={tour.status}
      />

      {/* Modal: phân công */}
      {showAssignForm && (
        <AssignmentForm
          tour={tour}
          existingAssignments={tour.assignments || []}
          onSuccess={() => { setShowAssignForm(false); fetchTour() }}
          onClose={() => setShowAssignForm(false)}
        />
      )}

      {/* Modal: sửa tour */}
      {showTourForm && (
        <TourForm
          tour={tour}
          onSuccess={() => {
            setShowTourForm(false)
            setSuccess('Cập nhật tour thành công!')
            fetchTour()
          }}
          onClose={() => setShowTourForm(false)}
        />
      )}
    </div>
  )
}

export default TourDetailPage
