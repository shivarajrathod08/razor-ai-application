import React from 'react';
import { 
  Play, 
  Sparkles, 
  AlertTriangle, 
  ShieldAlert, 
  RotateCcw, 
  ShoppingBag, 
  HelpCircle, 
  Zap 
} from 'lucide-react';

interface DemoBarProps {
  onLoadScenario: () => void;
  onLoadDemoCart: () => void;
  onSimulateFailure: () => void;
  onTestPriceTamper: () => void;
  onResetDemo: () => void;
  loading: boolean;
}

export const DemoBar: React.FC<DemoBarProps> = ({
  onLoadScenario,
  onLoadDemoCart,
  onSimulateFailure,
  onTestPriceTamper,
  onResetDemo,
  loading,
}) => {
  return (
    <div className="bg-slate-900 border-b border-slate-800 py-2 px-4 sm:px-6">
      <div className="max-w-7xl mx-auto flex flex-wrap items-center justify-between gap-2">
        <div className="flex items-center gap-2">
          <div className="flex items-center gap-1.5 text-xs font-bold text-brand-400 uppercase tracking-wider">
            <Zap className="w-3.5 h-3.5 text-amber-400" />
            Hackathon Demo Controls:
          </div>
          <span className="text-xs text-slate-400 hidden md:inline">
            One-click test scenarios for live presentation
          </span>
        </div>

        <div className="flex flex-wrap items-center gap-1.5">
          <button
            onClick={onLoadScenario}
            disabled={loading}
            className="flex items-center gap-1.5 px-3 py-1 rounded-lg bg-brand-600/20 hover:bg-brand-600/30 text-brand-300 border border-brand-500/30 text-xs font-semibold transition-all hover:scale-[1.02] active:scale-[0.98]"
            title="Step 1: Start prompt 'I need a laptop bag under ₹2,000'"
          >
            <Sparkles className="w-3 h-3 text-cyan-400" />
            1. Run Bag Search Flow
          </button>

          <button
            onClick={onLoadDemoCart}
            disabled={loading}
            className="flex items-center gap-1.5 px-3 py-1 rounded-lg bg-indigo-600/20 hover:bg-indigo-600/30 text-indigo-300 border border-indigo-500/30 text-xs font-semibold transition-all hover:scale-[1.02] active:scale-[0.98]"
            title="Preload Urban Laptop Backpack (₹1,499) + USB-C Hub (₹799)"
          >
            <ShoppingBag className="w-3 h-3 text-indigo-400" />
            2. Load Demo Cart (₹2,298)
          </button>

          <button
            onClick={onSimulateFailure}
            disabled={loading}
            className="flex items-center gap-1.5 px-3 py-1 rounded-lg bg-rose-600/20 hover:bg-rose-600/30 text-rose-300 border border-rose-500/30 text-xs font-semibold transition-all hover:scale-[1.02] active:scale-[0.98]"
            title="Demonstrate graceful payment failure and recovery"
          >
            <AlertTriangle className="w-3 h-3 text-rose-400" />
            3. Simulate Payment Failure
          </button>

          <button
            onClick={onTestPriceTamper}
            disabled={loading}
            className="flex items-center gap-1.5 px-3 py-1 rounded-lg bg-amber-600/20 hover:bg-amber-600/30 text-amber-300 border border-amber-500/30 text-xs font-semibold transition-all hover:scale-[1.02] active:scale-[0.98]"
            title="Demonstrate zero-trust pricing: Client attempts ₹1 price -> Backend forces ₹1,499 DB price"
          >
            <ShieldAlert className="w-3 h-3 text-amber-400" />
            4. Try Price Tampering
          </button>

          <button
            onClick={onResetDemo}
            disabled={loading}
            className="flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 border border-slate-700 text-xs font-medium transition-all"
            title="Reset carts, test orders, and restore catalog"
          >
            <RotateCcw className="w-3 h-3 text-slate-400" />
            Reset
          </button>
        </div>
      </div>
    </div>
  );
};