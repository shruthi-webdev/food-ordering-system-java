import { useEffect, useMemo, useState } from 'react';
import StepTabs, { StepId } from './components/StepTabs';
import MenuBoard from './components/MenuBoard';
import CartPanel from './components/CartPanel';
import CheckoutPanel from './components/CheckoutPanel';
import InvoicePane from './components/InvoicePane';
import ChatAssistant from './components/ChatAssistant';
import { CartItem, FoodItem, Invoice, PaymentMethod } from './types';
import { addToCart, checkout, fetchCart, fetchMenu, removeFromCart } from './api';

export default function App() {
  const [menu, setMenu] = useState<FoodItem[]>([]);
  const [cart, setCart] = useState<CartItem[]>([]);
  const [step, setStep] = useState<StepId>('browse');
  const [invoice, setInvoice] = useState<Invoice | null>(null);
  const [banner, setBanner] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [busyItem, setBusyItem] = useState<string | null>(null);
  const [checkoutBusy, setCheckoutBusy] = useState(false);

  useEffect(() => {
    const hydrate = async () => {
      try {
        const [menuData, cartData] = await Promise.all([fetchMenu(), fetchCart()]);
        setMenu(menuData);
        setCart(cartData);
      } catch (error) {
        setBanner((error as Error).message);
      }
    };
    hydrate();
  }, []);

  const refreshCart = async () => {
    const next = await fetchCart();
    setCart(next);
  };

  const handleAdd = async (itemId: string, qty: number) => {
    try {
      setBusyItem(itemId);
      await addToCart(itemId, qty);
      await refreshCart();
      setBanner('Added to cart.');
      setStep('cart');
    } catch (error) {
      setBanner((error as Error).message);
    } finally {
      setBusyItem(null);
    }
  };

  const handleRemove = async (itemId: string) => {
    try {
      setLoading(true);
      await removeFromCart(itemId);
      await refreshCart();
    } catch (error) {
      setBanner((error as Error).message);
    } finally {
      setLoading(false);
    }
  };

  const handleCheckout = async (name: string, method: PaymentMethod) => {
    if (!name) return;
    try {
      setCheckoutBusy(true);
      const invoicePayload = await checkout(name, method);
      setInvoice(invoicePayload);
      setStep('receipt');
      setBanner('Order confirmed!');
      await refreshCart();
    } catch (error) {
      setBanner((error as Error).message);
    } finally {
      setCheckoutBusy(false);
    }
  };

  const resetOrder = async () => {
    setInvoice(null);
    setStep('browse');
    setBanner('Start a fresh craving.');
    await refreshCart();
  };

  const subtotal = useMemo(() => {
    return cart.reduce((sum, item) => sum + item.foodItem.price * item.quantity, 0);
  }, [cart]);

  const completedSteps = useMemo<StepId[]>(() => {
    const done: StepId[] = [];
    if (step !== 'browse') done.push('browse');
    if (['billing', 'receipt'].includes(step)) done.push('cart');
    if (step === 'receipt') done.push('billing');
    return done;
  }, [step]);

  const canNavigateTo = (target: StepId) => {
    const order: StepId[] = ['browse', 'cart', 'billing', 'receipt'];
    const currentIndex = order.indexOf(step);
    const targetIndex = order.indexOf(target);
    return targetIndex <= currentIndex;
  };

  return (
    <div className="shell">
      <header className="hero">
        <div>
          <p className="eyebrow">java kitchen</p>
          <h1>code based spice.</h1>
          <p>
           get your customised food data feed, optimized for your cravings and dietary preferences. Syncs seamlessly to your grid for instant access during mealtime.
          </p>
        </div>
        <div className="badge">
          <span>CREDITS LOGGED</span>
          <strong>₹{subtotal.toFixed(2)}</strong>
          <small>auto-syncing to grid</small>
        </div>
      </header>

      <StepTabs
        active={step}
        completed={completedSteps}
        onNavigate={(next) => {
          if (canNavigateTo(next)) {
            setStep(next);
          }
        }}
      />

      {banner && <p className="banner">{banner}</p>}

      <main>
        <MenuBoard menu={menu} onAdd={handleAdd} busyItemId={busyItem} />
        {step === 'browse' || step === 'cart' ? (
          <CartPanel
            items={cart}
            loading={loading}
            onRemove={handleRemove}
            disabled={cart.length === 0}
            onProceed={() => setStep('billing')}
          />
        ) : step === 'billing' ? (
          <CheckoutPanel onCheckout={handleCheckout} busy={checkoutBusy} />
        ) : invoice ? (
          <InvoicePane invoice={invoice} onReset={resetOrder} />
        ) : null}
      </main>

      {/* Floating Chat Assistant */}
      <ChatAssistant onAddToCart={handleAdd} />
    </div>
  );
}
