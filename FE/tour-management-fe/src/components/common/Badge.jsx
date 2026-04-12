const Badge = ({ label, color, size = 'sm' }) => {
  const sizes = {
    sm: { fontSize: '11px', padding: '2px 8px' },
    md: { fontSize: '13px', padding: '4px 12px' },
  }
  return (
    <span style={{
      display: 'inline-block',
      backgroundColor: color + '20',
      color: color,
      border: `1px solid ${color}40`,
      borderRadius: '20px',
      fontWeight: 600,
      ...sizes[size],
    }}>
      {label}
    </span>
  )
}

export default Badge
