import Badge from '../common/Badge'
import { getGuideStatusColor, getGuideStatusLabel } from '../../utils/helpers'

const GuideCard = ({ guide, selectable, selected, onSelect, alreadyAssigned }) => {
  const isDisabled = guide.status !== 'AVAILABLE' || alreadyAssigned

  return (
    <div
      onClick={() => !isDisabled && selectable && onSelect && onSelect(guide)}
      style={{
        background: '#fff',
        borderRadius: '10px',
        border: selected ? '2px solid #3b82f6' : '1px solid #e5e7eb',
        padding: '16px',
        cursor: isDisabled ? 'not-allowed' : (selectable ? 'pointer' : 'default'),
        opacity: isDisabled ? 0.6 : 1,
        transition: 'all 0.2s',
        position: 'relative',
        boxShadow: selected ? '0 0 0 3px rgba(59,130,246,0.2)' : 'none',
      }}
    >
      {selected && (
        <div style={{
          position: 'absolute', top: '10px', right: '10px',
          background: '#3b82f6', color: '#fff', borderRadius: '50%',
          width: '22px', height: '22px', display: 'flex', alignItems: 'center', justifyContent: 'center',
          fontSize: '13px', fontWeight: 700,
        }}>✓</div>
      )}

      {alreadyAssigned && (
        <div style={{
          position: 'absolute', top: '10px', right: '10px',
          background: '#10b981', color: '#fff', borderRadius: '12px',
          padding: '2px 8px', fontSize: '11px', fontWeight: 700,
        }}>Đã phân công</div>
      )}

      <div style={{ display: 'flex', gap: '12px', alignItems: 'flex-start' }}>
        <div style={{
          width: '44px', height: '44px', borderRadius: '50%',
          background: 'linear-gradient(135deg, #667eea, #764ba2)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: '#fff', fontSize: '18px', fontWeight: 700, flexShrink: 0,
        }}>
          {guide.fullName.charAt(0)}
        </div>
        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{ fontSize: '12px', color: '#9ca3af' }}>{guide.code}</div>
          <div style={{ fontWeight: 700, color: '#111827', fontSize: '15px' }}>{guide.fullName}</div>
          <div style={{ marginTop: '6px' }}>
            <Badge label={getGuideStatusLabel(guide.status)} color={getGuideStatusColor(guide.status)} />
          </div>
        </div>
      </div>

      <div style={{ marginTop: '12px', fontSize: '13px', color: '#6b7280', display: 'flex', flexDirection: 'column', gap: '4px' }}>
        <div>🌍 Ngoại ngữ: <strong style={{ color: '#374151' }}>{guide.languages}</strong></div>
        <div>🎓 Chuyên môn: <strong style={{ color: '#374151' }}>{guide.specialization}</strong></div>
        <div>📍 Khu vực: <strong style={{ color: '#374151' }}>{guide.region}</strong></div>
        <div>⭐ Kinh nghiệm: <strong style={{ color: '#374151' }}>{guide.experienceYears} năm</strong></div>
        {guide.phone && <div>📞 <strong style={{ color: '#374151' }}>{guide.phone}</strong></div>}
      </div>
    </div>
  )
}

export default GuideCard
