export type StepId = 'browse' | 'cart' | 'billing' | 'receipt';

const labels: Record<StepId, string> = {
  browse: 'Browse',
  cart: 'Cart',
  billing: 'Billing',
  receipt: 'Receipt'
};

interface StepTabsProps {
  active: StepId;
  completed: StepId[];
  onNavigate?: (step: StepId) => void;
}

export default function StepTabs({ active, completed, onNavigate }: StepTabsProps) {
  const steps: StepId[] = ['browse', 'cart', 'billing', 'receipt'];
  return (
    <nav className="step-tabs">
      {steps.map((step, idx) => {
        const status = step === active ? 'active' : completed.includes(step) ? 'done' : 'idle';
        return (
          <button
            key={step}
            data-status={status}
            disabled={status === 'idle' && step !== active}
            onClick={() => onNavigate?.(step)}
          >
            <span>{String(idx + 1).padStart(2, '0')}</span>
            {labels[step]}
          </button>
        );
      })}
    </nav>
  );
}
