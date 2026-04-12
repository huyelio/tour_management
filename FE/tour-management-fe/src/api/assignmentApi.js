import api from './axiosConfig'

export const assignmentApi = {
  // Lấy danh sách phân công của tour
  getByTour: (tourId) =>
    api.get(`/assignments/tour/${tourId}`).then(r => r.data),

  // Lưu phân công (nhiều guide cùng lúc)
  // request: { tourId, guides: [{ guideId, role, note }] }
  save: (request) =>
    api.post('/assignments', request).then(r => r.data),

  // Hủy phân công
  cancel: (assignmentId) =>
    api.delete(`/assignments/${assignmentId}`).then(r => r.data),
}
