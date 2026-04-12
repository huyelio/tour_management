import api from './axiosConfig'

export const guideApi = {
  // Lấy danh sách hướng dẫn viên (có thể lọc)
  getAll: (filters = {}) => {
    const params = new URLSearchParams()
    if (filters.status) params.append('status', filters.status)
    if (filters.specialization) params.append('specialization', filters.specialization)
    if (filters.language) params.append('language', filters.language)
    if (filters.region) params.append('region', filters.region)
    return api.get(`/guides?${params.toString()}`).then(r => r.data)
  },

  // Lấy chi tiết một hướng dẫn viên
  getById: (id) =>
    api.get(`/guides/${id}`).then(r => r.data),
}
