import { CartItem } from '../types';

interface CartPanelProps {
  items: CartItem[];
  loading?: boolean;
  onRemove: (id: string) => Promise<void>;
  onProceed: () => void;
  disabled?: boolean;
}

export default function CartPanel({ items, loading, onRemove, onProceed, disabled }: CartPanelProps) {
  const currency = new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR'
  });

  const subtotal = items.reduce((sum, item) => sum + item.foodItem.price * item.quantity, 0);

  return (
    <section className="panel">
      <header>
        <p className="eyebrow">Data Cache</p>
        <h2>Review your uplink</h2>
      </header>
      <ul className="cart-list">
        {items.map((item) => (
          <li key={item.foodItem.id}>
            {item.foodItem.imageUrl && (
              <img
                src={item.foodItem.imageUrl}
                alt={item.foodItem.name}
                style={{ width: '40px', height: '40px', borderRadius: '4px', objectFit: 'cover', marginRight: '1rem' }}
              />
            )}
            <div style={{ flex: 1 }}>
              <strong>{item.foodItem.name}</strong>
              <span>
                {item.quantity} × {currency.format(item.foodItem.price)}
              </span>
            </div>
            <div>
              <span>{currency.format(item.foodItem.price * item.quantity)}</span>
              <button onClick={() => { void onRemove(item.foodItem.id); }}>Remove</button>
            </div>
          </li>
        ))}
        {items.length === 0 && <li className="empty">Your data cache is empty.</li>}
      </ul>
      <div className="cart-summary">
        <span>Subtotal</span>
        <strong>{currency.format(subtotal)}</strong>
      </div>
      <button className="primary" disabled={disabled || items.length === 0 || loading} onClick={onProceed}>
        {loading ? 'Processing…' : 'INITIALIZE BILLING'}
      </button>
    </section>
  );
}
