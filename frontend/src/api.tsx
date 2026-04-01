import { CartItem, FoodItem, Invoice, PaymentMethod, MenuItem, ParsedFoodIntent, MenuRecommendation, ChatRecommendationResponse } from './types';

// In-memory mock database to ensure the app works beautifully without the backend running
const mockMenu: FoodItem[] = [
  {
    id: 'butter-chicken',
    name: 'Butter Chicken',
    description: 'Tender chicken cooked in a rich, creamy tomato gravy with a hint of fenugreek',
    price: 290,
    rating: 4.8,
    imageUrl: 'https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?auto=format&fit=crop&q=80&w=800'
  },
  {
    id: 'paneer-tikka',
    name: 'Paneer Tikka',
    description: 'Cottage cheese cubes marinated in yogurt and spices, grilled to perfection',
    price: 220,
    rating: 4.5,
    imageUrl: 'https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?auto=format&fit=crop&q=80&w=800'
  },
  {
    id: 'biryani',
    name: 'Hyderabadi Dum Biryani',
    description: 'Fragrant basmati rice layered with marinated meat and slow-cooked in a sealed pot',
    price: 320,
    rating: 4.9,
    imageUrl: 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&q=80&w=800'
  },
  {
    id: 'samosa',
    name: 'Punjabi Samosas',
    description: 'Golden, crispy pastry filled with spiced potatoes and peas, served with mint chutney',
    price: 110,
    rating: 4.7,
    imageUrl: 'https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&q=80&w=800'
  },
  {
    id: 'naan',
    name: 'Garlic Butter Naan',
    description: 'Soft, fluffy Indian bread baked in a tandoor, brushed with garlic butter',
    price: 70,
    rating: 4.6,
    imageUrl: 'https://images.unsplash.com/photo-1626200271501-c85df8ca575f?auto=format&fit=crop&q=80&w=800'
  },
  {
    id: 'palak-paneer',
    name: 'Palak Paneer',
    description: 'Soft paneer cubes simmered in a creamy, spiced spinach sauce',
    price: 250,
    rating: 4.4,
    imageUrl: 'https://images.unsplash.com/photo-1601050690117-94f5f6af3bb0?auto=format&fit=crop&q=80&w=800'
  }
];

let mockCart: CartItem[] = [];

// Simulate network delay
const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

export async function fetchMenu(): Promise<FoodItem[]> {
  await delay(400);
  return mockMenu;
}

export async function fetchCart(): Promise<CartItem[]> {
  await delay(200);
  return [...mockCart];
}

export async function addToCart(itemId: string, quantity: number): Promise<CartItem> {
  await delay(300);
  const foodItem = mockMenu.find((item) => item.id === itemId);
  if (!foodItem) throw new Error('Item not found');

  const existing = mockCart.find((ci) => ci.id === itemId);
  if (existing) {
    existing.quantity += quantity;
    return existing;
  } else {
    const newItem: CartItem = { id: itemId, foodItem, quantity };
    mockCart.push(newItem);
    return newItem;
  }
}

export async function removeFromCart(itemId: string): Promise<void> {
  await delay(300);
  mockCart = mockCart.filter((ci) => ci.id !== itemId);
}

export async function checkout(customerName: string, paymentMethod: PaymentMethod): Promise<Invoice> {
  await delay(600);
  if (mockCart.length === 0) throw new Error('Cart is empty');
  
  const subTotal = mockCart.reduce((sum, item) => sum + item.foodItem.price * item.quantity, 0);
  const tax = subTotal * 0.1;
  const total = subTotal + tax;
  const lineItems = [...mockCart];
  mockCart = []; // clear cart on checkout
  
  return {
    orderId: 'ORD-' + Math.floor(Math.random() * 1000000),
    customerName,
    lineItems,
    subTotal,
    tax,
    total,
    paymentMethod,
    issuedAt: new Date().toISOString(),
    status: 'CONFIRMED'
  };
}

