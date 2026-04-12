const LoadingSpinner = ({ text = 'Đang tải...' }) => (
  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', padding: '40px', gap: '16px', color: '#6b7280' }}>
    <div style={{
      width: '40px', height: '40px',
      border: '4px solid #e5e7eb',
      borderTop: '4px solid #3b82f6',
      borderRadius: '50%',
      animation: 'spin 1s linear infinite',
    }} />
    <p>{text}</p>
    <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
  </div>
)

export default LoadingSpinner
