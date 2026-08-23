import React, { useState, useEffect } from 'react';
import { Header } from './components/Header';
import { DemoBar } from './components/DemoBar';
import { ConfirmationModal } from './components/ConfirmationModal';
import { RazorpayModal } from './components/RazorpayModal';
import { PriceTamperModal } from './components/PriceTamperModal';
import { ChatView } from './views/ChatView';
import { AuditTrailView } from './views/AuditTrailView';
import { DashboardView } from './views/DashboardView';
import { ProductsView } from './views/ProductsView';
import { OrdersView } from './views/OrdersView';
import { SettingsView } from './views/SettingsView';
import { Cart, CheckoutProposal, ConfigStatus, PaymentOrderResponse, PaymentVerifyResponse, PriceTamperDemoResponse } from './types';
import { api } from './services/api';

export function App() {
  const [sessionId] = useState(() => {
    const saved = localStorage.getItem('razorai_session_id');
    if (saved) return saved;
    const newId = 'sess_' + Math.random().toString(36).substring(2, 10);
    localStorage.setItem('razorai_session_id', newId);
    return newId;
  });

  const [activeTab, setActiveTab] = useState<'chat' | 'audit' | 'dashboard' | 'products' | 'orders' | 'settings'>('chat');
  const [cart, setCart] = useState<Cart | null>(null);
  const [configStatus, setConfigStatus] = useState<ConfigStatus | null>(null);
  const [isCartDrawerOpen, setIsCartDrawerOpen] = useState(false);

  // Modals state
  const [activeProposal, setActiveProposal] = useState<CheckoutProposal | null>(null);
  const [activePaymentOrder, setActivePaymentOrder] = useState<PaymentOrderResponse | null>(null);
  const [isTamperModalOpen, setIsTamperModalOpen] = useState(false);
  const [isProcessingCheckout, setIsProcessingCheckout] = useState(false);
  const [demoLoading, setDemoLoading] = useState(false);

  // Initial load
  useEffect(() => {
    const loadInitialData = async () => {
      try {
        const [config, userCart] = await Promise.all([
          api.getConfigStatus(),
          api.getCart(sessionId),
        ]);
        setConfigStatus(config);
        setCart(userCart);
      } catch (err) {
        console.error('Initialization error:', err);
      }
    };
    loadInitialData();
  }, [sessionId]);

  // Checkout Gating & Confirmation Flow
  const handleConfirmCheckout = async (simulateFailure: boolean) => {
    if (!activeProposal) return;
    setIsProcessingCheckout(true);

    try {
      // Step 1: Confirm order on backend with customer signature
      const paymentOrderRes = await api.confirmCheckout(
        sessionId,
        activeProposal.idempotencyKey,
        true,
        simulateFailure
      );

      setActiveProposal(null);
      setActivePaymentOrder(paymentOrderRes);
    } catch (err: any) {
      alert(err.message || 'Payment safety check failed');
    } finally {
      setIsProcessingCheckout(false);
    }
  };

  // Payment Verification with Razorpay Test Mode
  const handleCompletePayment = async (success: boolean, failureReason?: string): Promise<PaymentVerifyResponse> => {
    if (!activePaymentOrder) {
      throw new Error('No active payment order');
    }

    const res = await api.verifyPayment(
      activePaymentOrder.razorpayOrderId,
      'pay_test_' + Math.random().toString(36).substring(2, 10),
      'sig_test_mock_verified',
      !success || activePaymentOrder.isDemoFailureSimulated
    );

    // Refresh cart after payment
    const updatedCart = await api.getCart(sessionId);
    setCart(updatedCart);

    return res;
  };

  // Demo Actions
  const handleRunBagScenario = () => {
    setActiveTab('chat');
    // Triggers chat message in ChatView
  };

  const handleLoadDemoCart = async () => {
    setDemoLoading(true);
    try {
      const res = await api.loadDemoCart(sessionId);
      setCart(res.cart);
      setActiveTab('chat');
    } catch (err: any) {
      alert(err.message || 'Failed to load demo cart');
    } finally {
      setDemoLoading(false);
    }
  };

  const handleSimulateFailureDemo = async () => {
    setDemoLoading(true);
    try {
      // Ensure cart has items
      let currentCart = await api.getCart(sessionId);
      if (!currentCart || !currentCart.items || currentCart.items.length === 0) {
        const loaded = await api.loadDemoCart(sessionId);
        setCart(loaded.cart);
      }
      const proposal = await api.proposeCheckout(sessionId);
      setActiveProposal(proposal);
    } catch (err: any) {
      alert(err.message || 'Failed to create checkout proposal');
    } finally {
      setDemoLoading(false);
    }
  };

  const handleResetDemoState = async () => {
    setDemoLoading(true);
    try {
      await api.resetDemo();
      const newCart = await api.getCart(sessionId);
      setCart(newCart);
      alert('Demo state reset! Carts and test orders cleared, catalog restored.');
    } catch (err: any) {
      alert(err.message || 'Failed to reset demo');
    } finally {
      setDemoLoading(false);
    }
  };

  const handleRunPriceTamperTest = async (productId: number, tamperedPrice: number): Promise<PriceTamperDemoResponse> => {
    const res = await api.testPriceTampering(sessionId, productId, tamperedPrice);
    const updatedCart = await api.getCart(sessionId);
    setCart(updatedCart);
    return res;
  };

  return (
    <div className="min-h-screen bg-slate-950 flex flex-col font-['Plus_Jakarta_Sans',sans-serif] text-slate-100 selection:bg-brand-500 selection:text-white">
      {/* Top Navigation */}
      <Header
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        cartItemCount={cart?.totalItemCount || 0}
        configStatus={configStatus}
        onOpenCart={() => setIsCartDrawerOpen(!isCartDrawerOpen)}
      />

      {/* Demo Action Bar */}
      <DemoBar
        onLoadScenario={handleRunBagScenario}
        onLoadDemoCart={handleLoadDemoCart}
        onSimulateFailure={handleSimulateFailureDemo}
        onTestPriceTamper={() => setIsTamperModalOpen(true)}
        onResetDemo={handleResetDemoState}
        loading={demoLoading}
      />

      {/* View Router */}
      <main className="flex-1 flex flex-col overflow-hidden">
        {activeTab === 'chat' && (
          <ChatView
            sessionId={sessionId}
            cart={cart}
            onUpdateCart={setCart}
            onOpenCheckoutProposal={setActiveProposal}
            isCartDrawerOpen={isCartDrawerOpen}
            onCartDrawerChange={setIsCartDrawerOpen}
          />
        )}
        {activeTab === 'audit' && <AuditTrailView sessionId={sessionId} />}
        {activeTab === 'dashboard' && <DashboardView />}
        {activeTab === 'products' && (
          <ProductsView
            onAddToCart={async (id) => {
              const updated = await api.addToCart(sessionId, id, 1, false);
              setCart(updated);
            }}
            onOpenPriceTamperModal={() => setIsTamperModalOpen(true)}
          />
        )}
        {activeTab === 'orders' && <OrdersView />}
        {activeTab === 'settings' && <SettingsView configStatus={configStatus} />}
      </main>

      {/* Payment Confirmation Gating Modal */}
      {activeProposal && (
        <ConfirmationModal
          proposal={activeProposal}
          isOpen={!!activeProposal}
          onClose={() => setActiveProposal(null)}
          onConfirm={handleConfirmCheckout}
          isProcessing={isProcessingCheckout}
        />
      )}

      {/* Razorpay Test Mode Checkout Modal */}
      {activePaymentOrder && (
        <RazorpayModal
          paymentOrder={activePaymentOrder}
          isOpen={!!activePaymentOrder}
          onClose={() => setActivePaymentOrder(null)}
          onCompletePayment={handleCompletePayment}
        />
      )}

      {/* Price Tampering Security Test Modal */}
      {isTamperModalOpen && (
        <PriceTamperModal
          isOpen={isTamperModalOpen}
          onClose={() => setIsTamperModalOpen(false)}
          onRunTest={handleRunPriceTamperTest}
        />
      )}
    </div>
  );
}