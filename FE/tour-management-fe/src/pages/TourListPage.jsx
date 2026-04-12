import TourList from '../components/tour/TourList'

const TourListPage = () => (
  <div>
    <div style={{ marginBottom: '24px' }}>
      <h1 style={{ margin: 0, fontSize: '24px', fontWeight: 800, color: '#111827' }}>
        🗺️ Quản lý Tour Du lịch
      </h1>
      <p style={{ margin: '6px 0 0', color: '#6b7280', fontSize: '15px' }}>
        Chọn một tour để xem chi tiết và phân công hướng dẫn viên
      </p>
    </div>
    <TourList />
  </div>
)

export default TourListPage
