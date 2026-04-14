import { useState, useEffect, useCallback } from 'react'
import { reportApi } from '../api/reportApi'
import LoadingSpinner from '../components/common/LoadingSpinner'
import ErrorAlert from '../components/common/ErrorAlert'
import { formatCurrency, formatDate, getTourStatusLabel, getTourStatusColor } from '../utils/helpers'
import Badge from '../components/common/Badge'

// ─── Helpers ─────────────────────────────────────────────────────────────────

const formatCurrencyShort = (amount) => {
  if (!amount) return '0'
  if (amount >= 1_000_000_000) return (amount / 1_000_000_000).toFixed(1) + ' tỷ'
  if (amount >= 1_000_000) return (amount / 1_000_000).toFixed(1) + ' tr'
  return (amount / 1_000).toFixed(0) + ' k'
}

const TOUR_STATUSES = [
  { value: '', label: 'Tất cả trạng thái' },
  { value: 'PLANNING',  label: 'Lên kế hoạch' },
  { value: 'OPEN',      label: 'Mở bán' },
  { value: 'FULL',      label: 'Đã đủ khách' },
  { value: 'ONGOING',   label: 'Đang diễn ra' },
  { value: 'COMPLETED', label: 'Hoàn thành' },
  { value: 'CANCELLED', label: 'Đã hủy' },
]

const SORT_OPTIONS = [
  { value: 'revenue_desc', label: 'Doanh thu giảm dần' },
  { value: 'date_desc',    label: 'Ngày mới nhất' },
  { value: 'name_asc',     label: 'Tên A–Z' },
]

// ─── Sub-component: Summary Cards ────────────────────────────────────────────

const SummaryCard = ({ icon, label, value, color }) => (
  <div style={{
    background: '#fff', borderRadius: '12px', padding: '20px 24px',
    border: `1px solid ${color}33`, flex: 1, minWidth: '160px',
  }}>
    <div style={{ fontSize: '28px', marginBottom: '6px' }}>{icon}</div>
    <div style={{ fontSize: '22px', fontWeight: 800, color }}>{value}</div>
    <div style={{ fontSize: '13px', color: '#6b7280', marginTop: '4px' }}>{label}</div>
  </div>
)

// ─── Sub-component: Bar Chart (pure SVG, no dependency) ──────────────────────

const RevenueBarChart = ({ data }) => {
  if (!data || data.length === 0) return null

  const top5 = [...data]
    .sort((a, b) => (b.totalRevenue || 0) - (a.totalRevenue || 0))
    .slice(0, 5)

  const maxRevenue = Math.max(...top5.map(d => d.totalRevenue || 0), 1)
  const chartH = 180
  const barW = 60
  const gap  = 28
  const paddingLeft = 8
  const totalW = top5.length * (barW + gap) + paddingLeft

  const colors = ['#2563eb', '#3b82f6', '#60a5fa', '#93c5fd', '#bfdbfe']

  return (
    <div style={{ overflowX: 'auto' }}>
      <svg width={totalW} height={chartH + 72} style={{ display: 'block' }}>
        {top5.map((item, i) => {
          const barH = Math.max(((item.totalRevenue || 0) / maxRevenue) * chartH, 4)
          const x = paddingLeft + i * (barW + gap)
          const y = chartH - barH
          return (
            <g key={item.tourId}>
              {/* Bar */}
              <rect x={x} y={y} width={barW} height={barH}
                fill={colors[i % colors.length]} rx="5" />
              {/* Revenue label above bar */}
              <text x={x + barW / 2} y={y - 6} textAnchor="middle"
                fontSize="11" fill="#374151" fontWeight="600">
                {formatCurrencyShort(item.totalRevenue)}
              </text>
              {/* Tour code below bar */}
              <text x={x + barW / 2} y={chartH + 18} textAnchor="middle"
                fontSize="11" fill="#374151">
                {item.tourCode}
              </text>
              {/* Tour name (truncated) below code */}
              <text x={x + barW / 2} y={chartH + 34} textAnchor="middle"
                fontSize="10" fill="#9ca3af">
                {item.tourName.length > 12 ? item.tourName.slice(0, 11) + '…' : item.tourName}
              </text>
              {/* Guests label */}
              <text x={x + barW / 2} y={chartH + 52} textAnchor="middle"
                fontSize="10" fill="#6b7280">
                {item.totalGuests} khách
              </text>
            </g>
          )
        })}
        {/* Baseline */}
        <line x1={0} y1={chartH} x2={totalW} y2={chartH}
          stroke="#e5e7eb" strokeWidth="1" />
      </svg>
    </div>
  )
}

// ─── Main Page ────────────────────────────────────────────────────────────────

