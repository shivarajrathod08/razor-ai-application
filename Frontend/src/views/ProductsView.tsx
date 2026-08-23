import React, { useState, useEffect } from 'react';
import { 
  Layers, 
  Search, 
  Plus, 
  ShieldAlert, 
  Check, 
  Sparkles, 
  ExternalLink 
} from 'lucide-react';
import { Product } from '../types';
import { api } from '../services/api';

interface ProductsViewProps {
  onAddToCart: (productId: number) => void;
  onOpenPriceTamperModal: () => void;
}

export const ProductsView: React.FC<ProductsViewProps> = ({
  onAddToCart,
  onOpenPriceTamperModal,
}) => {
  const [products, setProducts] = useState<Product[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const data = await api.getProducts();
        setProducts(data);
      } catch (err) {
        console.error('Failed to load products:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchProducts();
  }, []);

  const categories = ['ALL', 'Bags', 'Electronics', 'Office', 'Accessories', 'Gadgets'];

  const filtered = products.filter((p) => {
    const matchesCat = selectedCategory === 'ALL' || p.category.toLowerCase() === selectedCategory.toLowerCase();
    const matchesSearch =
      !searchQuery ||
      p.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      p.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
      p.tags.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesCat && matchesSearch;
  });

  return (
    <div className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8 bg-slate-950 space-y-6">
      {/* Header */}
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <Layers className="w-5 h-5 text-cyan-400" />
            <h2 className="text-xl font-bold text-white">Merchant Product Catalog</h2>
          </div>
          <p className="text-xs text-slate-400">
            Authoritative database items with server-enforced pricing and inventory stock levels.
          </p>
        </div>

        <button
          onClick={onOpenPriceTamperModal}
          className="flex items-center gap-2 px-4 py-2 rounded-xl bg-amber-600/20 hover:bg-amber-600/30 text-amber-300 border border-amber-500/30 text-xs font-bold transition-all shadow-sm"
        >
          <ShieldAlert className="w-4 h-4 text-amber-400" />
          Test Price Tampering Defense
        </button>
      </div>

      {/* Filter Bar */}
      <div className="flex flex-wrap items-center justify-between gap-3 bg-slate-900 p-3 rounded-2xl border border-slate-800">
        <div className="flex flex-wrap items-center gap-1.5">
          {categories.map((cat) => (
            <button
              key={cat}
              onClick={() => setSelectedCategory(cat)}
              className={`px-3 py-1.5 rounded-xl text-xs font-semibold transition-all ${
                selectedCategory === cat
                  ? 'bg-brand-600 text-white shadow-sm'
                  : 'bg-slate-950 text-slate-400 hover:text-white border border-slate-800'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>

        <div className="relative">
          <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search catalog..."
            className="bg-slate-950 border border-slate-700/80 rounded-xl pl-9 pr-3 py-1.5 text-xs text-white placeholder:text-slate-500 focus:outline-none focus:border-brand-500 w-48 sm:w-64"
          />
        </div>
      </div>

      {/* Product Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
        {filtered.map((prod) => (
          <div
            key={prod.id}
            className="bg-slate-900 border border-slate-800 hover:border-slate-700 rounded-2xl p-4 flex flex-col justify-between transition-all group"
          >
            <div className="space-y-3">
              <div className="h-40 rounded-xl overflow-hidden bg-slate-950 relative">
                <img
                  src={prod.imageUrl}
                  alt={prod.name}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                />
                <span className="absolute top-2 right-2 px-2.5 py-1 rounded-lg bg-slate-950/80 backdrop-blur-sm text-xs font-extrabold text-emerald-400 border border-emerald-500/30 font-mono">
                  ₹{prod.price?.toLocaleString('en-IN')}
                </span>
                <span className="absolute bottom-2 left-2 text-[10px] font-semibold px-2 py-0.5 rounded bg-slate-950/80 text-cyan-300 border border-cyan-500/30">
                  Stock: {prod.stock}
                </span>
              </div>

              <div>
                <span className="text-[10px] font-bold uppercase tracking-wider text-brand-400">
                  {prod.category}
                </span>
                <h4 className="font-bold text-sm text-white line-clamp-1">{prod.name}</h4>
                <p className="text-xs text-slate-400 line-clamp-2 mt-1 leading-relaxed">
                  {prod.description}
                </p>
              </div>
            </div>

            <div className="pt-4 mt-2 border-t border-slate-800/80 space-y-2">
              {prod.upsellRationale && (
                <div className="text-[10px] text-purple-300/90 bg-purple-950/30 p-2 rounded-lg border border-purple-500/20 leading-tight">
                  <span className="font-bold text-purple-200">Affinity:</span> {prod.upsellRationale}
                </div>
              )}

              <button
                onClick={() => onAddToCart(prod.id)}
                className="w-full py-2 rounded-xl bg-brand-600 hover:bg-brand-500 text-white font-bold text-xs flex items-center justify-center gap-1.5 transition-colors shadow-sm"
              >
                <Plus className="w-3.5 h-3.5" />
                Add to Cart
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};