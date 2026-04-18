// hideStatus: true → ẩn select Trạng thái (dùng trong AssignmentForm vì status được BE xử lý)
const GuideFilter = ({ filters, onChange, hideStatus = false }) => {
  const selectStyle = {
    padding: '8px 12px', border: '1px solid #d1d5db', borderRadius: '8px',
    fontSize: '13px', background: '#fff', cursor: 'pointer', outline: 'none',
  }

  const inputStyle = {
    padding: '8px 12px', border: '1px solid #d1d5db', borderRadius: '8px',
    fontSize: '13px', outline: 'none', width: '100%',
  }

  return (
    <div style={{
      background: '#f9fafb', borderRadius: '10px', padding: '16px',
      marginBottom: '16px', border: '1px solid #e5e7eb',
    }}>
      <h4 style={{ margin: '0 0 12px', fontSize: '14px', fontWeight: 600, color: '#374151' }}>
        🔍 Bộ lọc hướng dẫn viên
      </h4>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(160px, 1fr))', gap: '10px' }}>
        {!hideStatus && (
          <div>
            <label style={{ fontSize: '12px', color: '#6b7280', display: 'block', marginBottom: '4px' }}>Trạng thái</label>
            <select style={selectStyle} value={filters.status} onChange={e => onChange({ ...filters, status: e.target.value })}>
              <option value="">Tất cả</option>
              <option value="AVAILABLE">Sẵn sàng</option>
              <option value="ON_TOUR">Đang dẫn tour</option>
              <option value="ON_LEAVE">Nghỉ phép</option>
              <option value="INACTIVE">Ngừng hoạt động</option>
            </select>
          </div>
        )}

        <div>
          <label style={{ fontSize: '12px', color: '#6b7280', display: 'block', marginBottom: '4px' }}>Chuyên môn</label>
          <select style={selectStyle} value={filters.specialization} onChange={e => onChange({ ...filters, specialization: e.target.value })}>
            <option value="">Tất cả</option>
            <option value="Cultural">Văn hóa</option>
            <option value="Beach">Biển đảo</option>
            <option value="Mountain">Leo núi</option>
            <option value="Eco-tourism">Sinh thái</option>
          </select>
        </div>

        <div>
          <label style={{ fontSize: '12px', color: '#6b7280', display: 'block', marginBottom: '4px' }}>Ngoại ngữ</label>
          <select style={selectStyle} value={filters.language} onChange={e => onChange({ ...filters, language: e.target.value })}>
            <option value="">Tất cả</option>
            <option value="English">Tiếng Anh</option>
            <option value="French">Tiếng Pháp</option>
            <option value="Japanese">Tiếng Nhật</option>
            <option value="Korean">Tiếng Hàn</option>
            <option value="Chinese">Tiếng Trung</option>
          </select>
        </div>

        <div>
          <label style={{ fontSize: '12px', color: '#6b7280', display: 'block', marginBottom: '4px' }}>Khu vực</label>
          <select style={selectStyle} value={filters.region} onChange={e => onChange({ ...filters, region: e.target.value })}>
            <option value="">Tất cả</option>
            <option value="Miền Bắc">Miền Bắc</option>
            <option value="Miền Trung">Miền Trung</option>
            <option value="Miền Nam">Miền Nam</option>
            <option value="Toàn quốc">Toàn quốc</option>
          </select>
        </div>
      </div>
    </div>
  )
}

export default GuideFilter
