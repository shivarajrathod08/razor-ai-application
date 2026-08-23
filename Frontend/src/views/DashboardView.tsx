import React, { useState, useEffect } from 'react';
import { 
  BarChart3, 
  TrendingUp, 
  Sparkles, 
  ShoppingBag, 
  CreditCard, 
  ShieldCheck, 
  ArrowUpRight, 
  Zap, 
  Layers 
} from 'lucide-react';
import { DashboardMetrics } from '../types';
import { api } from '../services/api';

export const DashboardView: React.FC = () => {
  const [metrics, setMetrics] = useState<DashboardMetrics | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchMetrics = async () => {
      try {
        const data = await api.getDashboardMetrics();
        setMetrics(data);
      } catch (err) {
        console.error('Failed to load dashboard metrics:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchMetrics();
  }, []);

  if (loading || !metrics) {
    return (
      <div className="flex-1 flex items-center justify-center bg-slate-950 text-slate-400 text-xs">
        <div className="flex items-center gap-2">
          <div className="w-4 h-4 border-2 border-brand-500 border-t-transparent rounded-full animate-spin" />
          Loading analytics...
        </div>
      </div>
    );
  }

  const aiPercentage = metrics.totalRevenue > 0
    ? Math.round((metrics.aiAssistedRevenue / metrics.totalRevenue) * 100)
    : 0;

  return (
    <div className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8 bg-slate-950 space-y-6">
      {/* Title & Demo Note */}
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <BarChart3 className="w-5 h-5 text-indigo-400" />
            <h2 className="text-xl font-bold text-white">Merchant Commerce Analytics</h2>
          </div>
          <p className="text-xs text-slate-400">
            Real-time business performance, AI agent conversion velocity, and upsell yield.
          </p>
        </div>

        <div className="px-3 py-1 rounded-full bg-slate-900 border border-slate-800 text-[11px] text-slate-400 flex items-center gap-1.5">
          <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
          Live Merchant Sandbox Data
        </div>
      </div>

      {/* Hero KPI Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Total Revenue */}
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-5 space-y-2 relative overflow-hidden">
          <div className="flex items-center justify-between text-xs text-slate-400">
            <span>Total Gross Revenue</span>
            <span className="p-1.5 rounded-lg bg-emerald-500/10 text-emerald-400">
              <TrendingUp className="w-4 h-4" />
            </span>
          </div>
          <div className="text-2xl font-black text-white font-mono">
            ₹{metrics.totalRevenue?.toLocaleString('en-IN')}
          </div>
          <div className="flex items-center gap-1 text-[11px] text-emerald-400">
            <ArrowUpRight className="w-3.5 h-3.5" />
            <span>+38.4% this month</span>
          </div>
        </div>

        {/* AI Assisted Revenue */}
        <div className="bg-gradient-to-br from-slate-900 via-purple-950/20 to-slate-900 border border-purple-500/30 rounded-2xl p-5 space-y-2 relative overflow-hidden">
          <div className="flex items-center justify-between text-xs text-purple-300">
            <span>AI-Assisted Revenue</span>
            <span className="p-1.5 rounded-lg bg-purple-500/20 text-purple-400">
              <Sparkles className="w-4 h-4" />
            </span>
          </div>
          <div className="text-2xl font-black text-purple-200 font-mono">
            ₹{metrics.aiAssistedRevenue?.toLocaleString('en-IN')}
          </div>
          <div className="text-[11px] text-purple-300/80">
            {aiPercentage}% of total store volume
          </div>
        </div>

        {/* Upsell Revenue */}
        <div className="bg-gradient-to-br from-slate-900 via-indigo-950/20 to-slate-900 border border-indigo-500/30 rounded-2xl p-5 space-y-2 relative overflow-hidden">
          <div className="flex items-center justify-between text-xs text-indigo-300">
            <span>Upsell Yield</span>
            <span className="p-1.5 rounded-lg bg-indigo-500/20 text-indigo-400">
              <Zap className="w-4 h-4" />
            </span>
          </div>
          <div className="text-2xl font-black text-indigo-200 font-mono">
            ₹{metrics.upsellRevenue?.toLocaleString('en-IN')}
          </div>
          <div className="text-[11px] text-indigo-300/80">
            {metrics.upsellOrders} cross-sells completed
          </div>
        </div>

        {/* Average Order Value */}
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-5 space-y-2 relative overflow-hidden">
          <div className="flex items-center justify-between text-xs text-slate-400">
            <span>Average Order Value</span>
            <span className="p-1.5 rounded-lg bg-cyan-500/10 text-cyan-400">
              <ShoppingBag className="w-4 h-4" />
            </span>
          </div>
          <div className="text-2xl font-black text-white font-mono">
            ₹{metrics.averageOrderValue?.toLocaleString('en-IN')}
          </div>
          <div className="text-[11px] text-slate-400">
            Across {metrics.totalOrders} verified orders
          </div>
        </div>
      </div>

      {/* AI Revenue Insights Section */}
      <div className="space-y-4">
        <div className="flex items-center gap-2">
          <Sparkles className="w-4 h-4 text-purple-400" />
          <h3 className="text-sm font-bold text-white uppercase tracking-wider">
            AI Revenue Insights & Catalog Affinities
          </h3>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {metrics.insights?.map((ins) => (
            <div
              key={ins.id}
              className="bg-slate-900/80 border border-slate-800 hover:border-slate-700 p-5 rounded-2xl space-y-3 transition-all"
            >
              <div className="flex items-center justify-between">
                <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-brand-500/10 text-brand-400 border border-brand-500/20">
                  {ins.category}
                </span>
                <span className="text-xs font-mono font-bold text-emerald-400">
                  {ins.metricHighlight}
                </span>
              </div>

              <h4 className="text-sm font-bold text-white leading-snug">{ins.title}</h4>
              <p className="text-xs text-slate-400 leading-relaxed">{ins.description}</p>
            </div>
          ))}
        </div>
      </div>

      {/* Payment Success & Conversion Gauges */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-5 space-y-3">
          <div className="flex items-center justify-between">
            <h4 className="text-xs font-bold text-slate-300 uppercase tracking-wider">
              Razorpay Test Mode Success Rate
            </h4>
            <span className="text-xs font-mono font-bold text-emerald-400">
              {metrics.paymentSuccessRate?.toFixed(1)}%
            </span>
          </div>
          <div className="w-full bg-slate-950 h-2.5 rounded-full overflow-hidden border border-slate-800">
            <div
              className="bg-emerald-500 h-full rounded-full transition-all duration-500"
              style={{ width: `${metrics.paymentSuccessRate}%` }}
            />
          </div>
          <p className="text-[11px] text-slate-400">
            Includes both genuine test transactions and handled demo failure simulations.
          </p>
        </div>

        <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-5 space-y-3">
          <div className="flex items-center justify-between">
            <h4 className="text-xs font-bold text-slate-300 uppercase tracking-wider">
              AI Upsell Acceptance Rate
            </h4>
            <span className="text-xs font-mono font-bold text-purple-400">
              {metrics.upsellConversionRate?.toFixed(1)}%
            </span>
          </div>
          <div className="w-full bg-slate-950 h-2.5 rounded-full overflow-hidden border border-slate-800">
            <div
              className="bg-purple-500 h-full rounded-full transition-all duration-500"
              style={{ width: `${metrics.upsellConversionRate}%` }}
            />
          </div>
          <p className="text-[11px] text-slate-400">
            Customers accepting explainable accessory recommendations during conversation.
          </p>
        </div>
      </div>
    </div>
  );
};