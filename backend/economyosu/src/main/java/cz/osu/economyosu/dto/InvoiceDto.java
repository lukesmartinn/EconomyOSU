package cz.osu.economyosu.dto;

import cz.osu.economyosu.entity.InvoiceStatus;
import cz.osu.economyosu.entity.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO pro fakturu.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {

    private Long id;

    private String invoiceNumber;

    private InvoiceType type;

    private Long partnerId;

    private String partnerName;

    private String partnerIco;

    private LocalDate issueDate;

    private LocalDate taxableSupplyDate;

    private LocalDate dueDate;

    private InvoiceStatus status;

    private BigDecimal totalAmountWithoutVat;

    private BigDecimal totalVatAmount;

    private BigDecimal totalAmountWithVat;

    private String note;

    private String variableSymbol;

    private String constantSymbol;

    private String specificSymbol;

    private List<InvoiceItemDto> items;
}
