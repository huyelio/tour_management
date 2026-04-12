import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

/**
 * Lấy thông báo lỗi từ phản hồi (ApiResponse, Spring Boot error, ProblemDetail, HTML…)
 */
function getErrorMessage(error) {
  if (!error.response) {
    if (error.code === 'ECONNABORTED') {
      return 'Hết thời gian chờ server. Kiểm tra backend có chạy không.'
    }
    if (error.message === 'Network Error') {
      return 'Không kết nối được tới server (Network Error). Kiểm tra backend và proxy /api.'
    }
    return error.message || 'Lỗi kết nối server (không có phản hồi).'
  }

  const { status, data } = error.response

  if (typeof data === 'string') {
    const trimmed = data.trim()
    if (trimmed.startsWith('<')) {
      return `Lỗi ${status}: server trả về HTML (thường do sai URL hoặc nginx).`
    }
    return trimmed || `Lỗi HTTP ${status}`
  }

  if (data && typeof data === 'object') {
    const msg =
      data.message ??
      data.detail ??
      data.title ??
      data.error ??
      (Array.isArray(data.errors) ? data.errors.map((e) => e.defaultMessage || e).join('; ') : null)
    if (msg) return typeof msg === 'string' ? msg : JSON.stringify(msg)
  }

  return `Lỗi HTTP ${status}`
}

api.interceptors.response.use(
  (response) => response,
  (error) => Promise.reject(new Error(getErrorMessage(error)))
)

export default api