// ═══════════════════════════════════════════════════════
// CHAT ASSISTANT — Mock LLM + Rule Engine (mirrors Java backend)
// ═══════════════════════════════════════════════════════

const extendedMenu: MenuItem[] = [
  {
    id: 'butter-chicken', name: 'Butter Chicken',
    description: 'Tender chicken in rich creamy tomato gravy',
    price: 290, rating: 4.8,
    imageUrl: 'https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?auto=format&fit=crop&q=80&w=800',
    veg: false, spiceLevel: 'MEDIUM', prepTimeMinutes: 20, available: true, stockCount: 15,
    containsOnion: true, containsNuts: false, mealType: 'DINNER', tags: ['popular']
  },
  {
    id: 'paneer-tikka', name: 'Paneer Tikka',
    description: 'Cottage cheese marinated in yogurt and spices, grilled',
    price: 220, rating: 4.5,
    imageUrl: 'https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?auto=format&fit=crop&q=80&w=800',
    veg: true, spiceLevel: 'SPICY', prepTimeMinutes: 15, available: true, stockCount: 20,
    containsOnion: true, containsNuts: false, mealType: 'DINNER', tags: ['popular']
  },
  {
    id: 'biryani', name: 'Hyderabadi Dum Biryani',
    description: 'Fragrant basmati rice with marinated meat, slow-cooked',
    price: 320, rating: 4.9,
    imageUrl: 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&q=80&w=800',
    veg: false, spiceLevel: 'SPICY', prepTimeMinutes: 35, available: true, stockCount: 10,
    containsOnion: true, containsNuts: false, mealType: 'LUNCH', tags: ['chef-special']
  },
  {
    id: 'samosa', name: 'Punjabi Samosas',
    description: 'Golden crispy pastry filled with spiced potatoes and peas',
    price: 110, rating: 4.7,
    imageUrl: 'https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&q=80&w=800',
    veg: true, spiceLevel: 'MEDIUM', prepTimeMinutes: 10, available: true, stockCount: 50,
    containsOnion: true, containsNuts: false, mealType: 'SNACK', tags: ['quick', 'budget']
  },
  {
    id: 'naan', name: 'Garlic Butter Naan',
    description: 'Soft fluffy bread baked in tandoor, brushed with garlic butter',
    price: 70, rating: 4.6,
    imageUrl: 'https://images.unsplash.com/photo-1626200271501-c85df8ca575f?auto=format&fit=crop&q=80&w=800',
    veg: true, spiceLevel: 'MILD', prepTimeMinutes: 8, available: true, stockCount: 100,
    containsOnion: false, containsNuts: false, mealType: 'ANY', tags: ['quick', 'budget']
  },
  {
    id: 'palak-paneer', name: 'Palak Paneer',
    description: 'Soft paneer cubes in creamy spiced spinach sauce',
    price: 250, rating: 4.4,
    imageUrl: 'https://images.unsplash.com/photo-1601050690117-94f5f6af3bb0?auto=format&fit=crop&q=80&w=800',
    veg: true, spiceLevel: 'MILD', prepTimeMinutes: 18, available: true, stockCount: 12,
    containsOnion: false, containsNuts: false, mealType: 'LUNCH', tags: ['healthy']
  },
  {
    id: 'masala-dosa', name: 'Masala Dosa',
    description: 'Crispy rice crepe stuffed with spiced potato filling',
    price: 140, rating: 4.6,
    imageUrl: 'https://images.unsplash.com/photo-1589301760014-d929f3979dbc?auto=format&fit=crop&q=80&w=800',
    veg: true, spiceLevel: 'MEDIUM', prepTimeMinutes: 12, available: true, stockCount: 25,
    containsOnion: true, containsNuts: false, mealType: 'BREAKFAST', tags: ['popular', 'quick']
  },
  {
    id: 'chole-bhature', name: 'Chole Bhature',
    description: 'Spiced chickpea curry with deep-fried fluffy bread',
    price: 170, rating: 4.5,
    imageUrl: 'https://images.unsplash.com/photo-1626132647523-66f5bf380027?auto=format&fit=crop&q=80&w=800',
    veg: true, spiceLevel: 'SPICY', prepTimeMinutes: 20, available: true, stockCount: 18,
    containsOnion: true, containsNuts: false, mealType: 'LUNCH', tags: ['popular']
  },
  {
    id: 'dal-makhani', name: 'Dal Makhani',
    description: 'Slow-cooked black lentils in buttery tomato cream sauce',
    price: 200, rating: 4.7,
    imageUrl: 'https://images.unsplash.com/photo-1585937421612-70a008356fbe?auto=format&fit=crop&q=80&w=800',
    veg: true, spiceLevel: 'MILD', prepTimeMinutes: 25, available: true, stockCount: 15,
    containsOnion: true, containsNuts: false, mealType: 'DINNER', tags: ['comfort']
  },
  {
    id: 'chicken-tikka', name: 'Chicken Tikka',
    description: 'Juicy boneless chicken marinated and chargrilled in tandoor',
    price: 260, rating: 4.6,
    imageUrl: 'https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?auto=format&fit=crop&q=80&w=800',
    veg: false, spiceLevel: 'SPICY', prepTimeMinutes: 18, available: true, stockCount: 20,
    containsOnion: true, containsNuts: false, mealType: 'DINNER', tags: ['popular']
  }
];

