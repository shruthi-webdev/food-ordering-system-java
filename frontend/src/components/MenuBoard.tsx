import { useState } from 'react';
import { FoodItem } from '../types';

interface MenuBoardProps {
  menu: FoodItem[];
  onAdd: (id: string, qty: number) => Promise<void>;
  busyItemId?: string | null;
}

export default function MenuBoard({ menu, onAdd, busyItemId }: MenuBoardProps) {
  const [quantities, setQuantities] = useState<Record<string, number>>({});

  const currency = new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR'
  });

  const handleAdd = async (item: FoodItem) => {
    const qty = Math.max(1, quantities[item.id] ?? 1);
    await onAdd(item.id, qty);
    setQuantities((prev) => ({ ...prev, [item.id]: 1 }));
  };

  return (
    <section className="panel">
      <header>
        <p className="eyebrow">Neon Spice Grid</p>
        <h2>Cybertandoor Selections</h2>
      </header>
      <div className="menu-grid">
        {menu.map((item) => (
          <article key={item.id}>
            {item.imageUrl && (
              <div
                className="menu-item-image"
                style={{ backgroundImage: `url(${item.imageUrl})` }}
              >
                {item.rating && <span className="rating-badge">★ {item.rating}</span>}
              </div>
            )}
            <div className="content-wrap">
              <h3>{item.name}</h3>
              <p>{item.description}</p>
            </div>
            <footer>
              <strong>{currency.format(item.price)}</strong>
              <label>
                Qty
                <input
                  type="number"
                  min={1}
                  max={9}
                  value={quantities[item.id] ?? 1}
                  onChange={(e) =>
                    setQuantities((prev) => ({
                      ...prev,
                      [item.id]: Number(e.target.value)
                    }))
                  }
                />
              </label>
              <button
                className="primary"
                disabled={busyItemId === item.id}
                onClick={() => handleAdd(item)}
              >
                {busyItemId === item.id ? 'UPLOADING…' : 'ADD TO CART'}
              </button>
            </footer>
          </article>
        ))}
        {menu.length === 0 && (
          <p className="empty">Establishing uplink to the kitchen…</p>
        )}
      </div>
    </section>
  );
}
