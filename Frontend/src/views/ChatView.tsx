import React, { useState, useEffect, useRef } from 'react';
import { 
  Send, 
  Bot, 
  User, 
  Sparkles, 
  Plus, 
  Check, 
  ShoppingCart, 
  Trash2, 
  ArrowRight, 
  ShieldCheck, 
  Info, 
  Lock, 
  HelpCircle,
  CreditCard,
  RotateCcw
} from 'lucide-react';
import { Cart, ChatResponse, CheckoutProposal, Product } from '../types';
import { api } from '../services/api';

interface ChatMessage {
  id: string;
  sender: 'user' | 'ai';
  text: string;
  recommendedProducts?: Product[];
  suggestedUpsell?: Product;
  upsellExplanation?: string;
  toolCalls?: string[];
  checkoutProposal?: CheckoutProposal;
  timestamp: string;
}

interface ChatViewProps {
  sessionId: string;
  cart: Cart | null;
  onUpdateCart: (cart: Cart) => void;
  onOpenCheckoutProposal: (proposal: CheckoutProposal) => void;
  onCartDrawerChange?: (isOpen: boolean) => void;
  isCartDrawerOpen?: boolean;
}

export const ChatView: React.FC<ChatViewProps> = ({
  sessionId,
  cart,
  onUpdateCart,
  onOpenCheckoutProposal,
  onCartDrawerChange,
  isCartDrawerOpen = false,
}) => {
  const [inputMessage, setInputMessage] = useState('');
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [showWhyDropdown, setShowWhyDropdown] = useState<Record<number, boolean>>({});
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // Initialize with greeting
  useEffect(() => {
    if (messages.length === 0) {
      setMessages([
        {
          id: '1',
          sender: 'ai',
          text: "Hello! I'm **RazorAI**, your intelligent shopping assistant. I can help you discover products, suggest compatible accessories, and guide you through safe, customer-gated checkouts.\n\nTry asking: *\"I need a laptop bag under ₹2,000\"* or *\"Show me desk setup accessories\"*.",
          timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        },
      ]);
    }
  }, []);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isLoading]);

  const handleSend = async (customText?: string) => {
    const textToSend = customText || inputMessage.trim();
    if (!textToSend || isLoading) return;

    const userMsg: ChatMessage = {
      id: Date.now().toString(),
      sender: 'user',
      text: textToSend,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };

    setMessages((prev) => [...prev, userMsg]);
    if (!customText) setInputMessage('');
    setIsLoading(true);

    try {
      const response: ChatResponse = await api.sendChatMessage(sessionId, textToSend);

      if (response.cart) {
        onUpdateCart(response.cart);
      }

      const aiMsg: ChatMessage = {
        id: (Date.now() + 1).toString(),
        sender: 'ai',
        text: response.reply,
        recommendedProducts: response.recommendedProducts,
        suggestedUpsell: response.suggestedUpsell,
        upsellExplanation: response.upsellExplanation,
        toolCalls: response.toolCallsExecuted,
        checkoutProposal: response.checkoutProposal,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      };

      setMessages((prev) => [...prev, aiMsg]);

      // If AI created a checkout proposal and requested confirmation, open modal
      if (response.checkoutProposal && response.paymentConfirmationRequested) {
        onOpenCheckoutProposal(response.checkoutProposal);
      }
    } catch (err: any) {
      const errorMsg: ChatMessage = {
        id: (Date.now() + 1).toString(),
        sender: 'ai',
        text: `Sorry, I encountered an issue: ${err.message || 'Server error'}. Please try again.`,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      };
      setMessages((prev) => [...prev, errorMsg]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleAddToCart = async (productId: number, isUpsell = false) => {
    try {
      const updatedCart = await api.addToCart(sessionId, productId, 1, isUpsell);
      onUpdateCart(updatedCart);
      
      // Notify conversation
      if (isUpsell) {
        handleSend("I have accepted the upsell suggestion. Please show my updated cart.");
      } else {
        handleSend("Add to cart");
      }
    } catch (err: any) {
      alert(err.message || 'Failed to add item to cart');
    }
  };

  const handleRemoveFromCart = async (productId: number) => {
    try {
      const updatedCart = await api.removeFromCart(sessionId, productId);
      onUpdateCart(updatedCart);
    } catch (err: any) {
      alert(err.message || 'Failed to remove item');
    }
  };

  const handleStartCheckout = async () => {
    try {
      const proposal = await api.proposeCheckout(sessionId);
      onOpenCheckoutProposal(proposal);
    } catch (err: any) {
      alert(err.message || 'Failed to initiate checkout proposal');
    }
  };

  const toggleWhy = (productId: number) => {
    setShowWhyDropdown((prev) => ({ ...prev, [productId]: !prev[productId] }));
  };

  return (
    <div className="flex-1 flex overflow-hidden bg-slate-950">
      {/* Main Chat Area */}
      <div className="flex-1 flex flex-col h-[calc(100vh-7rem)] overflow-hidden">
        {/* Messages Scroll Area */}
        <div className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-6">
          {messages.map((msg) => (
            <div
              key={msg.id}
              className={`flex items-start gap-3 max-w-3xl ${
                msg.sender === 'user' ? 'ml-auto flex-row-reverse' : 'mr-auto'
              }`}
            >
              {/* Avatar */}
              <div
                className={`w-8 h-8 rounded-xl shrink-0 flex items-center justify-center text-xs font-bold ${
                  msg.sender === 'user'
                    ? 'bg-brand-600 text-white'
                    : 'bg-slate-900 border border-slate-700 text-brand-400'
                }`}
              >
                {msg.sender === 'user' ? <User className="w-4 h-4" /> : <Bot className="w-4 h-4" />}
              </div>

              {/* Message Bubble */}
              <div className="space-y-3 max-w-xl">
                <div
                  className={`p-4 rounded-2xl text-sm leading-relaxed ${
                    msg.sender === 'user'
                      ? 'bg-brand-600 text-white rounded-tr-none shadow-md shadow-brand-500/10'
                      : 'bg-slate-900 text-slate-200 border border-slate-800 rounded-tl-none shadow-sm'
                  }`}
                >
                  <p className="whitespace-pre-line">{msg.text}</p>
                  <span
                    className={`block text-[10px] mt-2 font-mono ${
                      msg.sender === 'user' ? 'text-brand-200' : 'text-slate-500'
                    }`}
                  >
                    {msg.timestamp}
                  </span>
                </div>

                {/* Embedded Product Cards (if recommended) */}
                {msg.recommendedProducts && msg.recommendedProducts.length > 0 && (
                  <div className="space-y-3 pt-1">
                    <div className="text-xs font-semibold text-slate-400 flex items-center gap-1.5">
                      <Sparkles className="w-3.5 h-3.5 text-cyan-400" />
                      Recommended Catalog Matches:
                    </div>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                      {msg.recommendedProducts.slice(0, 3).map((prod) => (
                        <div
                          key={prod.id}
                          className="bg-slate-900/90 border border-slate-800 rounded-xl p-3 flex flex-col justify-between hover:border-slate-700 transition-all group"
                        >
                          <div className="space-y-2">
                            <div className="h-28 rounded-lg overflow-hidden bg-slate-950 relative">
                              <img
                                src={prod.imageUrl}
                                alt={prod.name}
                                className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                              />
                              <span className="absolute top-1.5 right-1.5 px-2 py-0.5 rounded-md bg-slate-950/80 backdrop-blur-sm text-[10px] font-bold text-emerald-400 border border-emerald-500/30">
                                ₹{prod.price?.toLocaleString('en-IN')}
                              </span>
                            </div>

                            <div>
                              <span className="text-[10px] font-semibold uppercase text-brand-400 tracking-wider">
                                {prod.category}
                              </span>
                              <h5 className="font-bold text-xs text-white line-clamp-1">{prod.name}</h5>
                              <p className="text-[11px] text-slate-400 line-clamp-2 mt-0.5">
                                {prod.description}
                              </p>
                            </div>
                          </div>

                          {/* Explainability Pill */}
                          <div className="pt-2.5 space-y-2">
                            <button
                              type="button"
                              onClick={() => toggleWhy(prod.id)}
                              className="text-[11px] font-medium text-brand-400 hover:text-brand-300 flex items-center gap-1"
                            >
                              <Info className="w-3 h-3" />
                              Why this product?
                            </button>

                            {showWhyDropdown[prod.id] && (
                              <div className="bg-slate-950 p-2 rounded-lg border border-slate-800 text-[10px] text-slate-300 space-y-1 animate-fade-in">
                                <div className="text-emerald-400 font-semibold flex items-center gap-1">
                                  <Check className="w-3 h-3" /> Within your ₹2,000 budget
                                </div>
                                <div className="text-emerald-400 font-semibold flex items-center gap-1">
                                  <Check className="w-3 h-3" /> Supports 15.6" laptops + water resistant
                                </div>
                                <div className="text-emerald-400 font-semibold flex items-center gap-1">
                                  <Check className="w-3 h-3" /> Top rated in Bags & Travel
                                </div>
                              </div>
                            )}

                            <button
                              onClick={() => handleAddToCart(prod.id, false)}
                              className="w-full py-1.5 rounded-lg bg-brand-600 hover:bg-brand-500 text-white font-bold text-xs flex items-center justify-center gap-1.5 transition-colors shadow-sm"
                            >
                              <Plus className="w-3.5 h-3.5" />
                              Add to Cart (₹{prod.price})
                            </button>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {/* Upsell Suggestion Card */}
                {msg.suggestedUpsell && (
                  <div className="bg-gradient-to-br from-purple-950/40 via-slate-900 to-purple-950/20 border border-purple-500/30 rounded-xl p-4 space-y-3 animate-fade-in">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <span className="w-2 h-2 rounded-full bg-purple-400 animate-ping" />
                        <span className="text-xs font-bold text-purple-300 uppercase tracking-wider">
                          💡 Explainable Upsell Suggestion
                        </span>
                      </div>
                      <span className="text-xs font-bold text-emerald-400 font-mono">
                        ₹{msg.suggestedUpsell.price?.toLocaleString('en-IN')}
                      </span>
                    </div>

                    <div className="flex items-center gap-3">
                      <img
                        src={msg.suggestedUpsell.imageUrl}
                        alt={msg.suggestedUpsell.name}
                        className="w-14 h-14 rounded-lg object-cover bg-slate-950 border border-purple-500/20 shrink-0"
                      />
                      <div>
                        <h6 className="font-bold text-xs text-white">{msg.suggestedUpsell.name}</h6>
                        <p className="text-[11px] text-purple-200/80 leading-snug mt-0.5">
                          {msg.upsellExplanation || msg.suggestedUpsell.description}
                        </p>
                      </div>
                    </div>

                    <div className="flex items-center gap-2 pt-1">
                      <button
                        onClick={() => handleAddToCart(msg.suggestedUpsell!.id, true)}
                        className="flex-1 py-1.5 rounded-lg bg-purple-600 hover:bg-purple-500 text-white font-bold text-xs flex items-center justify-center gap-1.5 transition-colors"
                      >
                        <Plus className="w-3.5 h-3.5" />
                        Add Upsell (+₹{msg.suggestedUpsell.price})
                      </button>
                    </div>
                  </div>
                )}

                {/* Executed Tools Badge */}
                {msg.toolCalls && msg.toolCalls.length > 0 && (
                  <div className="flex flex-wrap gap-1 pt-1">
                    {msg.toolCalls.map((tool, idx) => (
                      <span
                        key={idx}
                        className="text-[9px] font-mono px-2 py-0.5 rounded bg-slate-950 text-slate-400 border border-slate-800"
                        title={tool}
                      >
                        ⚙️ {tool.split('(')[0]}
                      </span>
                    ))}
                  </div>
                )}
              </div>
            </div>
          ))}

          {isLoading && (
            <div className="flex items-center gap-3 mr-auto max-w-md">
              <div className="w-8 h-8 rounded-xl bg-slate-900 border border-slate-700 flex items-center justify-center text-brand-400">
                <Bot className="w-4 h-4 animate-bounce" />
              </div>
              <div className="bg-slate-900 border border-slate-800 rounded-2xl rounded-tl-none p-3.5 text-xs text-slate-400 flex items-center gap-2">
                <div className="w-1.5 h-1.5 rounded-full bg-brand-400 animate-pulse" />
                <span>RazorAI is querying catalog & verifying prices...</span>
              </div>
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>

        {/* Input Bar */}
        <div className="p-4 bg-slate-900/90 border-t border-slate-800">
          <form
            onSubmit={(e) => {
              e.preventDefault();
              handleSend();
            }}
            className="flex items-center gap-2 max-w-4xl mx-auto"
          >
            <input
              type="text"
              value={inputMessage}
              onChange={(e) => setInputMessage(e.target.value)}
              placeholder="Ask for products, e.g. 'I need a laptop bag under ₹2,000'..."
              disabled={isLoading}
              className="flex-1 bg-slate-950 border border-slate-700/80 rounded-xl px-4 py-3 text-sm text-white placeholder:text-slate-500 focus:outline-none focus:border-brand-500 transition-colors"
            />
            <button
              type="submit"
              disabled={isLoading || !inputMessage.trim()}
              className="px-5 py-3 rounded-xl bg-brand-600 hover:bg-brand-500 text-white font-bold text-sm shadow-lg shadow-brand-500/20 transition-all flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <Send className="w-4 h-4" />
              <span className="hidden sm:inline">Send</span>
            </button>
          </form>
        </div>
      </div>

      {/* Cart Drawer / Sidebar */}
      <div
        className={`w-80 sm:w-96 bg-slate-900 border-l border-slate-800 flex flex-col justify-between transition-all duration-300 ${
          isCartDrawerOpen ? 'translate-x-0' : 'hidden lg:flex'
        }`}
      >
        <div className="p-4 border-b border-slate-800 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <ShoppingCart className="w-4 h-4 text-brand-400" />
            <h4 className="font-bold text-sm text-white">Active Cart</h4>
            <span className="text-xs px-2 py-0.5 rounded-full bg-slate-800 text-slate-300 font-mono">
              {cart?.totalItemCount || 0}
            </span>
          </div>
          <div className="text-[10px] text-emerald-400 font-semibold px-2 py-0.5 rounded bg-emerald-500/10 border border-emerald-500/20">
            Zero-Trust Pricing
          </div>
        </div>

        {/* Cart Items List */}
        <div className="flex-1 overflow-y-auto p-4 space-y-3">
          {cart && cart.items && cart.items.length > 0 ? (
            cart.items.map((item) => (
              <div
                key={item.id || item.productId}
                className="bg-slate-950 p-3 rounded-xl border border-slate-800/80 flex items-center justify-between gap-3 group"
              >
                <img
                  src={item.productImageUrl}
                  alt={item.productName}
                  className="w-12 h-12 rounded-lg object-cover bg-slate-900 shrink-0"
                />
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-1.5">
                    <h6 className="font-bold text-xs text-white truncate">{item.productName}</h6>
                    {item.upsellItem && (
                      <span className="text-[9px] font-bold px-1 rounded bg-purple-500/20 text-purple-300">
                        Upsell
                      </span>
                    )}
                  </div>
                  <div className="flex items-center justify-between text-xs text-slate-400 mt-1">
                    <span>Qty: {item.quantity}</span>
                    <span className="font-bold text-slate-200 font-mono">
                      ₹{item.subtotal?.toLocaleString('en-IN')}
                    </span>
                  </div>
                </div>
                <button
                  onClick={() => handleRemoveFromCart(item.productId)}
                  className="p-1 rounded text-slate-500 hover:text-rose-400 hover:bg-slate-800 transition-colors"
                  title="Remove"
                >
                  <Trash2 className="w-3.5 h-3.5" />
                </button>
              </div>
            ))
          ) : (
            <div className="text-center py-12 space-y-2">
              <ShoppingCart className="w-10 h-10 text-slate-700 mx-auto" />
              <p className="text-xs text-slate-400">Your cart is currently empty</p>
              <p className="text-[11px] text-slate-500">Ask the assistant to find products or use Demo Controls.</p>
            </div>
          )}
        </div>

        {/* Cart Total & Checkout CTA */}
        {cart && cart.items && cart.items.length > 0 && (
          <div className="p-4 bg-slate-950 border-t border-slate-800 space-y-3">
            <div className="space-y-1">
              <div className="flex items-center justify-between text-xs text-slate-400">
                <span>Calculated Subtotal</span>
                <span className="font-bold text-white font-mono text-sm">
                  ₹{cart.calculatedTotal?.toLocaleString('en-IN')}
                </span>
              </div>
              <div className="flex items-center justify-between text-[11px] text-slate-500">
                <span>Max Safety Limit</span>
                <span className="text-emerald-400 font-mono">₹10,000.00</span>
              </div>
            </div>

            <button
              onClick={handleStartCheckout}
              className="w-full py-3 rounded-xl bg-gradient-to-r from-brand-600 to-emerald-600 hover:from-brand-500 hover:to-emerald-500 text-white font-bold text-xs shadow-lg shadow-emerald-500/10 transition-all flex items-center justify-center gap-2"
            >
              <Lock className="w-3.5 h-3.5" />
              Proceed to Gated Checkout (₹{cart.calculatedTotal})
            </button>
          </div>
        )}
      </div>
    </div>
  );
};