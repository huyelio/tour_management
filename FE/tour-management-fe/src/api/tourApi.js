import api from './axiosConfig'

export const tourApi = {
  // Lấy tất cả tour
  getAll: (activeOnly = false) =>
    api.get(`/tours?activeOnly=${activeOnly}`).then(r => r.data),

  // Lấy chi tiết tour (kèm danh sách phân công)
  getById: (id) =>
    api.get(`/tours/${id}`).then(r => r.data),
}
