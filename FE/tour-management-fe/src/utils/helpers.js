// Format tiền Việt Nam
export const formatCurrency = (amount) => {
  if (!amount) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount)
}

// Format ngày
export const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('vi-VN')
}

// Màu badge theo trạng thái tour
export const getTourStatusColor = (status) => {
  const colors = {
    PLANNING: '#f59e0b',
    OPEN: '#10b981',
    FULL: '#3b82f6',
    ONGOING: '#8b5cf6',
    COMPLETED: '#6b7280',
    CANCELLED: '#ef4444',
  }
  return colors[status] || '#6b7280'
}

// Label tiếng Việt cho trạng thái tour
export const getTourStatusLabel = (status) => {
  const labels = {
    PLANNING: 'Lên kế hoạch',
    OPEN: 'Mở bán',
    FULL: 'Đã đủ khách',
    ONGOING: 'Đang diễn ra',
    COMPLETED: 'Hoàn thành',
    CANCELLED: 'Đã hủy',
  }
  return labels[status] || status
}

// Màu badge theo trạng thái guide
export const getGuideStatusColor = (status) => {
  const colors = {
    AVAILABLE: '#10b981',
    ON_TOUR: '#3b82f6',
    ON_LEAVE: '#f59e0b',
    INACTIVE: '#ef4444',
  }
  return colors[status] || '#6b7280'
}

// Label tiếng Việt cho trạng thái guide
export const getGuideStatusLabel = (status) => {
  const labels = {
    AVAILABLE: 'Sẵn sàng',
    ON_TOUR: 'Đang dẫn tour',
    ON_LEAVE: 'Nghỉ phép',
    INACTIVE: 'Ngừng hoạt động',
  }
  return labels[status] || status
}
