package cz.osu.economyosu.dto;

import cz.osu.economyosu.entity.VatRate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO pro položku faktury.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemDto {

    private Long id;

    private String description;

    private BigDecimal quantity;

    private String unit;

    private BigDecimal unitPrice;

    private VatRate vatRate;

    private Integer orderIndex;

    // Vypočítané hodnoty
    private BigDecimal totalWithoutVat;

    private BigDecimal vatAmount;

    private BigDecimal totalWithVat;
}