const RevenueReportPage = () => {
  const [filters, setFilters] = useState({
    fromDate: '', toDate: '', status: '', destination: '', sortBy: 'revenue_desc',
  })
  const [report, setReport]   = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError]     = useState(null)

  const fetchReport = useCallback(async (params) => {
    try {
      setLoading(true)
      setError(null)
      const res = await reportApi.getTourRevenue(params)
      setReport(res.data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }, [])

  // Tải dữ liệu lần đầu với filter mặc định
  useEffect(() => {
    fetchReport(filters)
  }, []) // eslint-disable-line react-hooks/exhaustive-deps

  const handleSearch = (e) => {
    e.preventDefault()
    fetchReport(filters)
  }

  const handleReset = () => {
    const defaults = { fromDate: '', toDate: '', status: '', destination: '', sortBy: 'revenue_desc' }
    setFilters(defaults)
    fetchReport(defaults)
  }

  const inputStyle = {
    border: '1px solid #d1d5db', borderRadius: '8px', padding: '8px 12px',
    fontSize: '14px', outline: 'none', background: '#fff',
  }
  const labelStyle = { fontSize: '13px', color: '#374151', fontWeight: 600, marginBottom: '4px', display: 'block' }

  return (
    <div>
      {/* Page Header */}
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ margin: '0 0 6px', fontSize: '24px', fontWeight: 800, color: '#111827' }}>
          📊 Thống kê doanh thu tour
        </h1>
        <p style={{ margin: 0, fontSize: '14px', color: '#6b7280' }}>
          Doanh thu được tính từ các booking có trạng thái <strong>CONFIRMED</strong> hoặc <strong>COMPLETED</strong>.
          Booking CANCELLED không được tính.
        </p>
      </div>

      {/* Filter Form */}
      <form onSubmit={handleSearch} style={{
        background: '#fff', borderRadius: '12px', padding: '20px',
        border: '1px solid #e5e7eb', marginBottom: '24px',
      }}>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))', gap: '16px' }}>
          <div>
            <label style={labelStyle}>Từ ngày</label>
            <input type="date" style={inputStyle} value={filters.fromDate}
              onChange={e => setFilters(p => ({ ...p, fromDate: e.target.value }))} />
          </div>
          <div>
            <label style={labelStyle}>Đến ngày</label>
            <input type="date" style={inputStyle} value={filters.toDate}
              onChange={e => setFilters(p => ({ ...p, toDate: e.target.value }))} />
          </div>
          <div>
            <label style={labelStyle}>Trạng thái tour</label>
            <select style={inputStyle} value={filters.status}
              onChange={e => setFilters(p => ({ ...p, status: e.target.value }))}>
              {TOUR_STATUSES.map(opt => (
                <option key={opt.value} value={opt.value}>{opt.label}</option>
              ))}
            </select>
          </div>
          <div>
            <label style={labelStyle}>Điểm đến</label>
            <input type="text" style={inputStyle} placeholder="VD: Hà Nội, Đà Lạt..."
              value={filters.destination}
              onChange={e => setFilters(p => ({ ...p, destination: e.target.value }))} />
          </div>
          <div>
            <label style={labelStyle}>Sắp xếp theo</label>
            <select style={inputStyle} value={filters.sortBy}
              onChange={e => setFilters(p => ({ ...p, sortBy: e.target.value }))}>
              {SORT_OPTIONS.map(opt => (
                <option key={opt.value} value={opt.value}>{opt.label}</option>
              ))}
            </select>
          </div>
        </div>

        <div style={{ display: 'flex', gap: '10px', marginTop: '16px' }}>
          <button type="submit" style={{
            padding: '9px 20px', background: '#1d4ed8', color: '#fff',
            border: 'none', borderRadius: '8px', cursor: 'pointer',
            fontSize: '14px', fontWeight: 700,
          }}>
            🔍 Tìm kiếm
          </button>
          <button type="button" onClick={handleReset} style={{
            padding: '9px 20px', background: '#fff', color: '#374151',
            border: '1px solid #d1d5db', borderRadius: '8px', cursor: 'pointer',
            fontSize: '14px',
          }}>
            Đặt lại
          </button>
        </div>
      </form>

      {/* Loading / Error */}
      {loading && <LoadingSpinner text="Đang tải báo cáo doanh thu..." />}
      {error && <ErrorAlert message={error} onClose={() => setError(null)} />}

      {/* Results */}
      {!loading && report && (
        <>
          {/* Summary Cards */}
          <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap', marginBottom: '24px' }}>
            <SummaryCard
              icon="💰"
              label="Tổng doanh thu"
              value={formatCurrency(report.summary.totalRevenue)}
              color="#2563eb"
            />
            <SummaryCard
              icon="👥"
              label="Tổng số khách"
              value={report.summary.totalGuests.toLocaleString('vi-VN') + ' khách'}
              color="#10b981"
            />
            <SummaryCard
              icon="🗺️"
              label="Tổng số tour"
              value={report.summary.totalTours + ' tour'}
              color="#f59e0b"
            />
          </div>

          {report.tours.length === 0 ? (
            <div style={{
              background: '#fff', borderRadius: '12px', padding: '48px',
              border: '1px solid #e5e7eb', textAlign: 'center', color: '#9ca3af',
            }}>
              <div style={{ fontSize: '40px', marginBottom: '12px' }}>📭</div>
              <p style={{ margin: 0, fontSize: '15px' }}>Không có dữ liệu phù hợp với điều kiện lọc.</p>
            </div>
          ) : (
            <>
              {/* Bar Chart */}
              <div style={{
                background: '#fff', borderRadius: '12px', padding: '20px',
                border: '1px solid #e5e7eb', marginBottom: '24px',
              }}>
                <h3 style={{ margin: '0 0 16px', fontSize: '16px', fontWeight: 700, color: '#111827' }}>
                  🏆 Top 5 tour doanh thu cao nhất
                </h3>
                <RevenueBarChart data={report.tours} />
              </div>

              {/* Table */}
              <div style={{
                background: '#fff', borderRadius: '12px',
                border: '1px solid #e5e7eb', overflow: 'hidden',
              }}>
                <div style={{ padding: '16px 20px', borderBottom: '1px solid #f3f4f6', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <h3 style={{ margin: 0, fontSize: '16px', fontWeight: 700, color: '#111827' }}>
                    📋 Chi tiết doanh thu ({report.tours.length} tour)
                  </h3>
                </div>
                <div style={{ overflowX: 'auto' }}>
                  <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '14px' }}>
                    <thead>
                      <tr style={{ background: '#f8fafc' }}>
                        {['Mã tour', 'Tên tour', 'Điểm đến', 'Ngày khởi hành', 'Tổng khách', 'Doanh thu'].map(h => (
                          <th key={h} style={{
                            padding: '12px 16px', textAlign: h === 'Doanh thu' || h === 'Tổng khách' ? 'right' : 'left',
                            fontSize: '12px', fontWeight: 700, color: '#6b7280',
                            textTransform: 'uppercase', letterSpacing: '0.05em',
                            borderBottom: '1px solid #e5e7eb', whiteSpace: 'nowrap',
                          }}>
                            {h}
                          </th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {report.tours.map((t, idx) => (
                        <tr key={t.tourId} style={{
                          borderBottom: '1px solid #f3f4f6',
                          background: idx % 2 === 0 ? '#fff' : '#fafafa',
                        }}>
                          <td style={{ padding: '12px 16px', whiteSpace: 'nowrap' }}>
                            <span style={{
                              background: '#eff6ff', color: '#2563eb',
                              borderRadius: '6px', padding: '2px 8px', fontSize: '12px', fontWeight: 700,
                            }}>
                              {t.tourCode}
                            </span>
                          </td>
                          <td style={{ padding: '12px 16px', color: '#111827', fontWeight: 500 }}>
                            {t.tourName}
                          </td>
                          <td style={{ padding: '12px 16px', color: '#6b7280' }}>
                            📍 {t.destination}
                          </td>
                          <td style={{ padding: '12px 16px', color: '#6b7280', whiteSpace: 'nowrap' }}>
                            {formatDate(t.startDate)}
                          </td>
                          <td style={{ padding: '12px 16px', textAlign: 'right', fontWeight: 600, color: '#374151' }}>
                            {(t.totalGuests || 0).toLocaleString('vi-VN')}
                          </td>
                          <td style={{ padding: '12px 16px', textAlign: 'right', whiteSpace: 'nowrap' }}>
                            {t.totalRevenue > 0 ? (
                              <span style={{ fontWeight: 700, color: '#2563eb', fontSize: '15px' }}>
                                {formatCurrency(t.totalRevenue)}
                              </span>
                            ) : (
                              <span style={{ color: '#9ca3af', fontSize: '13px' }}>—</span>
                            )}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                    <tfoot>
                      <tr style={{ background: '#f0f9ff', borderTop: '2px solid #bfdbfe' }}>
                        <td colSpan={4} style={{ padding: '12px 16px', fontWeight: 700, color: '#1e40af' }}>
                          Tổng cộng
                        </td>
                        <td style={{ padding: '12px 16px', textAlign: 'right', fontWeight: 700, color: '#1e40af' }}>
                          {report.summary.totalGuests.toLocaleString('vi-VN')}
                        </td>
                        <td style={{ padding: '12px 16px', textAlign: 'right', fontWeight: 700, color: '#1e40af', fontSize: '15px', whiteSpace: 'nowrap' }}>
                          {formatCurrency(report.summary.totalRevenue)}
                        </td>
                      </tr>
                    </tfoot>
                  </table>
                </div>
              </div>
            </>
          )}
        </>
      )}
    </div>
  )
}

export default RevenueReportPage
