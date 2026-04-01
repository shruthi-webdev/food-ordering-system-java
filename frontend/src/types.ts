export type PaymentMethod = 'card' | 'wallet' | 'cash';

export interface FoodItem {
  id: string;
  name: string;
  description: string;
  price: number;
  rating?: number;
  imageUrl?: string;
}

export interface CartItem {
  id: string;
  foodItem: FoodItem;
  quantity: number;
}

export interface Invoice {
  orderId: string;
  customerName: string;
  lineItems: CartItem[];
  subTotal: number;
  tax: number;
  total: number;
  paymentMethod: PaymentMethod;
  issuedAt: string;
  status: string;
}

// ── Chat Assistant Types ──────────────────────────

export interface MenuItem {
  id: string;
  name: string;
  description: string;
  price: number;
  rating: number;
  imageUrl: string;
  veg: boolean;
  spiceLevel: string;
  prepTimeMinutes: number;
  available: boolean;
  stockCount: number;
  containsOnion: boolean;
  containsNuts: boolean;
  mealType: string;
  tags: string[];
}

export interface ParsedFoodIntent {
  veg: boolean | null;
  maxBudget: number | null;
  spicePreference: string | null;
  maxPrepTime: number | null;
  excludeIngredients: string[];
  mealType: string | null;
  quantity: number;
  ambiguous: boolean;
  contradictory: boolean;
  clarificationNote: string | null;
}

export interface MenuRecommendation {
  item: MenuItem;
  reason: string;
  matchScore: number;
}

export interface ChatRecommendationResponse {
  parsedIntent: ParsedFoodIntent;
  recommendations: MenuRecommendation[];
  message: string;
}

export interface ChatMessage {
  id: string;
  role: 'user' | 'assistant';
  text: string;
  recommendations?: MenuRecommendation[];
  intent?: ParsedFoodIntent;
  timestamp: number;
}
