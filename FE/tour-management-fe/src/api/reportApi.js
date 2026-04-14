import api from './axiosConfig'

export const reportApi = {
  /**
   * GET /api/reports/tour-revenue
   * @param {Object} params - { fromDate, toDate, status, destination, sortBy }
   */
  getTourRevenue: (params = {}) => {
    const query = new URLSearchParams()
    if (params.fromDate)    query.append('fromDate', params.fromDate)
    if (params.toDate)      query.append('toDate', params.toDate)
    if (params.status)      query.append('status', params.status)
    if (params.destination) query.append('destination', params.destination)
    if (params.sortBy)      query.append('sortBy', params.sortBy)
    const qs = query.toString()
    return api.get(`/reports/tour-revenue${qs ? '?' + qs : ''}`).then(r => r.data)
  },
}
