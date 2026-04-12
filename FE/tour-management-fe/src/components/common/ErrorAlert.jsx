const ErrorAlert = ({ message, onClose }) => (
  <div style={{
    background: '#fef2f2', border: '1px solid #fecaca', borderRadius: '8px',
    padding: '12px 16px', color: '#dc2626', marginBottom: '16px',
    display: 'flex', justifyContent: 'space-between', alignItems: 'center',
  }}>
    <span>⚠️ {message}</span>
    {onClose && (
      <button onClick={onClose} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#dc2626', fontSize: '18px' }}>×</button>
    )}
  </div>
)

export default ErrorAlert