// ── Simulated LLM Intent Parser ───────────────────
function parseIntent(message: string): ParsedFoodIntent {
  const lower = message.toLowerCase();
  const intent: ParsedFoodIntent = {
    veg: null, maxBudget: null, spicePreference: null, maxPrepTime: null,
    excludeIngredients: [], mealType: null, quantity: 1,
    ambiguous: false, contradictory: false, clarificationNote: null
  };

  // Veg / Non-veg detection
  const mentionsVeg = /\bveg\b|vegetarian/.test(lower);
  const mentionsNonVeg = /non-?veg|chicken|mutton|fish|meat|lamb/.test(lower);
  if (mentionsVeg && mentionsNonVeg) {
    intent.contradictory = true;
    intent.clarificationNote = "You mentioned both veg and non-veg (e.g., 'veg chicken'). Showing veg options since 'veg' appeared first.";
    intent.veg = true;
  } else if (mentionsVeg) {
    intent.veg = true;
  } else if (mentionsNonVeg) {
    intent.veg = false;
  }

  // Budget
  const budgetMatch = lower.match(/(?:under|below|budget|max)\s*\$?(\d+)/);
  if (budgetMatch) intent.maxBudget = parseFloat(budgetMatch[1]);

  // Spice
  if (/extra\s*spicy|very\s*spicy/.test(lower)) intent.spicePreference = 'EXTRA_SPICY';
  else if (/spicy/.test(lower)) intent.spicePreference = 'SPICY';
  else if (/mild/.test(lower)) intent.spicePreference = 'MILD';
  else if (/medium\s*spice/.test(lower)) intent.spicePreference = 'MEDIUM';

  // Prep time
  const timeMatch = lower.match(/(\d+)\s*min/);
  if (timeMatch) intent.maxPrepTime = parseInt(timeMatch[1]);
  else if (/quick|fast|hurry/.test(lower)) intent.maxPrepTime = 15;

  // Exclusions
  const exclusions: string[] = [];
  if (/no\s*onion|without\s*onion/.test(lower)) exclusions.push('onion');
  if (/no\s*nuts?|nut[\s-]*free/.test(lower)) exclusions.push('nuts');
  if (/no\s*garlic|without\s*garlic/.test(lower)) exclusions.push('garlic');
  intent.excludeIngredients = exclusions;

  // Meal type
  if (/breakfast/.test(lower)) intent.mealType = 'BREAKFAST';
  else if (/lunch/.test(lower)) intent.mealType = 'LUNCH';
  else if (/dinner/.test(lower)) intent.mealType = 'DINNER';
  else if (/snack/.test(lower)) intent.mealType = 'SNACK';

  // Quantity
  const qtyMatch = lower.match(/(\d+)\s*(?:items?|options?|dishes?)/);
  if (qtyMatch) intent.quantity = Math.min(5, parseInt(qtyMatch[1]));

  // Ambiguous check
  if (!intent.veg && intent.veg !== false && !intent.maxBudget && !intent.spicePreference
      && !intent.maxPrepTime && !intent.mealType && exclusions.length === 0) {
    intent.ambiguous = true;
    intent.clarificationNote = "Your request is quite broad. Showing our top-rated dishes!";
  }

  return intent;
}

