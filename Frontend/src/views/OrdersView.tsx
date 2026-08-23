import React, { useState, useEffect } from 'react';
import { 
  History, 
  RefreshCw, 
  CheckCircle2, 
  AlertTriangle, 
  Clock, 
  ShieldCheck, 
  ExternalLink,
  Lock,
  ArrowRight
} from 'lucide-react';
import { Order } from '../types';
import { api } from '../services/api';

export const OrdersView: React.FC = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);

  const fetchOrders = async () => {
    setLoading(true);
    try {
      const data = await api.getOrders();
      setOrders(data);
    } catch (err) {
      console.error('Failed to load orders:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'PAID':
        return {
          bg: 'bg-emerald-500/10 text-emerald-300 border-emerald-500/30',
          icon: <CheckCircle2 className="w-3.5 h-3.5" />,
          label: 'PAID',
        };
      case 'PENDING_PAYMENT':
        return {
          bg: 'bg-amber-500/10 text-amber-300 border-amber-500/30',
          icon: <Clock className="w-3.5 h-3.5" />,
          label: 'PENDING_PAYMENT',
        };
      case 'PAYMENT_FAILED':
        return {
          bg: 'bg-rose-500/10 text-rose-300 border-rose-500/30',
          icon: <AlertTriangle className="w-3.5 h-3.5" />,
          label: 'PAYMENT_FAILED',
        };
      default:
        return {
          bg: 'bg-slate-800 text-slate-400 border-slate-700',
          icon: <Clock className="w-3.5 h-3.5" />,
          label: status,
        };
    }
  };

  return (
    <div className="flex-1 flex flex-col h-[calc(100vh-7rem)] overflow-hidden bg-slate-950">
      {/* Header */}
      <div className="p-4 sm:p-6 bg-slate-900 border-b border-slate-800 flex items-center justify-between shrink-0">
        <div>
          <div className="flex items-center gap-2">
            <History className="w-5 h-5 text-amber-400" />
            <h2 className="text-lg font-bold text-white">Order State Machine & History</h2>
          </div>
          <p className="text-xs text-slate-400">
            Authoritative order states with customer confirmation timestamps and safety guard signatures.
          </p>
        </div>

        <button
          onClick={fetchOrders}
          disabled={loading}
          className="p-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 border border-slate-700 transition-colors"
          title="Refresh"
        >
          <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
        </button>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-y-auto p-4 sm:p-6">
        {orders.length === 0 ? (
          <div className="text-center py-20 space-y-3">
            <History className="w-12 h-12 text-slate-700 mx-auto" />
            <p className="text-sm font-semibold text-slate-400">No orders created yet</p>
            <p className="text-xs text-slate-500">
              Create a checkout proposal through the AI shopping assistant or demo controls.
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {orders.map((ord) => {
              const statusInfo = getStatusBadge(ord.status);
              return (
                <div
                  key={ord.id}
                  className="bg-slate-900 border border-slate-800 hover:border-slate-700 rounded-2xl p-5 space-y-4 transition-all"
                >
                  <div className="flex items-center justify-between">
                    <div>
                      <span className="text-xs font-mono font-bold text-white">{ord.orderNumber}</span>
                      <span className="block text-[10px] text-slate-500 font-mono mt-0.5">
                        {new Date(ord.createdAt).toLocaleString()}
                      </span>
                    </div>
                    <span
                      className={`flex items-center gap-1 text-[10px] font-bold px-2.5 py-1 rounded-full border ${statusInfo.bg}`}
                    >
                      {statusInfo.icon}
                      {statusInfo.label}
                    </span>
                  </div>

                  {/* Items List */}
                  <div className="bg-slate-950/60 p-3 rounded-xl border border-slate-800/80 space-y-2 text-xs">
                    {ord.items?.map((item) => (
                      <div key={item.id} className="flex justify-between items-center text-slate-300">
                        <span className="truncate max-w-[180px]">
                          {item.quantity}x {item.productName}
                        </span>
                        <span className="font-mono font-semibold text-slate-100">
                          ₹{item.subtotal?.toLocaleString('en-IN')}
                        </span>
                      </div>
                    ))}
                    <div className="pt-2 border-t border-slate-800 flex justify-between items-center text-sm font-bold">
                      <span className="text-slate-400">Total</span>
                      <span className="text-emerald-400 font-mono">
                        ₹{ord.totalAmount?.toLocaleString('en-IN')}
                      </span>
                    </div>
                  </div>

                  {/* Safety & Confirmation Details */}
                  <div className="space-y-1.5 text-[11px]">
                    <div className="flex items-center justify-between text-slate-400">
                      <span>Customer Confirmation:</span>
                      <span className={ord.customerConfirmed ? 'text-emerald-400 font-semibold' : 'text-amber-400'}>
                        {ord.customerConfirmed ? 'Explicitly Confirmed' : 'Pending'}
                      </span>
                    </div>
                    <div className="flex items-center justify-between text-slate-400">
                      <span>Safety Guard:</span>
                      <span className={ord.safetyPassed ? 'text-emerald-400 font-semibold' : 'text-slate-500'}>
                        {ord.safetyPassed ? 'Verified (8/8 Checks)' : 'Pending'}
                      </span>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};