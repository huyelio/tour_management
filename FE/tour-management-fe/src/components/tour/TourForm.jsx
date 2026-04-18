import { useState, useEffect } from 'react'
import { tourApi } from '../../api/tourApi'

const STATUSES = [
  { value: 'PLANNING', label: 'Lên kế hoạch' },
  { value: 'OPEN', label: 'Mở bán' },
  { value: 'FULL', label: 'Đã đủ khách' },
  { value: 'ONGOING', label: 'Đang diễn ra' },
  { value: 'COMPLETED', label: 'Hoàn thành' },
  { value: 'CANCELLED', label: 'Đã hủy' },
]

const INITIAL = {
  code: '', name: '', description: '', destination: '',
  startDate: '', endDate: '', maxGuests: '', price: '',
  status: 'PLANNING', minGuides: 1,
  requiredLanguages: '', requiredSpecialization: '', departureRegion: '',
}

const inputStyle = {
  width: '100%', padding: '8px 12px', border: '1px solid #d1d5db',
  borderRadius: '6px', fontSize: '14px', boxSizing: 'border-box', outline: 'none',
}
const labelStyle = {
  display: 'block', fontSize: '13px', fontWeight: 600, color: '#374151', marginBottom: '4px',
}

const TourForm = ({ tour, onSuccess, onClose }) => {
  const isEdit = !!tour
  const [form, setForm] = useState(INITIAL)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (tour) {
      setForm({
        code: tour.code || '',
        name: tour.name || '',
        description: tour.description || '',
        destination: tour.destination || '',
        startDate: tour.startDate || '',
        endDate: tour.endDate || '',
        maxGuests: tour.maxGuests || '',
        price: tour.price || '',
        status: tour.status || 'PLANNING',
        minGuides: tour.minGuides || 1,
        requiredLanguages: tour.requiredLanguages || '',
        requiredSpecialization: tour.requiredSpecialization || '',
        departureRegion: tour.departureRegion || '',
      })
    }
  }, [tour])

  const set = (field) => (e) => setForm(f => ({ ...f, [field]: e.target.value }))

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!form.code.trim() || !form.name.trim() || !form.destination.trim() || !form.startDate || !form.endDate) {
      setError('Vui lòng điền đầy đủ các trường bắt buộc (*)'); return
    }
    if (new Date(form.endDate) < new Date(form.startDate)) {
      setError('Ngày kết thúc phải >= ngày bắt đầu'); return
    }
    if (!form.maxGuests || parseInt(form.maxGuests) < 1) {
      setError('Số khách tối đa phải >= 1'); return
    }
    if (form.price === '' || parseFloat(form.price) < 0) {
      setError('Giá phải >= 0'); return
    }
    try {
      setLoading(true)
      setError(null)
      const payload = {
        ...form,
        maxGuests: parseInt(form.maxGuests),
        price: parseFloat(form.price),
        minGuides: parseInt(form.minGuides) || 1,
        requiredLanguages: form.requiredLanguages || null,
        requiredSpecialization: form.requiredSpecialization || null,
        departureRegion: form.departureRegion || null,
        description: form.description || null,
      }
      if (isEdit) {
        await tourApi.update(tour.id, payload)
      } else {
        await tourApi.create(payload)
      }
      onSuccess()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{
      position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.5)',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      zIndex: 1000, padding: '20px',
    }}>
      <div style={{
        background: '#fff', borderRadius: '16px', padding: '28px',
        width: '100%', maxWidth: '700px', maxHeight: '90vh', overflowY: 'auto',
        boxShadow: '0 20px 60px rgba(0,0,0,0.3)',
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
          <h2 style={{ margin: 0, fontSize: '18px', fontWeight: 800, color: '#111827' }}>
            {isEdit ? '✏️ Sửa tour' : '➕ Thêm tour mới'}
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
              <label style={labelStyle}>Mã tour *</label>
              <input
                style={inputStyle} value={form.code} onChange={set('code')}
                placeholder="VD: TOUR001" maxLength={20}
              />
            </div>
            <div>
              <label style={labelStyle}>Trạng thái</label>
              <select style={inputStyle} value={form.status} onChange={set('status')}>
                {STATUSES.map(s => <option key={s.value} value={s.value}>{s.label}</option>)}
              </select>
            </div>

            <div style={{ gridColumn: '1 / -1' }}>
              <label style={labelStyle}>Tên tour *</label>
              <input style={inputStyle} value={form.name} onChange={set('name')} placeholder="Nhập tên tour" maxLength={200} />
            </div>

            <div style={{ gridColumn: '1 / -1' }}>
              <label style={labelStyle}>Điểm đến *</label>
              <input style={inputStyle} value={form.destination} onChange={set('destination')} placeholder="VD: Đà Nẵng, Hội An" maxLength={200} />
            </div>

            <div>
              <label style={labelStyle}>Ngày bắt đầu *</label>
              <input type="date" style={inputStyle} value={form.startDate} onChange={set('startDate')} />
            </div>
            <div>
              <label style={labelStyle}>Ngày kết thúc *</label>
              <input type="date" style={inputStyle} value={form.endDate} onChange={set('endDate')} />
            </div>

            <div>
              <label style={labelStyle}>Số khách tối đa *</label>
              <input type="number" min="1" style={inputStyle} value={form.maxGuests} onChange={set('maxGuests')} placeholder="VD: 30" />
            </div>
            <div>
              <label style={labelStyle}>Giá (VNĐ) *</label>
              <input type="number" min="0" step="1000" style={inputStyle} value={form.price} onChange={set('price')} placeholder="VD: 5000000" />
            </div>

            <div>
              <label style={labelStyle}>Số HDV tối thiểu</label>
              <input type="number" min="1" style={inputStyle} value={form.minGuides} onChange={set('minGuides')} />
            </div>
            <div>
              <label style={labelStyle}>Khu vực khởi hành</label>
              <input style={inputStyle} value={form.departureRegion} onChange={set('departureRegion')} placeholder="VD: Hà Nội" />
            </div>

            <div>
              <label style={labelStyle}>Ngôn ngữ yêu cầu</label>
              <input style={inputStyle} value={form.requiredLanguages} onChange={set('requiredLanguages')} placeholder="VD: English, French" />
            </div>
            <div>
              <label style={labelStyle}>Chuyên môn yêu cầu</label>
              <input style={inputStyle} value={form.requiredSpecialization} onChange={set('requiredSpecialization')} placeholder="VD: Mountain, Eco" />
            </div>

            <div style={{ gridColumn: '1 / -1' }}>
              <label style={labelStyle}>Mô tả</label>
              <textarea
                style={{ ...inputStyle, resize: 'vertical', minHeight: '80px' }}
                value={form.description}
                onChange={set('description')}
                placeholder="Mô tả tour..."
              />
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
              style={{ padding: '10px 24px', background: loading ? '#93c5fd' : '#1d4ed8', color: '#fff', border: 'none', borderRadius: '8px', cursor: loading ? 'not-allowed' : 'pointer', fontSize: '14px', fontWeight: 700 }}
            >
              {loading ? 'Đang lưu...' : isEdit ? 'Cập nhật' : 'Tạo tour'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default TourForm
