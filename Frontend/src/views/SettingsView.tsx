import React from 'react';
import { 
  Sliders, 
  ShieldCheck, 
  Lock, 
  CheckCircle2, 
  Key, 
  Cpu, 
  Server, 
  FileCode2 
} from 'lucide-react';
import { ConfigStatus } from '../types';

interface SettingsViewProps {
  configStatus: ConfigStatus | null;
}

export const SettingsView: React.FC<SettingsViewProps> = ({ configStatus }) => {
  return (
    <div className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8 bg-slate-950 space-y-6 max-w-4xl mx-auto">
      <div>
        <div className="flex items-center gap-2">
          <Sliders className="w-5 h-5 text-brand-400" />
          <h2 className="text-xl font-bold text-white">System Configuration & Security Posture</h2>
        </div>
        <p className="text-xs text-slate-400">
          Architecture overview, backend security constraints, and Razorpay Test Mode status.
        </p>
      </div>

      {/* Security Guarantees Card */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 space-y-4">
        <div className="flex items-center gap-2 text-sm font-bold text-emerald-400 uppercase tracking-wider">
          <ShieldCheck className="w-4 h-4" />
          Core Safety Guarantees
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
          <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 space-y-1.5">
            <span className="font-bold text-white flex items-center gap-1.5">
              <Lock className="w-3.5 h-3.5 text-emerald-400" />
              1. Zero-Trust Pricing
            </span>
            <p className="text-slate-400 leading-relaxed">
              Neither the LLM nor the frontend can alter prices or totals. Prices are retrieved strictly from MySQL/H2 database records.
            </p>
          </div>

          <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 space-y-1.5">
            <span className="font-bold text-white flex items-center gap-1.5">
              <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
              2. Mandatory Confirmation Gate
            </span>
            <p className="text-slate-400 leading-relaxed">
              Financial orders cannot transition from DRAFT to PENDING_PAYMENT without explicit customer confirmation.
            </p>
          </div>

          <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 space-y-1.5">
            <span className="font-bold text-white flex items-center gap-1.5">
              <Key className="w-3.5 h-3.5 text-amber-400" />
              3. Secret Isolation
            </span>
            <p className="text-slate-400 leading-relaxed">
              `RAZORPAY_KEY_SECRET` and `GEMINI_API_KEY` reside exclusively in backend environment variables and are never sent to the client.
            </p>
          </div>

          <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 space-y-1.5">
            <span className="font-bold text-white flex items-center gap-1.5">
              <Lock className="w-3.5 h-3.5 text-cyan-400" />
              4. Bounded Transaction Limits
            </span>
            <p className="text-slate-400 leading-relaxed">
              Transactions exceeding ₹{configStatus?.maxTransactionLimit?.toLocaleString('en-IN') || '10,000'} are automatically blocked by the Safety Guard.
            </p>
          </div>
        </div>
      </div>

      {/* Backend & Environment Info */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 space-y-4">
        <h3 className="text-sm font-bold text-white uppercase tracking-wider flex items-center gap-2">
          <Server className="w-4 h-4 text-cyan-400" />
          Environment & Runtime Status
        </h3>

        <div className="bg-slate-950 rounded-xl p-4 border border-slate-800 space-y-2 text-xs font-mono">
          <div className="flex justify-between py-1 border-b border-slate-800/60">
            <span className="text-slate-400">Razorpay Key ID (Public):</span>
            <span className="text-slate-200">{configStatus?.razorpayKeyId || 'rzp_test_demo_razorai'}</span>
          </div>
          <div className="flex justify-between py-1 border-b border-slate-800/60">
            <span className="text-slate-400">Gateway Mode:</span>
            <span className="text-amber-400 font-bold">TEST MODE (No real money)</span>
          </div>
          <div className="flex justify-between py-1 border-b border-slate-800/60">
            <span className="text-slate-400">Max Transaction Limit:</span>
            <span className="text-emerald-400 font-bold">₹{configStatus?.maxTransactionLimit || 10000}</span>
          </div>
          <div className="flex justify-between py-1">
            <span className="text-slate-400">Backend Framework:</span>
            <span className="text-cyan-300">Spring Boot 3.3.5 / Java 17 LTS</span>
          </div>
        </div>
      </div>
    </div>
  );
};