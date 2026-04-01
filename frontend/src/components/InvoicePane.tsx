import { Invoice } from '../types';

interface InvoicePaneProps {
  invoice: Invoice;
  onReset: () => void;
}

export default function InvoicePane({ invoice, onReset }: InvoicePaneProps) {
  const currency = new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR'
  });

  return (
    <section className="panel">
      <header>
        <p className="eyebrow">Receipt #{invoice.orderId}</p>
        <h2>Bon appétit!</h2>
      </header>
      <ul className="cart-list">
        {invoice.lineItems.map((item) => (
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
              <span>{item.quantity} × {currency.format(item.foodItem.price)}</span>
            </div>
            <span>{currency.format(item.foodItem.price * item.quantity)}</span>
          </li>
        ))}
      </ul>
      <div className="invoice-totals">
        <div>
          <span>Subtotal</span>
          <strong>{currency.format(invoice.subTotal)}</strong>
        </div>
        <div>
          <span>Tax</span>
          <strong>{currency.format(invoice.tax)}</strong>
        </div>
        <div>
          <span>Total</span>
          <strong>{currency.format(invoice.total)}</strong>
        </div>
        <div>
          <span>Payment</span>
          <strong>{invoice.paymentMethod}</strong>
        </div>
        <div>
          <span>Issued</span>
          <strong>{new Date(invoice.issuedAt).toLocaleString()}</strong>
        </div>
      </div>
      <button className="ghost" onClick={onReset}>New order</button>
    </section>
  );
}
