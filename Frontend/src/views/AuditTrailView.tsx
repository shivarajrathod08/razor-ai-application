import React, { useState, useEffect } from 'react';
import { 
  ShieldCheck, 
  ShieldAlert, 
  Clock, 
  RefreshCw, 
  Search, 
  Filter, 
  Bot, 
  User, 
  CreditCard, 
  Lock, 
  CheckCircle2, 
  AlertTriangle,
  Code,
  FileText
} from 'lucide-react';
import { AuditEvent } from '../types';
import { api } from '../services/api';

interface AuditTrailViewProps {
  sessionId: string;
}

export const AuditTrailView: React.FC<AuditTrailViewProps> = ({ sessionId }) => {
  const [events, setEvents] = useState<AuditEvent[]>([]);
  const [loading, setLoading] = useState(true);
  const [filterSession, setFilterSession] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState<AuditEvent | null>(null);
  const [searchQuery, setSearchQuery] = useState('');

  const fetchEvents = async () => {
    setLoading(true);
    try {
      if (filterSession && sessionId) {
        const data = await api.getAuditEventsBySession(sessionId);
        setEvents(data);
      } else {
        const data = await api.getAuditEvents();
        setEvents(data);
      }
    } catch (err: any) {
      console.error('Failed to load audit events:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEvents();
  }, [filterSession, sessionId]);

  const filteredEvents = events.filter((e) => {
    if (!searchQuery) return true;
    const q = searchQuery.toLowerCase();
    return (
      e.eventType.toLowerCase().includes(q) ||
      e.description.toLowerCase().includes(q) ||
      e.actor.toLowerCase().includes(q) ||
      (e.orderId && e.orderId.toLowerCase().includes(q))
    );
  });

  const getActorBadge = (actor: string) => {
    switch (actor) {
      case 'CUSTOMER':
        return {
          bg: 'bg-blue-500/10 text-blue-300 border-blue-500/30',
          icon: <User className="w-3.5 h-3.5" />,
          label: 'Customer',
        };
      case 'AI_AGENT':
        return {
          bg: 'bg-purple-500/10 text-purple-300 border-purple-500/30',
          icon: <Bot className="w-3.5 h-3.5" />,
          label: 'AI Commerce Agent',
        };
      case 'BACKEND_SAFETY_GUARD':
        return {
          bg: 'bg-emerald-500/10 text-emerald-300 border-emerald-500/30',
          icon: <Lock className="w-3.5 h-3.5" />,
          label: 'Backend Safety Guard',
        };
      case 'RAZORPAY_TEST_GATEWAY':
        return {
          bg: 'bg-amber-500/10 text-amber-300 border-amber-500/30',
          icon: <CreditCard className="w-3.5 h-3.5" />,
          label: 'Razorpay Test Gateway',
        };
      default:
        return {
          bg: 'bg-slate-800 text-slate-300 border-slate-700',
          icon: <ShieldCheck className="w-3.5 h-3.5" />,
          label: actor,
        };
    }
  };

  return (
    <div className="flex-1 flex flex-col h-[calc(100vh-7rem)] overflow-hidden bg-slate-950">
      {/* Header Controls */}
      <div className="p-4 sm:p-6 bg-slate-900 border-b border-slate-800 flex flex-wrap items-center justify-between gap-4 shrink-0">
        <div>
          <div className="flex items-center gap-2">
            <ShieldCheck className="w-5 h-5 text-emerald-400" />
            <h2 className="text-lg font-bold text-white">Immutable Audit Trail & Timeline</h2>
          </div>
          <p className="text-xs text-slate-400">
            Real-time chronological record of every conversational intent, pricing verification, confirmation gate, and payment event.
          </p>
        </div>

        <div className="flex items-center gap-2.5">
          <div className="relative">
            <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Filter by keyword, actor, order..."
              className="bg-slate-950 border border-slate-700/80 rounded-xl pl-9 pr-3 py-1.5 text-xs text-white placeholder:text-slate-500 focus:outline-none focus:border-brand-500 w-48 sm:w-64"
            />
          </div>

          <button
            onClick={() => setFilterSession(!filterSession)}
            className={`px-3 py-1.5 rounded-xl text-xs font-semibold border transition-all ${
              filterSession
                ? 'bg-brand-600/20 text-brand-300 border-brand-500/40'
                : 'bg-slate-800 text-slate-300 border-slate-700 hover:bg-slate-700'
            }`}
          >
            Current Session Only
          </button>

          <button
            onClick={fetchEvents}
            disabled={loading}
            className="p-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 border border-slate-700 transition-colors"
            title="Refresh"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
          </button>
        </div>
      </div>

      {/* Main Content: Timeline + Inspector */}
      <div className="flex-1 flex overflow-hidden">
        {/* Timeline Scroll */}
        <div className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-4">
          {filteredEvents.length === 0 ? (
            <div className="text-center py-20 space-y-3">
              <ShieldCheck className="w-12 h-12 text-slate-700 mx-auto" />
              <p className="text-sm font-semibold text-slate-400">No audit events found</p>
              <p className="text-xs text-slate-500">Interact with the shopping assistant to generate live audit logs.</p>
            </div>
          ) : (
            <div className="relative pl-6 border-l-2 border-slate-800 space-y-6">
              {filteredEvents.map((evt) => {
                const actorInfo = getActorBadge(evt.actor);
                const isSelected = selectedEvent?.eventId === evt.eventId;

                return (
                  <div key={evt.eventId} className="relative group">
                    {/* Dot on Timeline */}
                    <div
                      className={`absolute -left-[31px] top-1.5 w-3.5 h-3.5 rounded-full border-2 border-slate-950 ${
                        evt.eventType === 'PRICE_TAMPERING_ATTEMPT'
                          ? 'bg-amber-400'
                          : evt.success
                          ? 'bg-emerald-400'
                          : 'bg-rose-500'
                      }`}
                    />

                    {/* Event Card */}
                    <div
                      onClick={() => setSelectedEvent(evt)}
                      className={`p-4 rounded-xl border transition-all cursor-pointer ${
                        isSelected
                          ? 'bg-slate-800/90 border-brand-500/50 shadow-md shadow-brand-500/5'
                          : 'bg-slate-900/80 border-slate-800 hover:border-slate-700 hover:bg-slate-900'
                      }`}
                    >
                      <div className="flex flex-wrap items-center justify-between gap-2 mb-2">
                        <div className="flex items-center gap-2">
                          <span
                            className={`flex items-center gap-1 text-[10px] font-bold px-2 py-0.5 rounded-full border ${actorInfo.bg}`}
                          >
                            {actorInfo.icon}
                            {actorInfo.label}
                          </span>
                          <span className="text-xs font-mono font-bold text-white tracking-wide">
                            {evt.eventType}
                          </span>
                        </div>

                        <div className="flex items-center gap-3 text-[11px] text-slate-400">
                          {evt.amount && (
                            <span className="font-mono font-bold text-emerald-400">
                              ₹{evt.amount.toLocaleString('en-IN')}
                            </span>
                          )}
                          <span className="font-mono flex items-center gap-1">
                            <Clock className="w-3 h-3 text-slate-500" />
                            {new Date(evt.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' })}
                          </span>
                        </div>
                      </div>

                      <p className="text-xs text-slate-300 leading-relaxed">
                        {evt.description}
                      </p>

                      {/* Footer metadata pills */}
                      <div className="mt-2.5 flex flex-wrap items-center gap-2 text-[10px] text-slate-500 font-mono">
                        <span>Event ID: {evt.eventId.substring(0, 8)}...</span>
                        {evt.orderId && <span>· Order: {evt.orderId}</span>}
                        {evt.metadataJson && (
                          <span className="text-brand-400 hover:underline flex items-center gap-0.5 ml-auto">
                            <Code className="w-3 h-3" /> Inspect Payload
                          </span>
                        )}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

        {/* Selected Event Inspector Side Panel */}
        {selectedEvent && (
          <div className="w-80 sm:w-96 bg-slate-900 border-l border-slate-800 p-4 sm:p-6 overflow-y-auto flex flex-col justify-between shrink-0 animate-fade-in">
            <div className="space-y-4">
              <div className="flex items-center justify-between pb-3 border-b border-slate-800">
                <h4 className="font-bold text-sm text-white flex items-center gap-2">
                  <FileText className="w-4 h-4 text-brand-400" />
                  Audit Event Details
                </h4>
                <button
                  onClick={() => setSelectedEvent(null)}
                  className="text-xs text-slate-400 hover:text-white"
                >
                  Close
                </button>
              </div>

              <div className="space-y-2 text-xs">
                <div>
                  <span className="text-slate-500 block text-[10px] uppercase font-bold">Event Type</span>
                  <span className="font-mono font-bold text-brand-300">{selectedEvent.eventType}</span>
                </div>
                <div>
                  <span className="text-slate-500 block text-[10px] uppercase font-bold">Actor</span>
                  <span className="text-slate-200">{selectedEvent.actor}</span>
                </div>
                <div>
                  <span className="text-slate-500 block text-[10px] uppercase font-bold">Timestamp</span>
                  <span className="font-mono text-slate-300">{new Date(selectedEvent.timestamp).toLocaleString()}</span>
                </div>
                {selectedEvent.amount && (
                  <div>
                    <span className="text-slate-500 block text-[10px] uppercase font-bold">Amount</span>
                    <span className="font-mono font-bold text-emerald-400">₹{selectedEvent.amount.toLocaleString('en-IN')}</span>
                  </div>
                )}
                <div>
                  <span className="text-slate-500 block text-[10px] uppercase font-bold">Description</span>
                  <p className="text-slate-200 leading-relaxed bg-slate-950 p-2.5 rounded-lg border border-slate-800">
                    {selectedEvent.description}
                  </p>
                </div>
              </div>

              {/* JSON Metadata Viewer */}
              {selectedEvent.metadataJson && (
                <div className="space-y-1.5">
                  <span className="text-slate-500 block text-[10px] uppercase font-bold">
                    Raw Verification Metadata JSON
                  </span>
                  <pre className="bg-slate-950 p-3 rounded-xl border border-slate-800 text-[11px] font-mono text-cyan-300 overflow-x-auto max-h-60">
                    {JSON.stringify(JSON.parse(selectedEvent.metadataJson), null, 2)}
                  </pre>
                </div>
              )}
            </div>

            <div className="pt-4 border-t border-slate-800 text-[10px] text-slate-500 text-center">
              🛡️ Cryptographically verifiable session event
            </div>
          </div>
        )}
      </div>
    </div>
  );
};