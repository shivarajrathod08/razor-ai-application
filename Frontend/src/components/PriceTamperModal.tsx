import React, { useState } from 'react';
import { 
  ShieldAlert, 
  X, 
  CheckCircle2, 
  AlertTriangle, 
  ArrowRight, 
  Lock, 
  Database 
} from 'lucide-react';
import { PriceTamperDemoResponse } from '../types';

interface PriceTamperModalProps {
  isOpen: boolean;
  onClose: () => void;
  onRunTest: (productId: number, tamperedPrice: number) => Promise<PriceTamperDemoResponse>;
}

export const PriceTamperModal: React.FC<PriceTamperModalProps> = ({
  isOpen,
  onClose,
  onRunTest,
}) => {
  const [tamperedPrice, setTamperedPrice] = useState('1.00');
  const [isTesting, setIsTesting] = useState(false);
  const [result, setResult] = useState<PriceTamperDemoResponse | null>(null);

  if (!isOpen) return null;

  const handleTest = async () => {
    setIsTesting(true);
    try {
      // Product ID 1: Urban Laptop Backpack (Catalog price: ₹1,499)
      const res = await onRunTest(1, parseFloat(tamperedPrice) || 1.0);
      setResult(res);
    } catch (err: any) {
      alert(err.message || 'Tamper test error');
    } finally {
      setIsTesting(false);
    }
  };

  const handleReset = () => {
    setResult(null);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-md animate-fade-in">
      <div className="bg-slate-900 border border-amber-500/30 rounded-2xl max-w-lg w-full overflow-hidden shadow-2xl shadow-amber-500/10 animate-scale-in">
        {/* Header */}
        <div className="bg-amber-950/30 p-5 border-b border-amber-500/20 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-amber-500/10 border border-amber-500/30 flex items-center justify-center">
              <ShieldAlert className="w-5 h-5 text-amber-400" />
            </div>
            <div>
              <h3 className="text-base font-bold text-white">
                Zero-Trust Pricing Security Demonstration
              </h3>
              <p className="text-xs text-amber-300/80">
                Demo Mode: "Try Price Tampering"
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Body */}
        <div className="p-6 space-y-4">
          <p className="text-xs text-slate-300 leading-relaxed">
            This live security test simulates an adversary or malicious frontend/LLM attempting to add the <strong>Urban Laptop Backpack</strong> (Database price: <strong>₹1,499</strong>) for an arbitrary price such as <strong>₹1.00</strong>.
          </p>

          {!result ? (
            <div className="space-y-4">
              <div className="bg-slate-950/60 p-4 rounded-xl border border-slate-800 space-y-3">
                <div className="text-xs font-semibold text-slate-300">Target Product: Urban Laptop Backpack</div>
                <div className="flex items-center justify-between text-xs text-slate-400">
                  <span>Authoritative Database Price:</span>
                  <span className="font-bold text-emerald-400 font-mono">₹1,499.00</span>
                </div>
                <div>
                  <label className="text-xs font-semibold text-amber-300 block mb-1.5">
                    Malicious Client-Supplied Price (INR):
                  </label>
                  <input
                    type="number"
                    value={tamperedPrice}
                    onChange={(e) => setTamperedPrice(e.target.value)}
                    className="w-full bg-slate-900 border border-slate-700 rounded-xl px-3 py-2 text-sm text-white font-mono focus:outline-none focus:border-amber-500"
                    placeholder="1.00"
                  />
                </div>
              </div>

              <button
                onClick={handleTest}
                disabled={isTesting}
                className="w-full py-3 rounded-xl bg-amber-600 hover:bg-amber-500 text-white font-bold text-xs shadow-lg shadow-amber-500/20 transition-all flex items-center justify-center gap-2 disabled:opacity-50"
              >
                {isTesting ? (
                  <>
                    <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                    Executing Security Probe...
                  </>
                ) : (
                  <>
                    <Lock className="w-4 h-4" />
                    Execute Tampering Probe
                  </>
                )}
              </button>
            </div>
          ) : (
            <div className="space-y-4">
              {/* Outcome Banner */}
              <div className="bg-emerald-950/40 border border-emerald-500/30 rounded-xl p-4 space-y-3">
                <div className="flex items-center gap-2 text-emerald-400 text-sm font-bold">
                  <CheckCircle2 className="w-5 h-5 shrink-0" />
                  Zero-Trust Guard Passed: Price Tampering Blocked!
                </div>
                <p className="text-xs text-slate-300 leading-relaxed">
                  {result.explanation}
                </p>
                <div className="bg-slate-950/80 rounded-lg p-3 border border-slate-800 text-xs font-mono space-y-1.5">
                  <div className="flex justify-between">
                    <span className="text-rose-400">Client Attempted Price:</span>
                    <span className="text-rose-300 font-bold line-through">₹{result.clientSuppliedPrice}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-emerald-400">Database Price Enforced:</span>
                    <span className="text-emerald-300 font-bold">₹{result.authoritativeDatabasePrice}</span>
                  </div>
                  <div className="flex justify-between text-[11px] text-slate-400 pt-1 border-t border-slate-800">
                    <span>Audit Event Logged:</span>
                    <span className="text-slate-300">#{result.auditEventId.substring(0, 8)}...</span>
                  </div>
                </div>
              </div>

              <div className="flex items-center gap-2">
                <button
                  onClick={handleReset}
                  className="flex-1 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold"
                >
                  Test Another Value
                </button>
                <button
                  onClick={onClose}
                  className="flex-1 py-2.5 rounded-xl bg-brand-600 hover:bg-brand-500 text-white text-xs font-bold"
                >
                  Close
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};