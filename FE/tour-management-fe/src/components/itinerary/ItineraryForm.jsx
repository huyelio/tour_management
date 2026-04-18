import { useState, useEffect } from 'react'
import { itineraryApi } from '../../api/itineraryApi'

const ACTIVITY_TYPES = [
  'Tham quan', 'Ăn uống', 'Di chuyển', 'Nghỉ ngơi',
  'Hoạt động ngoài trời', 'Mua sắm', 'Khác',
]

const INITIAL = {
  dayNumber: 1, sequenceOrder: 1,
  title: '', description: '', location: '',
  startTime: '', endTime: '', activityType: '', note: '', isOptional: false,
}

const inputStyle = {
  width: '100%', padding: '8px 12px', border: '1px solid #d1d5db',
  borderRadius: '6px', fontSize: '14px', boxSizing: 'border-box', outline: 'none',
}
const labelStyle = {
  display: 'block', fontSize: '13px', fontWeight: 600, color: '#374151', marginBottom: '4px',
}

const ItineraryForm = ({ tourId, durationDays, itinerary, onSuccess, onClose }) => {
  const isEdit = !!itinerary
  const [form, setForm] = useState(INITIAL)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (itinerary) {
      setForm({
        dayNumber: itinerary.dayNumber || 1,
        sequenceOrder: itinerary.sequenceOrder || 1,
        title: itinerary.title || '',
        description: itinerary.description || '',
        location: itinerary.location || '',
        startTime: itinerary.startTime || '',
        endTime: itinerary.endTime || '',
        activityType: itinerary.activityType || '',
        note: itinerary.note || '',
        isOptional: itinerary.isOptional || false,
      })
    } else {
      setForm(INITIAL)
    }
  }, [itinerary])

  const set = (field) => (e) => {
    const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value
    setForm(f => ({ ...f, [field]: value }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!form.title.trim()) {
      setError('Vui lòng nhập tiêu đề hoạt động'); return
    }
    if (form.startTime && form.endTime && form.startTime >= form.endTime) {
      setError('Giờ bắt đầu phải trước giờ kết thúc'); return
    }
    const maxDay = durationDays || 1
    if (parseInt(form.dayNumber) > maxDay) {
      setError(`Ngày ${form.dayNumber} vượt quá số ngày tour (${maxDay} ngày)`); return
    }
    try {
      setLoading(true)
      setError(null)
      const payload = {
        dayNumber: parseInt(form.dayNumber),
        sequenceOrder: parseInt(form.sequenceOrder),
        title: form.title.trim(),
        description: form.description || null,
        location: form.location || null,
        startTime: form.startTime || null,
        endTime: form.endTime || null,
        activityType: form.activityType || null,
        note: form.note || null,
        isOptional: form.isOptional,
      }
      if (isEdit) {
        await itineraryApi.update(tourId, itinerary.id, payload)
      } else {
        await itineraryApi.create(tourId, payload)
      }
      onSuccess()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const days = Array.from({ length: durationDays || 1 }, (_, i) => i + 1)

  return (
    <div style={{
      position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.55)',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      zIndex: 1001, padding: '20px',
    }}>
      <div style={{
        background: '#fff', borderRadius: '16px', padding: '28px',
        width: '100%', maxWidth: '560px', maxHeight: '90vh', overflowY: 'auto',
        boxShadow: '0 20px 60px rgba(0,0,0,0.3)',
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
          <h2 style={{ margin: 0, fontSize: '18px', fontWeight: 800, color: '#111827' }}>
            {isEdit ? '✏️ Sửa hoạt động' : '➕ Thêm hoạt động'}
          </h2>
          <button
            onClick={onClose}
            style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '20px', color: '#9ca3af', lineHeight: 1 }}
          >✕</button>
        </div>

        {error && (
          <div style={{
            background: '#fef2f2', border: '1px solid #fca5a5', borderRadius: '8px',
            padding: '10px 14px', marginBottom: '16px', color: '#dc2626', fontSize: '14px',
          }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
            <div>
              <label style={labelStyle}>Ngày *</label>
              <select style={inputStyle} value={form.dayNumber} onChange={set('dayNumber')}>
                {days.map(d => <option key={d} value={d}>Ngày {d}</option>)}
              </select>
            </div>
            <div>
              <label style={labelStyle}>Thứ tự *</label>
              <input type="number" min="1" style={inputStyle} value={form.sequenceOrder} onChange={set('sequenceOrder')} />
            </div>

            <div style={{ gridColumn: '1 / -1' }}>
              <label style={labelStyle}>Tiêu đề hoạt động *</label>
              <input
                style={inputStyle} value={form.title} onChange={set('title')}
                placeholder="VD: Tham quan Hội An Ancient Town" maxLength={200}
              />
            </div>

            <div>
              <label style={labelStyle}>Giờ bắt đầu</label>
              <input type="time" style={inputStyle} value={form.startTime} onChange={set('startTime')} />
            </div>
            <div>
              <label style={labelStyle}>Giờ kết thúc</label>
              <input type="time" style={inputStyle} value={form.endTime} onChange={set('endTime')} />
            </div>

            <div>
              <label style={labelStyle}>Địa điểm</label>
              <input style={inputStyle} value={form.location} onChange={set('location')} placeholder="VD: Phố cổ Hội An" />
            </div>
            <div>
              <label style={labelStyle}>Loại hoạt động</label>
              <select style={inputStyle} value={form.activityType} onChange={set('activityType')}>
                <option value="">-- Chọn loại --</option>
                {ACTIVITY_TYPES.map(t => <option key={t} value={t}>{t}</option>)}
              </select>
            </div>

            <div style={{ gridColumn: '1 / -1' }}>
              <label style={labelStyle}>Mô tả</label>
              <textarea
                style={{ ...inputStyle, resize: 'vertical', minHeight: '70px' }}
                value={form.description} onChange={set('description')}
                placeholder="Mô tả chi tiết hoạt động..."
              />
            </div>

            <div style={{ gridColumn: '1 / -1' }}>
              <label style={labelStyle}>Ghi chú</label>
              <input style={inputStyle} value={form.note} onChange={set('note')} placeholder="Ghi chú thêm..." />
            </div>

            <div style={{ gridColumn: '1 / -1' }}>
              <label style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', fontSize: '14px', color: '#374151' }}>
                <input type="checkbox" checked={form.isOptional} onChange={set('isOptional')} />
                Hoạt động tùy chọn (không bắt buộc)
              </label>
            </div>
          </div>

          <div style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end', marginTop: '20px' }}>
            <button
              type="button" onClick={onClose}
              style={{ padding: '10px 20px', border: '1px solid #d1d5db', borderRadius: '8px', background: '#fff', cursor: 'pointer', fontSize: '14px', color: '#374151' }}
            >
              Hủy
            </button>
            <button
              type="submit" disabled={loading}
              style={{ padding: '10px 24px', background: loading ? '#6ee7b7' : '#059669', color: '#fff', border: 'none', borderRadius: '8px', cursor: loading ? 'not-allowed' : 'pointer', fontSize: '14px', fontWeight: 700 }}
            >
              {loading ? 'Đang lưu...' : isEdit ? 'Cập nhật' : 'Thêm hoạt động'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default ItineraryForm
