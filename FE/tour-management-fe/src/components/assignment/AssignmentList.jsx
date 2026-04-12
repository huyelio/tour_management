import { useState } from 'react'
import { assignmentApi } from '../../api/assignmentApi'
import Badge from '../common/Badge'
import { getGuideStatusColor } from '../../utils/helpers'

const roleLabel = { LEAD: 'HDV Chính', ASSISTANT: 'Hỗ trợ' }
const roleColor = { LEAD: '#7c3aed', ASSISTANT: '#0891b2' }
const assignStatusLabel = { ASSIGNED: 'Đã phân công', CONFIRMED: 'Đã xác nhận', CANCELLED: 'Đã hủy' }
const assignStatusColor = { ASSIGNED: '#f59e0b', CONFIRMED: '#10b981', CANCELLED: '#ef4444' }

const AssignmentList = ({ assignments, onRefresh }) => {
  const [cancellingId, setCancellingId] = useState(null)

  const handleCancel = async (id) => {
    if (!confirm('Bạn có chắc muốn hủy phân công này?')) return
    try {
      setCancellingId(id)
      await assignmentApi.cancel(id)
      onRefresh()
    } catch (err) {
      alert('Lỗi: ' + err.message)
    } finally {
      setCancellingId(null)
    }
  }

  const active = assignments.filter(a => a.status !== 'CANCELLED')
  const cancelled = assignments.filter(a => a.status === 'CANCELLED')

  if (assignments.length === 0) {
    return (
      <div style={{ textAlign: 'center', padding: '32px', color: '#9ca3af', background: '#f9fafb', borderRadius: '10px', border: '1px dashed #d1d5db' }}>
        <div style={{ fontSize: '36px', marginBottom: '8px' }}>👤</div>
        <p>Chưa có hướng dẫn viên nào được phân công</p>
      </div>
    )
  }

  return (
    <div>
      <h4 style={{ margin: '0 0 12px', fontSize: '14px', fontWeight: 600, color: '#374151' }}>
        Hướng dẫn viên đã phân công ({active.length} người)
      </h4>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        {active.map(a => (
          <div key={a.id} style={{
            background: '#fff', borderRadius: '10px', padding: '14px 16px',
            border: '1px solid #e5e7eb', display: 'flex', gap: '12px', alignItems: 'center',
          }}>
            <div style={{
              width: '40px', height: '40px', borderRadius: '50%',
              background: 'linear-gradient(135deg, #667eea, #764ba2)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              color: '#fff', fontWeight: 700, fontSize: '16px', flexShrink: 0,
            }}>
              {a.guideName?.charAt(0)}
            </div>
            <div style={{ flex: 1 }}>
              <div style={{ fontWeight: 700, color: '#111827' }}>{a.guideName}</div>
              <div style={{ fontSize: '12px', color: '#6b7280', marginTop: '2px' }}>
                {a.guideCode} | {a.guideLanguages} | 📞 {a.guidePhone}
              </div>
            </div>
            <div style={{ display: 'flex', gap: '8px', alignItems: 'center', flexWrap: 'wrap', justifyContent: 'flex-end' }}>
              <Badge label={roleLabel[a.role] || a.role} color={roleColor[a.role] || '#6b7280'} />
              <Badge label={assignStatusLabel[a.status] || a.status} color={assignStatusColor[a.status] || '#6b7280'} />
              <button
                onClick={() => handleCancel(a.id)}
                disabled={cancellingId === a.id}
                style={{
                  padding: '4px 12px', background: '#fee2e2', color: '#dc2626',
                  border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '12px', fontWeight: 600,
                }}
              >
                {cancellingId === a.id ? '...' : 'Hủy'}
              </button>
            </div>
          </div>
        ))}

        {cancelled.length > 0 && (
          <details style={{ marginTop: '8px' }}>
            <summary style={{ cursor: 'pointer', fontSize: '13px', color: '#9ca3af', padding: '4px' }}>
              Xem {cancelled.length} phân công đã hủy
            </summary>
            {cancelled.map(a => (
              <div key={a.id} style={{
                background: '#f9fafb', borderRadius: '8px', padding: '10px 14px',
                border: '1px solid #e5e7eb', marginTop: '6px', opacity: 0.7,
                display: 'flex', gap: '10px', alignItems: 'center',
              }}>
                <span style={{ fontWeight: 600, color: '#6b7280', flex: 1 }}>{a.guideName}</span>
                <Badge label="Đã hủy" color="#ef4444" />
              </div>
            ))}
          </details>
        )}
      </div>
    </div>
  )
}

export default AssignmentList
