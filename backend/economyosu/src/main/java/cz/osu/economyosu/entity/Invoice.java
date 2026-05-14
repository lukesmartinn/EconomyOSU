package cz.osu.economyosu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entita reprezentující fakturu (vydanou nebo přijatou).
 */
@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Číslo faktury (např. 2024001, F-2024-001)
     */
    @Column(nullable = false, unique = true, length = 50)
    private String invoiceNumber;

    /**
     * Typ faktury - vydaná nebo přijatá
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvoiceType type;

    /**
     * Partner - odběratel (pro vydané) nebo dodavatel (pro přijaté)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    /**
     * Datum vystavení faktury
     */
    @Column(nullable = false)
    private LocalDate issueDate;

    /**
     * Datum zdanitelného plnění (DUZP)
     */
    @Column(nullable = false)
    private LocalDate taxableSupplyDate;

    /**
     * Datum splatnosti
     */
    @Column(nullable = false)
    private LocalDate dueDate;

    /**
     * Stav faktury
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvoiceStatus status;

    /**
     * Celková částka bez DPH
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmountWithoutVat;

    /**
     * Celková částka DPH
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalVatAmount;

    /**
     * Celková částka včetně DPH
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmountWithVat;

    /**
     * Poznámka k faktuře
     */
    @Column(length = 1000)
    private String note;

    /**
     * Variabilní symbol
     */
    @Column(length = 20)
    private String variableSymbol;

    /**
     * Konstantní symbol
     */
    @Column(length = 20)
    private String constantSymbol;

    /**
     * Specifický symbol
     */
    @Column(length = 20)
    private String specificSymbol;

    /**
     * Položky faktury
     */
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InvoiceItem> items = new ArrayList<>();

    /**
     * Pomocná metoda pro přidání položky faktury
     */
    public void addItem(InvoiceItem item) {
        items.add(item);
        item.setInvoice(this);
    }

    /**
     * Pomocná metoda pro odebrání položky faktury
     */
    public void removeItem(InvoiceItem item) {
        items.remove(item);
        item.setInvoice(null);
    }

    /**
     * Přepočítá celkové částky faktury na základě položek
     */
    public void recalculateTotals() {
        totalAmountWithoutVat = items.stream()
                .map(InvoiceItem::getTotalWithoutVat)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalVatAmount = items.stream()
                .map(InvoiceItem::getVatAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalAmountWithVat = items.stream()
                .map(InvoiceItem::getTotalWithVat)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
