import api from './axiosConfig'

export const itineraryApi = {
  getByTour: (tourId) =>
    api.get(`/tours/${tourId}/itineraries`).then(r => r.data),

  create: (tourId, data) =>
    api.post(`/tours/${tourId}/itineraries`, data).then(r => r.data),

  update: (tourId, itineraryId, data) =>
    api.put(`/tours/${tourId}/itineraries/${itineraryId}`, data).then(r => r.data),

  delete: (tourId, itineraryId) =>
    api.delete(`/tours/${tourId}/itineraries/${itineraryId}`).then(r => r.data),
}
