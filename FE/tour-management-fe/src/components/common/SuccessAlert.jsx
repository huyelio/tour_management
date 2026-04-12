const SuccessAlert = ({ message, onClose }) => (
  <div style={{
    background: '#f0fdf4', border: '1px solid #bbf7d0', borderRadius: '8px',
    padding: '12px 16px', color: '#16a34a', marginBottom: '16px',
    display: 'flex', justifyContent: 'space-between', alignItems: 'center',
  }}>
    <span>✅ {message}</span>
    {onClose && (
      <button onClick={onClose} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#16a34a', fontSize: '18px' }}>×</button>
    )}
  </div>
)

export default SuccessAlert
