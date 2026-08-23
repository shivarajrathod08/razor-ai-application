package com.hackathon.service;


import com.hackathon.dto.ProductDto;
import com.hackathon.exception.ResourceNotFoundException;
import com.hackathon.model.Product;
import com.hackathon.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostConstruct
    @Transactional
    public void seedCatalogIfEmpty() {
        if (productRepository.count() == 0) {
            log.info("Catalog is empty. Seeding realistic demo catalog with 20 curated products...");
            List<Product> products = createDemoProducts();
            productRepository.saveAll(products);
            log.info("Seeded {} products into catalog.", products.size());
        }
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAllActiveProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Product getProductEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        return ProductDto.fromEntity(getProductEntity(id));
    }

    @Transactional(readOnly = true)
    public List<ProductDto> searchProducts(String query, BigDecimal maxPrice) {
        if (query == null || query.trim().isEmpty()) {
            if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
                return productRepository.findByActiveTrueAndPriceLessThanEqual(maxPrice).stream()
                        .map(ProductDto::fromEntity).collect(Collectors.toList());
            }
            return getAllActiveProducts();
        }

        List<Product> results;
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            results = productRepository.searchProductsWithBudget(query.trim(), maxPrice);
        } else {
            results = productRepository.searchProducts(query.trim());
        }
        return results.stream().map(ProductDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCaseAndActiveTrue(category).stream()
                .map(ProductDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getUpsellRecommendations(Long productId) {
        Product base = getProductEntity(productId);
        if (base.getUpsellProductIds() == null || base.getUpsellProductIds().trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> upsellIds = Arrays.stream(base.getUpsellProductIds().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());

        return productRepository.findAllById(upsellIds).stream()
                .filter(Product::isActive)
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deductStock(Long productId, int quantity) {
        Product p = getProductEntity(productId);
        if (p.getStock() < quantity) {
            throw new IllegalArgumentException("Insufficient stock for product " + p.getName() + " (Available: " + p.getStock() + ")");
        }
        p.setStock(p.getStock() - quantity);
        productRepository.save(p);
    }

    private List<Product> createDemoProducts() {
        List<Product> list = new ArrayList<>();
        // Bags
        list.add(new Product("Urban Laptop Backpack", "Premium 15.6-inch water-resistant laptop backpack with dedicated cushioned compartments and USB charging port.", "Bags", new BigDecimal("1499.00"), 45, "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500&auto=format&fit=crop&q=60", "laptop bag,backpack,travel,waterproof,work", "2,3,6", "Customers buying this backpack frequently add a USB-C hub and silent mouse for a complete mobile workspace."));
        list.add(new Product("USB-C 7-in-1 Multiport Hub", "High-speed 4K HDMI, 100W Power Delivery, SD/TF card reader, and 3x USB 3.0 ports for MacBook, Dell, and Windows laptops.", "Electronics", new BigDecimal("799.00"), 80, "https://images.unsplash.com/photo-1625842268584-8f3296236761?w=500&auto=format&fit=crop&q=60", "hub,adapter,usb-c,hdmi,ports,macbook", "1,5", "Essential accessory for laptops with limited Type-C ports to connect external monitors and peripherals."));
        list.add(new Product("Bluetooth Silent Ergonomic Mouse", "Rechargeable wireless mouse with whisper-quiet clicks, dual Bluetooth 5.2 + 2.4G USB, and ergonomic thumb rest.", "Accessories", new BigDecimal("649.00"), 60, "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=500&auto=format&fit=crop&q=60", "mouse,wireless,bluetooth,silent,ergonomic", "1,4", "Complements any laptop or desk setup for silent, fatigue-free productivity."));
        list.add(new Product("Slim Executive Laptop Sleeve", "Minimalist shock-absorbent 14-inch sleeve with magnetic snap closure and soft velvet lining.", "Bags", new BigDecimal("899.00"), 35, "https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=500&auto=format&fit=crop&q=60", "sleeve,slim,laptop,case,minimalist", "2,3", "Pairs perfectly with compact USB-C adapters and slim mice for lightweight carry."));

        // Office & Setup
        list.add(new Product("Aluminum Foldable Laptop Stand", "Ergonomic 6-level height adjustable aluminum riser promoting better posture and maximum laptop ventilation.", "Office", new BigDecimal("1299.00"), 50, "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500&auto=format&fit=crop&q=60", "stand,laptop riser,ergonomic,desk,aluminum", "1,2,7", "Greatly improves ergonomic posture when paired with external keyboard and mouse."));
        list.add(new Product("GaN 65W Ultra-Fast Charger", "Compact dual USB-C + USB-A GaN III fast wall charger supporting laptops, iPads, iPhones, and Android.", "Electronics", new BigDecimal("1199.00"), 90, "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=500&auto=format&fit=crop&q=60", "charger,fast charging,gan,type-c,power adapter", "1,2,15", "Replaces bulky OEM power bricks with a pocket-sized 65W multi-device charger."));
        list.add(new Product("Ergonomic Mechanical Keyboard", "Compact 75% hot-swappable tactile mechanical keyboard with RGB backlighting and multi-device Bluetooth.", "Electronics", new BigDecimal("2499.00"), 25, "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500&auto=format&fit=crop&q=60", "keyboard,mechanical,rgb,bluetooth,typing", "3,5,8", "Pairs with an ergonomic mouse and desk mat for the ultimate developer desk setup."));
        list.add(new Product("XL Leather Desk Mat & Pad", "Waterproof vegan leather extra-large desk blotter (90cm x 45cm) for smooth mouse tracking and desk protection.", "Office", new BigDecimal("499.00"), 70, "https://images.unsplash.com/photo-1616401784845-180882ba9ba8?w=500&auto=format&fit=crop&q=60", "desk mat,mousepad,leather,desk accessory,office", "7,3", "Protects the desk while elevating aesthetics and mouse gliding precision."));

        // Audio & Visual
        list.add(new Product("Wireless Active Noise Cancelling Headphones", "Over-ear Bluetooth 5.3 headphones with 40dB hybrid ANC, 50-hour battery life, and high-res audio drivers.", "Electronics", new BigDecimal("2999.00"), 40, "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&auto=format&fit=crop&q=60", "headphones,anc,noise cancelling,wireless,audio", "1,10", "Block out background distractions for focused work sessions and video calls."));
        list.add(new Product("4K Ultra-HD Pro Webcam", "Ultra HD webcam with auto-focus, dual noise-reducing microphones, and physical privacy shutter.", "Electronics", new BigDecimal("3499.00"), 30, "https://images.unsplash.com/photo-1587826080692-f439cd0b70da?w=500&auto=format&fit=crop&q=60", "webcam,4k,streaming,video call,camera", "11,5", "Essential upgrade for crystal-clear Zoom meetings and client presentations."));
        list.add(new Product("Adjustable LED Ring Light with Tripod", "10-inch studio ring light with 3 color modes, 10 brightness levels, and 360-degree ball head mount.", "Gadgets", new BigDecimal("1299.00"), 45, "https://images.unsplash.com/photo-1512496015851-a90fb38ba796?w=500&auto=format&fit=crop&q=60", "ring light,lighting,tripod,streaming,video", "10,14", "Provides studio-grade face lighting for video conferences and content creation."));

        // Mobile & Travel Gadgets
        list.add(new Product("Portable Power Bank 20000mAh", "Heavy-duty 22.5W fast charging power bank with digital LED battery percentage and triple outputs.", "Gadgets", new BigDecimal("1799.00"), 65, "https://images.unsplash.com/photo-1609592807904-8b61e27a7c8e?w=500&auto=format&fit=crop&q=60", "powerbank,battery,portable,fast charge,travel", "1,6,15", "Keeps phones and tablets powered during commutes and business trips."));
        list.add(new Product("Anti-Theft Commuter Crossbody Bag", "Waterproof sling bag with TSA lock, hidden card slots, and integrated USB cable port.", "Bags", new BigDecimal("1799.00"), 30, "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=500&auto=format&fit=crop&q=60", "crossbody,sling bag,anti-theft,travel,commute", "12,15", "Compact travel bag ideal for storing power banks and passports securely."));
        list.add(new Product("Professional Aluminum Camera Tripod", "Lightweight 60-inch panoramic ball-head tripod with quick release plate and phone mount.", "Accessories", new BigDecimal("1599.00"), 20, "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=500&auto=format&fit=crop&q=60", "tripod,camera,photography,mount,stable", "10,11", "Ensures shake-free photos, video recording, and webcam stability."));
        list.add(new Product("Braided 100W USB-C to USB-C Cable (2M)", "Military-grade nylon braided 2-meter fast charging and 480Mbps data transfer cable.", "Accessories", new BigDecimal("349.00"), 120, "https://images.unsplash.com/photo-1585338107529-13afc5f02586?w=500&auto=format&fit=crop&q=60", "cable,type-c,fast charging,braided,100w", "6,12", "Durable long cable compatible with 65W GaN chargers and power banks."));

        return list;
    }
}