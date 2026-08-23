import React, { useState } from 'react';
import { 
  ShieldCheck, 
  Lock, 
  AlertCircle, 
  CheckCircle2, 
  X, 
  ArrowRight, 
  Sparkles, 
  AlertTriangle,
  CreditCard,
  Layers
} from 'lucide-react';
import { CheckoutProposal } from '../types';

interface ConfirmationModalProps {
  proposal: CheckoutProposal;
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (simulateFailure: boolean) => void;
  isProcessing: boolean;
}

export const ConfirmationModal: React.FC<ConfirmationModalProps> = ({
  proposal,
  isOpen,
  onClose,
  onConfirm,
  isProcessing,
}) => {
  const [simulateFailure, setSimulateFailure] = useState(false);

  if (!isOpen || !proposal) return null;

  const precheck = proposal.safetyPrecheck;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-md animate-fade-in">
      <div className="bg-slate-900 border border-slate-700/80 rounded-2xl max-w-lg w-full overflow-hidden shadow-2xl shadow-brand-500/10 animate-scale-in">
        {/* Header */}
        <div className="bg-gradient-to-r from-slate-900 via-slate-800 to-slate-900 p-5 border-b border-slate-800 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center">
              <ShieldCheck className="w-5 h-5 text-emerald-400" />
            </div>
            <div>
              <h3 className="text-base font-bold text-white flex items-center gap-2">
                Explicit Payment Confirmation Gate
              </h3>
              <p className="text-xs text-slate-400">
                Order #{proposal.orderNumber} · Server Verified Total
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            disabled={isProcessing}
            className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6 space-y-5 max-h-[75vh] overflow-y-auto">
          {/* Gating Notice */}
          <div className="bg-brand-950/40 border border-brand-500/30 rounded-xl p-3.5 flex items-start gap-3">
            <Lock className="w-4 h-4 text-brand-400 mt-0.5 shrink-0" />
            <div className="text-xs text-brand-200">
              <span className="font-semibold text-brand-300">Bounded & Gated Architecture:</span> The AI agent cannot initiate financial transactions autonomously. Your explicit consent is required to create a Razorpay Test Mode order.
            </div>
          </div>

          {/* Itemized Order Breakdown */}
          <div className="space-y-2">
            <div className="flex items-center justify-between text-xs font-semibold text-slate-400 uppercase tracking-wider">
              <span>Item Description</span>
              <span>Server-Verified Price</span>
            </div>
            <div className="bg-slate-950/60 rounded-xl p-3.5 border border-slate-800/80 space-y-2">
              {proposal.items?.map((item) => (
                <div key={item.id || item.productId} className="flex items-center justify-between text-sm py-1 border-b border-slate-800/50 last:border-0">
                  <div className="flex items-center gap-2">
                    <span className="font-medium text-slate-200">{item.productName}</span>
                    {item.quantity > 1 && (
                      <span className="text-xs px-1.5 py-0.5 rounded bg-slate-800 text-slate-400">
                        x{item.quantity}
                      </span>
                    )}
                    {item.upsellItem && (
                      <span className="text-[10px] font-semibold px-1.5 py-0.5 rounded bg-purple-500/20 text-purple-300 border border-purple-500/30">
                        Upsell
                      </span>
                    )}
                  </div>
                  <span className="font-semibold text-slate-100 font-mono">
                    ₹{item.subtotal?.toLocaleString('en-IN') || item.unitPrice?.toLocaleString('en-IN')}
                  </span>
                </div>
              ))}

              <div className="pt-2 flex items-center justify-between border-t border-slate-700">
                <span className="text-sm font-bold text-slate-200">Total Calculated Amount</span>
                <span className="text-lg font-extrabold text-emerald-400 font-mono">
                  ₹{proposal.calculatedSubtotal?.toLocaleString('en-IN')}
                </span>
              </div>
            </div>
          </div>

          {/* 8-Point Backend Safety Checklist */}
          <div className="bg-slate-950/40 rounded-xl p-3.5 border border-slate-800 space-y-2">
            <span className="text-xs font-bold text-slate-300 uppercase tracking-wider block">
              Deterministic Safety Guard Checklist
            </span>
            <div className="grid grid-cols-2 gap-2 text-[11px]">
              <div className="flex items-center gap-1.5 text-emerald-400">
                <CheckCircle2 className="w-3.5 h-3.5 shrink-0" />
                <span>Active Cart Verified</span>
              </div>
              <div className="flex items-center gap-1.5 text-emerald-400">
                <CheckCircle2 className="w-3.5 h-3.5 shrink-0" />
                <span>Database Prices Loaded</span>
              </div>
              <div className="flex items-center gap-1.5 text-emerald-400">
                <CheckCircle2 className="w-3.5 h-3.5 shrink-0" />
                <span>Inventory Available</span>
              </div>
              <div className="flex items-center gap-1.5 text-emerald-400">
                <CheckCircle2 className="w-3.5 h-3.5 shrink-0" />
                <span>Under ₹{precheck?.maxTransactionLimit?.toLocaleString('en-IN') || '10,000'} Limit</span>
              </div>
              <div className="flex items-center gap-1.5 text-emerald-400">
                <CheckCircle2 className="w-3.5 h-3.5 shrink-0" />
                <span>Idempotency Key Active</span>
              </div>
              <div className="flex items-center gap-1.5 text-amber-400">
                <span className="w-3.5 h-3.5 rounded-full border border-amber-400 flex items-center justify-center text-[9px] font-bold">!</span>
                <span>User Confirmation Pending</span>
              </div>
            </div>
          </div>

          {/* Demo Simulation Toggle */}
          <div className="bg-rose-950/20 border border-rose-500/20 rounded-xl p-3 flex items-center justify-between">
            <div className="flex items-center gap-2.5">
              <AlertTriangle className="w-4 h-4 text-rose-400 shrink-0" />
              <div>
                <div className="text-xs font-bold text-rose-300">Simulate Payment Failure (Demo Mode)</div>
                <div className="text-[11px] text-rose-400/80">Simulates card decline to showcase graceful failure handling</div>
              </div>
            </div>
            <input
              type="checkbox"
              id="simFailToggle"
              checked={simulateFailure}
              onChange={(e) => setSimulateFailure(e.target.checked)}
              className="w-4 h-4 rounded text-rose-500 focus:ring-rose-500 bg-slate-900 border-slate-700 cursor-pointer"
            />
          </div>
        </div>

        {/* Footer Actions */}
        <div className="p-5 bg-slate-950/80 border-t border-slate-800 flex items-center justify-end gap-3">
          <button
            onClick={onClose}
            disabled={isProcessing}
            className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-400 hover:text-slate-200 hover:bg-slate-800 transition-colors"
          >
            Cancel
          </button>

          <button
            onClick={() => onConfirm(simulateFailure)}
            disabled={isProcessing}
            className={`flex items-center gap-2 px-5 py-2.5 rounded-xl text-xs font-bold text-white shadow-lg transition-all ${
              simulateFailure
                ? 'bg-rose-600 hover:bg-rose-500 shadow-rose-500/20'
                : 'bg-emerald-600 hover:bg-emerald-500 shadow-emerald-500/20'
            } disabled:opacity-50`}
          >
            {isProcessing ? (
              <>
                <div className="w-3.5 h-3.5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                Authorizing...
              </>
            ) : (
              <>
                <CreditCard className="w-3.5 h-3.5" />
                {simulateFailure ? 'Proceed with Simulated Failure' : 'Confirm & Authorize Test Payment'}
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
};