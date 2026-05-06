import api from './axiosConfig'

export const assignmentApi = {
  // Lấy danh sách phân công của tour
  getByTour: (tourId) =>
    api.get(`/assignments/tour/${tourId}`).then(r => r.data),

  // Body: [ { tour: { id }, guide: { id }, role, note }, ... ]
  save: (assignments) =>
    api.post('/assignments', assignments).then(r => r.data),

  // Hủy phân công
  cancel: (assignmentId) =>
    api.delete(`/assignments/${assignmentId}`).then(r => r.data),
}
