import React, { useState } from 'react';
import { 
  CreditCard, 
  Smartphone, 
  Building2, 
  Wallet, 
  ShieldCheck, 
  X, 
  CheckCircle2, 
  AlertTriangle,
  RotateCcw
} from 'lucide-react';
import { PaymentOrderResponse, PaymentVerifyResponse } from '../types';

interface RazorpayModalProps {
  paymentOrder: PaymentOrderResponse | null;
  isOpen: boolean;
  onClose: () => void;
  onCompletePayment: (success: boolean, failureReason?: string) => Promise<PaymentVerifyResponse>;
}

export const RazorpayModal: React.FC<RazorpayModalProps> = ({
  paymentOrder,
  isOpen,
  onClose,
  onCompletePayment,
}) => {
  const [selectedMethod, setSelectedMethod] = useState<'card' | 'upi' | 'netbanking'>('card');
  const [isProcessing, setIsProcessing] = useState(false);
  const [verificationResult, setVerificationResult] = useState<PaymentVerifyResponse | null>(null);

  if (!isOpen || !paymentOrder) return null;

  const handlePay = async (simulateSuccess: boolean) => {
    setIsProcessing(true);
    try {
      const res = await onCompletePayment(simulateSuccess);
      setVerificationResult(res);
    } catch (err: any) {
      setVerificationResult({
        verified: false,
        paymentStatus: 'FAILED',
        orderStatus: 'PAYMENT_FAILED',
        orderNumber: paymentOrder.orderNumber,
        paymentNumber: paymentOrder.paymentNumber,
        amount: paymentOrder.amount,
        message: err.message || 'Payment failed',
        demoFailure: paymentOrder.isDemoFailureSimulated,
      });
    } finally {
      setIsProcessing(false);
    }
  };

  const handleResetAndRetry = () => {
    setVerificationResult(null);
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/85 backdrop-blur-md animate-fade-in">
      <div className="bg-slate-900 border border-slate-700/80 rounded-2xl max-w-md w-full overflow-hidden shadow-2xl shadow-blue-500/10 animate-scale-in">
        {/* Header - Styled like Razorpay Standard Checkout */}
        <div className="bg-[#0C2340] p-5 text-white flex items-center justify-between border-b border-blue-900/50">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-lg bg-blue-600/30 border border-blue-400/40 flex items-center justify-center font-black text-blue-300 text-sm">
              ₹
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="font-bold text-sm">Razorpay Checkout</span>
                <span className="text-[10px] font-bold px-1.5 py-0.5 rounded bg-amber-400/20 text-amber-300 border border-amber-400/30">
                  TEST MODE
                </span>
              </div>
              <p className="text-[11px] text-blue-200/80">Order: {paymentOrder.razorpayOrderId}</p>
            </div>
          </div>
          <button
            onClick={onClose}
            disabled={isProcessing}
            className="p-1 rounded-lg text-blue-300 hover:text-white hover:bg-blue-800/40 transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Verification Result View */}
        {verificationResult ? (
          <div className="p-6 space-y-5 text-center">
            {verificationResult.verified ? (
              <div className="space-y-3">
                <div className="w-16 h-16 rounded-full bg-emerald-500/10 border-2 border-emerald-500/40 flex items-center justify-center mx-auto text-emerald-400 animate-scale-in">
                  <CheckCircle2 className="w-8 h-8" />
                </div>
                <h4 className="text-lg font-bold text-white">Payment Successful!</h4>
                <p className="text-xs text-slate-300">
                  {verificationResult.message}
                </p>
                <div className="bg-slate-950/60 rounded-xl p-3 border border-slate-800 text-left text-xs space-y-1 font-mono">
                  <div className="text-slate-400">Order ID: <span className="text-slate-200">{verificationResult.orderNumber}</span></div>
                  <div className="text-slate-400">Payment ID: <span className="text-slate-200">{verificationResult.paymentNumber}</span></div>
                  <div className="text-slate-400">Amount Paid: <span className="text-emerald-400 font-bold">₹{verificationResult.amount?.toLocaleString('en-IN')}</span></div>
                  <div className="text-slate-400">State Machine: <span className="text-emerald-400 font-bold">PAID</span></div>
                </div>
                <button
                  onClick={onClose}
                  className="w-full py-2.5 rounded-xl bg-brand-600 hover:bg-brand-500 text-white text-xs font-bold transition-all"
                >
                  Done
                </button>
              </div>
            ) : (
              <div className="space-y-3">
                <div className="w-16 h-16 rounded-full bg-rose-500/10 border-2 border-rose-500/40 flex items-center justify-center mx-auto text-rose-400 animate-scale-in">
                  <AlertTriangle className="w-8 h-8" />
                </div>
                <div className="inline-block px-2.5 py-0.5 rounded-full bg-rose-500/20 text-rose-300 text-[10px] font-bold uppercase tracking-wider">
                  {verificationResult.demoFailure ? 'Demo Simulated Failure' : 'Payment Failed'}
                </div>
                <h4 className="text-lg font-bold text-white">Payment Unsuccessful</h4>
                <p className="text-xs text-rose-300">
                  {verificationResult.message}
                </p>
                <div className="bg-slate-950/60 rounded-xl p-3 border border-slate-800 text-left text-xs space-y-1 font-mono">
                  <div className="text-slate-400">Status: <span className="text-rose-400 font-bold">PAYMENT_FAILED</span></div>
                  <div className="text-slate-400">Customer Charged: <span className="text-slate-200 font-bold">₹0 (No charge recorded)</span></div>
                  <div className="text-slate-400">Order State: <span className="text-amber-400 font-bold">Unpaid / Retry Available</span></div>
                </div>
                <div className="flex items-center gap-2">
                  <button
                    onClick={handleResetAndRetry}
                    className="flex-1 py-2.5 rounded-xl bg-brand-600 hover:bg-brand-500 text-white text-xs font-bold transition-all flex items-center justify-center gap-1.5"
                  >
                    <RotateCcw className="w-3.5 h-3.5" />
                    Retry Payment
                  </button>
                  <button
                    onClick={onClose}
                    className="px-4 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold"
                  >
                    Close
                  </button>
                </div>
              </div>
            )}
          </div>
        ) : (
          <div className="p-6 space-y-5">
            {/* Amount Banner */}
            <div className="flex items-center justify-between bg-slate-950/60 p-3.5 rounded-xl border border-slate-800">
              <div>
                <span className="text-xs text-slate-400">Payable Amount</span>
                <div className="text-xl font-black text-white font-mono">
                  ₹{paymentOrder.amount?.toLocaleString('en-IN')}
                </div>
              </div>
              <div className="text-right">
                <span className="text-[10px] text-slate-400 block">Gateway</span>
                <span className="text-xs font-semibold text-brand-400">Razorpay Test Mode</span>
              </div>
            </div>

            {paymentOrder.isDemoFailureSimulated && (
              <div className="bg-rose-950/40 border border-rose-500/30 rounded-xl p-3 text-xs text-rose-300 flex items-center gap-2">
                <AlertTriangle className="w-4 h-4 text-rose-400 shrink-0" />
                <span><strong>Demo Simulation:</strong> This test transaction is primed to simulate bank decline.</span>
              </div>
            )}

            {/* Payment Method Selector */}
            <div className="space-y-2">
              <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Select Test Method</span>
              <div className="grid grid-cols-3 gap-2">
                <button
                  type="button"
                  onClick={() => setSelectedMethod('card')}
                  className={`p-3 rounded-xl border flex flex-col items-center gap-1.5 text-xs font-medium transition-all ${
                    selectedMethod === 'card'
                      ? 'bg-blue-600/20 border-blue-500/50 text-blue-300'
                      : 'bg-slate-950/50 border-slate-800 text-slate-400 hover:bg-slate-800'
                  }`}
                >
                  <CreditCard className="w-4 h-4" />
                  Test Card
                </button>
                <button
                  type="button"
                  onClick={() => setSelectedMethod('upi')}
                  className={`p-3 rounded-xl border flex flex-col items-center gap-1.5 text-xs font-medium transition-all ${
                    selectedMethod === 'upi'
                      ? 'bg-blue-600/20 border-blue-500/50 text-blue-300'
                      : 'bg-slate-950/50 border-slate-800 text-slate-400 hover:bg-slate-800'
                  }`}
                >
                  <Smartphone className="w-4 h-4" />
                  Test UPI
                </button>
                <button
                  type="button"
                  onClick={() => setSelectedMethod('netbanking')}
                  className={`p-3 rounded-xl border flex flex-col items-center gap-1.5 text-xs font-medium transition-all ${
                    selectedMethod === 'netbanking'
                      ? 'bg-blue-600/20 border-blue-500/50 text-blue-300'
                      : 'bg-slate-950/50 border-slate-800 text-slate-400 hover:bg-slate-800'
                  }`}
                >
                  <Building2 className="w-4 h-4" />
                  Netbanking
                </button>
              </div>
            </div>

            {/* Simulated Card Details */}
            {selectedMethod === 'card' && (
              <div className="bg-slate-950/40 p-3.5 rounded-xl border border-slate-800 space-y-2 text-xs">
                <div className="text-slate-400">Card Number: <span className="text-slate-200 font-mono">4111 2222 3333 4444</span> (Razorpay Test Card)</div>
                <div className="flex justify-between text-slate-400">
                  <span>Expiry: <span className="text-slate-200 font-mono">12/28</span></span>
                  <span>CVV: <span className="text-slate-200 font-mono">123</span></span>
                </div>
              </div>
            )}

            {/* Trigger Button */}
            <div className="space-y-2 pt-2">
              <button
                onClick={() => handlePay(!paymentOrder.isDemoFailureSimulated)}
                disabled={isProcessing}
                className="w-full py-3 rounded-xl bg-blue-600 hover:bg-blue-500 text-white font-bold text-xs shadow-lg shadow-blue-500/20 transition-all flex items-center justify-center gap-2 disabled:opacity-50"
              >
                {isProcessing ? (
                  <>
                    <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                    Processing with Razorpay Test Gateway...
                  </>
                ) : (
                  <>
                    <ShieldCheck className="w-4 h-4 text-cyan-300" />
                    {paymentOrder.isDemoFailureSimulated ? 'Simulate Razorpay Payment Failure' : `Pay ₹${paymentOrder.amount?.toLocaleString('en-IN')} in Test Mode`}
                  </>
                )}
              </button>
              <div className="text-center text-[10px] text-slate-500">
                🔒 Protected by RazorAI Deterministic Financial Guard
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};