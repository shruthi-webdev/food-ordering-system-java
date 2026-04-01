import { useState, useRef, useEffect } from 'react';
import { ChatMessage, MenuRecommendation } from '../types';
import { sendChatMessage } from '../api';

interface ChatAssistantProps {
  onAddToCart: (itemId: string, qty: number) => Promise<void>;
}

export default function ChatAssistant({ onAddToCart }: ChatAssistantProps) {
  const [open, setOpen] = useState(false);
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      id: 'welcome',
      role: 'assistant',
      text: 'Welcome to the Neon Curry AI. Tell me what you crave — spicy, mild, veg, quick, budget-friendly — and I\'ll find it in the grid.',
      timestamp: Date.now()
    }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [addingId, setAddingId] = useState<string | null>(null);
  const endRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, loading]);

  const handleSend = async () => {
    const trimmed = input.trim();
    if (!trimmed || loading) return;

    const userMsg: ChatMessage = {
      id: 'u-' + Date.now(),
      role: 'user',
      text: trimmed,
      timestamp: Date.now()
    };

    setMessages((prev) => [...prev, userMsg]);
    setInput('');
    setLoading(true);

    try {
      const response = await sendChatMessage(trimmed);
      const botMsg: ChatMessage = {
        id: 'b-' + Date.now(),
        role: 'assistant',
        text: response.message,
        recommendations: response.recommendations,
        intent: response.parsedIntent,
        timestamp: Date.now()
      };
      setMessages((prev) => [...prev, botMsg]);
    } catch {
      const errMsg: ChatMessage = {
        id: 'e-' + Date.now(),
        role: 'assistant',
        text: 'System glitch detected. Please retry your transmission.',
        timestamp: Date.now()
      };
      setMessages((prev) => [...prev, errMsg]);
    } finally {
      setLoading(false);
    }
  };

  const handleAddFromRec = async (rec: MenuRecommendation) => {
    setAddingId(rec.item.id);
    try {
      await onAddToCart(rec.item.id, 1);
      setMessages((prev) => [
        ...prev,
        {
          id: 'cart-' + Date.now(),
          role: 'assistant',
          text: `✓ ${rec.item.name} added to your data cache!`,
          timestamp: Date.now()
        }
      ]);
    } catch {
      setMessages((prev) => [
        ...prev,
        {
          id: 'cerr-' + Date.now(),
          role: 'assistant',
          text: `Failed to add ${rec.item.name}. Try again.`,
          timestamp: Date.now()
        }
      ]);
    } finally {
      setAddingId(null);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <>
      {/* Floating toggle button */}
      <button
        className="chat-fab"
        onClick={() => setOpen(!open)}
        title="AI Food Assistant"
      >
        {open ? '✕' : '🤖'}
      </button>

      {/* Chat panel */}
      {open && (
        <div className="chat-panel">
          <div className="chat-header">
            <span className="chat-header-icon">⚡</span>
            <div>
              <h3>NEON CURRY AI</h3>
              <small>Smart Food Assistant</small>
            </div>
          </div>

          <div className="chat-messages">
            {messages.map((msg) => (
              <div key={msg.id} className={`chat-bubble chat-${msg.role}`}>
                <p className="chat-text">{msg.text}</p>

                {/* Recommendation cards */}
                {msg.recommendations && msg.recommendations.length > 0 && (
                  <div className="rec-cards">
                    {msg.recommendations.map((rec) => (
                      <div key={rec.item.id} className="rec-card">
                        <div
                          className="rec-card-img"
                          style={{ backgroundImage: `url(${rec.item.imageUrl})` }}
                        >
                          <span className="rec-badge-veg">{rec.item.veg ? '🟢 VEG' : '🔴 NON-VEG'}</span>
                          <span className="rec-badge-score">{rec.matchScore}%</span>
                        </div>
                        <div className="rec-card-body">
                          <div className="rec-card-top">
                            <strong>{rec.item.name}</strong>
                            <span className="rec-price">₹{rec.item.price.toFixed(2)}</span>
                          </div>
                          <p className="rec-desc">{rec.item.description}</p>
                          <div className="rec-meta">
                            <span>★ {rec.item.rating}</span>
                            <span>🔥 {rec.item.spiceLevel}</span>
                            <span>⏱ {rec.item.prepTimeMinutes}m</span>
                          </div>
                          <p className="rec-reason">{rec.reason}</p>
                          <button
                            className="rec-add-btn"
                            disabled={addingId === rec.item.id}
                            onClick={() => handleAddFromRec(rec)}
                          >
                            {addingId === rec.item.id ? 'ADDING…' : '+ ADD TO CART'}
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>
                )}

                {/* Intent debug (subtle) */}
                {msg.intent && (msg.intent.contradictory || msg.intent.ambiguous) && (
                  <p className="chat-intent-note">
                    {msg.intent.clarificationNote}
                  </p>
                )}
              </div>
            ))}

            {loading && (
              <div className="chat-bubble chat-assistant">
                <p className="chat-text chat-typing">
                  <span></span><span></span><span></span>
                </p>
              </div>
            )}
            <div ref={endRef} />
          </div>

          <div className="chat-input-bar">
            <input
              type="text"
              placeholder="Try: spicy veg under ₹200..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              disabled={loading}
            />
            <button onClick={handleSend} disabled={loading || !input.trim()}>
              SEND
            </button>
          </div>
        </div>
      )}
    </>
  );
}
