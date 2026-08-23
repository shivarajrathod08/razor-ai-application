import {
  Cart,
  ChatResponse,
  CheckoutProposal,
  ConfigStatus,
  DashboardMetrics,
  Order,
  PaymentOrderResponse,
  PaymentVerifyResponse,
  PriceTamperDemoResponse,
  Product,
  AuditEvent
} from '../types';

export interface BaseApiResponse<T> {
  success: boolean;
  message?: string;
  errorCode?: string;
  data: T;
  timestamp: string;
}

const API_BASE = '/api';

async function fetchJson<T>(url: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE}${url}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(options?.headers || {}),
    },
  });

  const json: BaseApiResponse<T> = await res.json();
  if (!res.ok || !json.success) {
    const errorMsg = json.message || `Request failed with status ${res.status}`;
    const error = new Error(errorMsg);
    (error as any).data = json.data;
    (error as any).errorCode = json.errorCode;
    throw error;
  }
  return json.data;
}

export const api = {
  // Chat
  async sendChatMessage(sessionId: string, message: string): Promise<ChatResponse> {
    return fetchJson<ChatResponse>('/chat', {
      method: 'POST',
      body: JSON.stringify({ sessionId, message }),
    });
  },

  // Cart
  async getCart(sessionId: string): Promise<Cart> {
    return fetchJson<Cart>(`/cart/${sessionId}`);
  },

  async addToCart(sessionId: string, productId: number, quantity = 1, isUpsell = false, untrustedPrice?: number): Promise<Cart> {
    return fetchJson<Cart>(`/cart/${sessionId}/items`, {
      method: 'POST',
      body: JSON.stringify({ productId, quantity, isUpsell, untrustedPrice }),
    });
  },

  async removeFromCart(sessionId: string, productId: number): Promise<Cart> {
    return fetchJson<Cart>(`/cart/${sessionId}/items/${productId}`, {
      method: 'DELETE',
    });
  },

  async clearCart(sessionId: string): Promise<void> {
    return fetchJson<void>(`/cart/${sessionId}/clear`, {
      method: 'DELETE',
    });
  },

  // Checkout & Gating
  async proposeCheckout(sessionId: string): Promise<CheckoutProposal> {
    return fetchJson<CheckoutProposal>(`/checkout/propose?sessionId=${encodeURIComponent(sessionId)}`, {
      method: 'POST',
    });
  },

  async confirmCheckout(sessionId: string, idempotencyKey: string, customerConfirmed: boolean, simulateFailure = false): Promise<PaymentOrderResponse> {
    return fetchJson<PaymentOrderResponse>('/checkout/confirm', {
      method: 'POST',
      body: JSON.stringify({ sessionId, idempotencyKey, customerConfirmed, simulateFailure }),
    });
  },

  // Payment Verification
  async verifyPayment(razorpayOrderId: string, razorpayPaymentId: string, razorpaySignature?: string, simulateFailure = false): Promise<PaymentVerifyResponse> {
    return fetchJson<PaymentVerifyResponse>('/payments/verify', {
      method: 'POST',
      body: JSON.stringify({ razorpayOrderId, razorpayPaymentId, razorpaySignature, simulateFailure }),
    });
  },

  // Catalog & Products
  async getProducts(): Promise<Product[]> {
    return fetchJson<Product[]>('/products');
  },

  async searchProducts(query?: string, maxPrice?: number): Promise<Product[]> {
    const params = new URLSearchParams();
    if (query) params.append('query', query);
    if (maxPrice) params.append('maxPrice', maxPrice.toString());
    return fetchJson<Product[]>(`/products/search?${params.toString()}`);
  },

  // Orders
  async getOrders(): Promise<Order[]> {
    return fetchJson<Order[]>('/orders');
  },

  async getOrderByNumber(orderNumber: string): Promise<Order> {
    return fetchJson<Order>(`/orders/${orderNumber}`);
  },

  // Audit Trail
  async getAuditEvents(): Promise<AuditEvent[]> {
    return fetchJson<AuditEvent[]>('/audit');
  },

  async getAuditEventsBySession(sessionId: string): Promise<AuditEvent[]> {
    return fetchJson<AuditEvent[]>(`/audit/session/${sessionId}`);
  },

  // Dashboard
  async getDashboardMetrics(): Promise<DashboardMetrics> {
    return fetchJson<DashboardMetrics>('/dashboard/metrics');
  },

  // Config & Status
  async getConfigStatus(): Promise<ConfigStatus> {
    return fetchJson<ConfigStatus>('/config/status');
  },

  // Demo Actions
  async loadDemoCart(sessionId: string): Promise<{ message: string; cart: Cart }> {
    return fetchJson<{ message: string; cart: Cart }>(`/demo/load-cart?sessionId=${encodeURIComponent(sessionId)}`, {
      method: 'POST',
    });
  },

  async resetDemo(): Promise<{ success: boolean; message: string }> {
    return fetchJson<{ success: boolean; message: string }>('/demo/reset', {
      method: 'POST',
    });
  },

  async testPriceTampering(sessionId: string, productId: number, tamperedPrice: number): Promise<PriceTamperDemoResponse> {
    return fetchJson<PriceTamperDemoResponse>('/demo/price-tamper', {
      method: 'POST',
      body: JSON.stringify({ sessionId, productId, tamperedPrice }),
    });
  },
};