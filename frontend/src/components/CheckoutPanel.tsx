import { useState } from 'react';
import { PaymentMethod } from '../types';

interface CheckoutPanelProps {
  onCheckout: (name: string, method: PaymentMethod) => Promise<void>;
  busy?: boolean;
}

const paymentLabels: Record<PaymentMethod, string> = {
  cash: 'Physical Credits',
  card: 'Cyber Card',
  wallet: 'Neural Wallet'
};

export default function CheckoutPanel({ onCheckout, busy }: CheckoutPanelProps) {
  const [name, setName] = useState('');
  const [method, setMethod] = useState<PaymentMethod>('card');

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    await onCheckout(name.trim(), method);
  };

  return (
    <section className="panel">
      <header>
        <p className="eyebrow">Network Firewall</p>
        <h2>Secure Credit Transfer</h2>
      </header>
      <form className="checkout-form" onSubmit={handleSubmit}>
        <label>
          Customer name
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Alex Diner"
            required
          />
        </label>
        <fieldset>
          <legend>Payment method</legend>
          {Object.entries(paymentLabels).map(([value, label]) => (
            <label key={value} className="radio">
              <input
                type="radio"
                name="payment"
                value={value}
                checked={method === value}
                onChange={() => setMethod(value as PaymentMethod)}
              />
              <span>{label}</span>
            </label>
          ))}
        </fieldset>
        <button className="primary" type="submit" disabled={busy}>
          {busy ? 'Authenticating…' : 'TRANSMIT ORDER'}
        </button>
      </form>
    </section>
  );
}
