import api from './axiosConfig'

export const tourApi = {
  getAll: (activeOnly = false) =>
    api.get(`/tours?activeOnly=${activeOnly}`).then(r => r.data),

  getById: (id) =>
    api.get(`/tours/${id}`).then(r => r.data),

  create: (data) =>
    api.post('/tours', data).then(r => r.data),

  update: (id, data) =>
    api.put(`/tours/${id}`, data).then(r => r.data),

  cancel: (id) =>
    api.delete(`/tours/${id}`).then(r => r.data),
}
