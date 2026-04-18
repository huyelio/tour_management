import { useState, useEffect } from 'react'
import { guideApi } from '../../api/guideApi'
import { assignmentApi } from '../../api/assignmentApi'
import GuideCard from '../guide/GuideCard'
import GuideFilter from '../guide/GuideFilter'
import LoadingSpinner from '../common/LoadingSpinner'
import ErrorAlert from '../common/ErrorAlert'
import SuccessAlert from '../common/SuccessAlert'

const AssignmentForm = ({ tour, existingAssignments, onSuccess, onClose }) => {
  const [guides, setGuides] = useState([])
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState(null)
  const [successMsg, setSuccessMsg] = useState(null)

  // Danh sách guide đã chọn: [{ guideId, role, note }]
  const [selectedGuides, setSelectedGuides] = useState([])

  // Không có filter status – BE tự tính eligible/warning theo ngày tour
  const [filters, setFilters] = useState({ specialization: '', language: '', region: '' })

  // ID các guide đã được phân công (status != CANCELLED)
  const assignedGuideIds = (existingAssignments || [])
    .filter(a => a.status !== 'CANCELLED')
    .map(a => a.guideId)

  useEffect(() => {
    fetchGuides()
  }, [filters])

  const fetchGuides = async () => {
    try {
      setLoading(true)
      const res = await guideApi.getForTour(tour.id, filters)
      setGuides(res.data || [])
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  // Tách 2 nhóm: eligible và ineligible (BE đã sort eligible lên trước, FE chỉ tách để header riêng)
  const eligibleGuides = guides.filter(g => g.eligible)
  const ineligibleGuides = guides.filter(g => !g.eligible)

  const handleSelectGuide = (guide) => {
    const exists = selectedGuides.find(g => g.guideId === guide.id)
    if (exists) {
      setSelectedGuides(prev => prev.filter(g => g.guideId !== guide.id))
    } else {
      setSelectedGuides(prev => [...prev, { guideId: guide.id, guideName: guide.fullName, role: 'LEAD', note: '' }])
    }
  }

  const handleRoleChange = (guideId, role) => {
    setSelectedGuides(prev => prev.map(g => g.guideId === guideId ? { ...g, role } : g))
  }

  const handleNoteChange = (guideId, note) => {
    setSelectedGuides(prev => prev.map(g => g.guideId === guideId ? { ...g, note } : g))
  }

  const handleSubmit = async () => {
    if (selectedGuides.length === 0) {
      setError('Vui lòng chọn ít nhất một hướng dẫn viên')
      return
    }

    setSaving(true)
    setError(null)
    setSuccessMsg(null)

    try {
      const request = {
        tourId: tour.id,
        guides: selectedGuides.map(({ guideId, role, note }) => ({ guideId, role, note })),
      }
      const res = await assignmentApi.save(request)
      setSuccessMsg(res.message || 'Phân công thành công!')
      setSelectedGuides([])
      if (onSuccess) onSuccess()
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  const isSelected = (guideId) => selectedGuides.some(g => g.guideId === guideId)

  return (
    <div style={{
      position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.5)',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      zIndex: 1000, padding: '20px',
    }}>
      <div style={{
        background: '#fff', borderRadius: '16px', width: '100%', maxWidth: '900px',
        maxHeight: '90vh', display: 'flex', flexDirection: 'column',
        boxShadow: '0 25px 50px rgba(0,0,0,0.2)',
      }}>
        {/* Header */}
        <div style={{
          padding: '20px 24px', borderBottom: '1px solid #e5e7eb',
          display: 'flex', justifyContent: 'space-between', alignItems: 'center',
          background: 'linear-gradient(135deg, #1d4ed8, #3b82f6)',
          borderRadius: '16px 16px 0 0', color: '#fff',
        }}>
          <div>
            <h2 style={{ margin: 0, fontSize: '18px', fontWeight: 700 }}>Phân công hướng dẫn viên</h2>
            <p style={{ margin: '4px 0 0', opacity: 0.85, fontSize: '14px' }}>
              Tour: <strong>{tour.name}</strong> ({tour.code}) | Cần tối thiểu: {tour.minGuides || 1} HDV
            </p>
          </div>
          <button onClick={onClose} style={{ background: 'rgba(255,255,255,0.2)', border: 'none', color: '#fff', borderRadius: '8px', padding: '8px 12px', cursor: 'pointer', fontSize: '18px' }}>×</button>
        </div>

        {/* Content */}
        <div style={{ padding: '20px 24px', overflowY: 'auto', flex: 1 }}>
          {error && <ErrorAlert message={error} onClose={() => setError(null)} />}
          {successMsg && <SuccessAlert message={successMsg} onClose={() => setSuccessMsg(null)} />}

          {/* Bộ lọc – ẩn select Trạng thái vì BE đã xử lý theo ngày tour */}
          <GuideFilter filters={filters} onChange={setFilters} hideStatus />

          {/* Danh sách đã chọn */}
          {selectedGuides.length > 0 && (
            <div style={{
              background: '#eff6ff', border: '1px solid #bfdbfe', borderRadius: '10px',
              padding: '16px', marginBottom: '16px',
            }}>
              <h4 style={{ margin: '0 0 12px', color: '#1d4ed8', fontSize: '14px', fontWeight: 700 }}>
                ✅ Đã chọn {selectedGuides.length} hướng dẫn viên:
              </h4>
              {selectedGuides.map(g => (
                <div key={g.guideId} style={{
                  background: '#fff', borderRadius: '8px', padding: '12px',
                  marginBottom: '8px', display: 'flex', gap: '12px', alignItems: 'center',
                  border: '1px solid #bfdbfe',
                }}>
                  <span style={{ fontWeight: 600, color: '#1e40af', flex: 1 }}>{g.guideName}</span>

                  <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                    <label style={{ fontSize: '12px', color: '#6b7280' }}>Vai trò:</label>
                    <select
                      value={g.role}
                      onChange={e => handleRoleChange(g.guideId, e.target.value)}
                      style={{ padding: '4px 8px', borderRadius: '6px', border: '1px solid #d1d5db', fontSize: '13px' }}
                    >
                      <option value="LEAD">Hướng dẫn chính</option>
                      <option value="ASSISTANT">Hỗ trợ</option>
                    </select>
                  </div>

                  <input
                    type="text"
                    placeholder="Ghi chú..."
                    value={g.note}
                    onChange={e => handleNoteChange(g.guideId, e.target.value)}
                    style={{ padding: '4px 8px', borderRadius: '6px', border: '1px solid #d1d5db', fontSize: '13px', width: '150px' }}
                  />

                  <button
                    onClick={() => setSelectedGuides(prev => prev.filter(x => x.guideId !== g.guideId))}
                    style={{ background: '#fee2e2', color: '#dc2626', border: 'none', borderRadius: '6px', padding: '4px 10px', cursor: 'pointer', fontSize: '13px' }}
                  >
                    Bỏ
                  </button>
                </div>
              ))}
            </div>
          )}

          {/* Danh sách hướng dẫn viên – 2 nhóm */}
          {loading ? (
            <LoadingSpinner text="Đang tải danh sách hướng dẫn viên..." />
          ) : guides.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '40px', color: '#9ca3af' }}>
              <div style={{ fontSize: '40px' }}>👤</div>
              <p>Không tìm thấy hướng dẫn viên</p>
            </div>
          ) : (
            <>
              {/* Nhóm 1 – Phù hợp */}
              <div style={{ marginBottom: '20px' }}>
                <div style={{
                  display: 'flex', alignItems: 'center', gap: '8px',
                  marginBottom: '12px', padding: '6px 12px',
                  background: '#f0fdf4', borderRadius: '8px', border: '1px solid #bbf7d0',
                }}>
                  <span style={{ fontSize: '14px', fontWeight: 700, color: '#15803d' }}>
                    ✅ Phù hợp với tour ({eligibleGuides.length} người)
                  </span>
                </div>
                {eligibleGuides.length === 0 ? (
                  <p style={{ color: '#9ca3af', fontSize: '13px', paddingLeft: '8px' }}>
                    Không có hướng dẫn viên nào phù hợp với bộ lọc hiện tại
                  </p>
                ) : (
                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '12px' }}>
                    {eligibleGuides.map(guide => (
                      <GuideCard
                        key={guide.id}
                        guide={guide}
                        selectable={true}
                        selected={isSelected(guide.id)}
                        onSelect={handleSelectGuide}
                        alreadyAssigned={assignedGuideIds.includes(guide.id)}
                      />
                    ))}
                  </div>
                )}
              </div>

              {/* Nhóm 2 – Không phù hợp */}
              {ineligibleGuides.length > 0 && (
                <div>
                  <div style={{
                    display: 'flex', alignItems: 'center', gap: '8px',
                    marginBottom: '12px', padding: '6px 12px',
                    background: '#fffbeb', borderRadius: '8px', border: '1px solid #fcd34d',
                  }}>
                    <span style={{ fontSize: '14px', fontWeight: 700, color: '#92400e' }}>
                      ⚠ Không thể phân công ({ineligibleGuides.length} người)
                    </span>
                    <span style={{ fontSize: '12px', color: '#b45309' }}>
                      – bị trùng lịch hoặc không hoạt động
                    </span>
                  </div>
                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '12px' }}>
                    {ineligibleGuides.map(guide => (
                      <GuideCard
                        key={guide.id}
                        guide={guide}
                        selectable={true}
                        selected={false}
                        onSelect={null}
                        alreadyAssigned={assignedGuideIds.includes(guide.id)}
                        warning={guide.availabilityWarning}
                      />
                    ))}
                  </div>
                </div>
              )}
            </>
          )}
        </div>

        {/* Footer - nút Lưu phân công */}
        <div style={{
          padding: '16px 24px', borderTop: '1px solid #e5e7eb',
          display: 'flex', justifyContent: 'flex-end', gap: '12px',
          background: '#f9fafb', borderRadius: '0 0 16px 16px',
        }}>
          <button
            onClick={onClose}
            style={{ padding: '10px 20px', border: '1px solid #d1d5db', borderRadius: '8px', background: '#fff', cursor: 'pointer', fontSize: '14px' }}
          >
            Đóng
          </button>
          <button
            onClick={handleSubmit}
            disabled={saving || selectedGuides.length === 0}
            style={{
              padding: '10px 24px',
              background: saving || selectedGuides.length === 0 ? '#9ca3af' : '#1d4ed8',
              color: '#fff', border: 'none', borderRadius: '8px',
              cursor: saving || selectedGuides.length === 0 ? 'not-allowed' : 'pointer',
              fontSize: '14px', fontWeight: 700,
            }}
          >
            {saving ? '⏳ Đang lưu...' : `💾 Lưu phân công (${selectedGuides.length})`}
          </button>
        </div>
      </div>
    </div>
  )
}

export default AssignmentForm
