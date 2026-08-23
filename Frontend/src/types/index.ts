export interface Product {
  id: number;
  name: string;
  description: string;
  category: string;
  price: number;
  currency: string;
  stock: number;
  imageUrl: string;
  tags: string;
  active: boolean;
  upsellProductIds?: string;
  upsellRationale?: string;
}

export interface CartItem {
  id: number;
  productId: number;
  productName: string;
  productCategory: string;
  productImageUrl: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
  upsellItem: boolean;
}

export interface Cart {
  id?: number;
  sessionId: string;
  items: CartItem[];
  calculatedTotal: number;
  currency: string;
  totalItemCount: number;
}

export interface SafetyCheckDetail {
  activeCartCheck: boolean;
  productExistenceCheck: boolean;
  productActiveCheck: boolean;
  stockAvailabilityCheck: boolean;
  serverSidePriceVerification: boolean;
  transactionLimitCheck: boolean;
  customerConfirmationCheck: boolean;
  idempotencyCheck: boolean;
  calculatedAmount: number;
  maxTransactionLimit: number;
  verdict: 'APPROVED' | 'BLOCKED';
  reasons: string[];
}

export interface CheckoutProposal {
  orderNumber: string;
  sessionId: string;
  items: CartItem[];
  calculatedSubtotal: number;
  currency: string;
  confirmationRequired: boolean;
  idempotencyKey: string;
  confirmationPrompt: string;
  safetyPrecheck: SafetyCheckDetail;
}

export interface PaymentOrderResponse {
  paymentNumber: string;
  orderNumber: string;
  razorpayOrderId: string;
  amount: number;
  currency: string;
  razorpayKeyId: string;
  testMode: boolean;
  status: string;
  isDemoFailureSimulated: boolean;
  safetyCheckResult?: SafetyCheckDetail;
  message: string;
}

export interface PaymentVerifyResponse {
  verified: boolean;
  paymentStatus: string;
  orderStatus: string;
  orderNumber: string;
  paymentNumber: string;
  amount: number;
  message: string;
  demoFailure: boolean;
}

export interface AuditEvent {
  eventId: string;
  timestamp: string;
  sessionId?: string;
  orderId?: string;
  paymentId?: string;
  eventType: string;
  actor: 'CUSTOMER' | 'AI_AGENT' | 'BACKEND_SAFETY_GUARD' | 'ORDER_SERVICE' | 'RAZORPAY_TEST_GATEWAY' | 'MERCHANT_ADMIN';
  description: string;
  amount?: number;
  success: boolean;
  metadataJson?: string;
}

export interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
  upsellItem: boolean;
}

export interface Order {
  id: number;
  orderNumber: string;
  sessionId: string;
  items: OrderItem[];
  totalAmount: number;
  currency: string;
  status: 'DRAFT' | 'PENDING_PAYMENT' | 'PAID' | 'PAYMENT_FAILED' | 'CANCELLED';
  customerConfirmed: boolean;
  customerConfirmedAt?: string;
  safetyPassed: boolean;
  safetyCheckSummary?: string;
  idempotencyKey: string;
  createdAt: string;
}

export interface RevenueInsight {
  id: string;
  title: string;
  description: string;
  impact: string;
  category: string;
  metricHighlight: string;
}

export interface DashboardMetrics {
  totalRevenue: number;
  aiAssistedRevenue: number;
  upsellRevenue: number;
  nonAiRevenue: number;
  totalOrders: number;
  aiAssistedOrders: number;
  upsellOrders: number;
  averageOrderValue: number;
  conversionRate: number;
  paymentSuccessRate: number;
  upsellConversionRate: number;
  insights: RevenueInsight[];
}

export interface ChatResponse {
  sessionId: string;
  reply: string;
  recommendedProducts?: Product[];
  suggestedUpsell?: Product;
  upsellExplanation?: string;
  cart?: Cart;
  checkoutProposal?: CheckoutProposal;
  toolCallsExecuted?: string[];
  paymentConfirmationRequested: boolean;
}

export interface PriceTamperDemoResponse {
  tamperingDetected: boolean;
  productName: string;
  clientSuppliedPrice: number;
  authoritativeDatabasePrice: number;
  outcome: string;
  explanation: string;
  auditEventId: string;
}

export interface ConfigStatus {
  isRazorpayConfigured: boolean;
  isGeminiConfigured: boolean;
  razorpayKeyId: string;
  testMode: boolean;
  maxTransactionLimit: number;
  currency: string;
}