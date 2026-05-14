package cz.osu.economyosu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Entita reprezentující položku faktury.
 */
@Entity
@Table(name = "invoice_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Faktura, ke které položka patří
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    /**
     * Popis položky / název položky
     */
    @Column(nullable = false, length = 500)
    private String description;

    /**
     * Množství
     */
    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantity;

    /**
     * Jednotka (ks, hod, m, kg, atd.)
     */
    @Column(length = 20)
    @Builder.Default
    private String unit = "ks";

    /**
     * Cena za jednotku bez DPH
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    /**
     * Sazba DPH
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VatRate vatRate;

    /**
     * Pořadí položky na faktuře
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer orderIndex = 0;

    /**
     * Vypočítá celkovou cenu bez DPH (množství * jednotková cena)
     */
    public BigDecimal getTotalWithoutVat() {
        return quantity.multiply(unitPrice)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Vypočítá částku DPH
     */
    public BigDecimal getVatAmount() {
        BigDecimal totalWithoutVat = getTotalWithoutVat();
        BigDecimal vatRateDecimal = BigDecimal.valueOf(vatRate.getRate())
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return totalWithoutVat.multiply(vatRateDecimal)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Vypočítá celkovou cenu včetně DPH
     */
    public BigDecimal getTotalWithVat() {
        return getTotalWithoutVat().add(getVatAmount())
                .setScale(2, RoundingMode.HALF_UP);
    }
}