// ── Rule Engine ───────────────────────────────────
function applyRules(intent: ParsedFoodIntent, items: MenuItem[]): MenuRecommendation[] {
  const results: MenuRecommendation[] = [];

  for (const item of items) {
    // Hard filters
    if (!item.available || item.stockCount <= 0) continue;
    if (intent.veg !== null) {
      if (intent.veg && !item.veg) continue;
      if (!intent.veg && item.veg) continue;
    }
    if (intent.maxBudget !== null && item.price > intent.maxBudget) continue;
    if (intent.maxPrepTime !== null && item.prepTimeMinutes > intent.maxPrepTime) continue;
    if (intent.mealType && item.mealType !== 'ANY' && item.mealType !== intent.mealType) continue;

    // Exclusion filter
    let excluded = false;
    for (const ex of intent.excludeIngredients) {
      if (ex === 'onion' && item.containsOnion) { excluded = true; break; }
      if (ex === 'nuts' && item.containsNuts) { excluded = true; break; }
    }
    if (excluded) continue;

    // Scoring
    let score = 50;
    const reasons: string[] = [];
    if (intent.spicePreference && intent.spicePreference === item.spiceLevel) {
      score += 20;
      reasons.push(`Matches your ${intent.spicePreference.toLowerCase()} spice preference`);
    }
    if (item.rating >= 4.5) { score += 15; reasons.push(`Highly rated (${item.rating}★)`); }
    else if (item.rating >= 4.0) { score += 10; reasons.push(`Well rated (${item.rating}★)`); }
    if (item.prepTimeMinutes <= 10) { score += 10; reasons.push(`Ready in just ${item.prepTimeMinutes} min`); }
    if (intent.maxBudget && item.price <= intent.maxBudget * 0.7) {
      score += 5;
      reasons.push(`Great value at $${item.price.toFixed(2)}`);
    }

    results.push({
      item,
      reason: reasons.length > 0 ? reasons.join('. ') : 'Matches your criteria',
      matchScore: Math.min(100, score)
    });
  }

  results.sort((a, b) => b.matchScore - a.matchScore || b.item.rating - a.item.rating);
  const limit = Math.max(1, Math.min(intent.quantity > 1 ? intent.quantity : 3, 5));
  return results.slice(0, limit);
}

// ── Public API: Send Chat Message ─────────────────
export async function sendChatMessage(message: string): Promise<ChatRecommendationResponse> {
  await delay(800); // simulate network + LLM processing

  const intent = parseIntent(message);
  const recommendations = applyRules(intent, extendedMenu);

  let responseMessage: string;
  if (intent.contradictory) {
    responseMessage = '⚠ ' + intent.clarificationNote;
  } else if (intent.ambiguous) {
    responseMessage = 'ℹ ' + intent.clarificationNote;
  } else if (recommendations.length === 0) {
    responseMessage = 'No dishes match your exact criteria. Try broadening your request!';
  } else {
    responseMessage = `Found ${recommendations.length} dish${recommendations.length > 1 ? 'es' : ''} for you!`;
  }

  return { parsedIntent: intent, recommendations, message: responseMessage };
}
