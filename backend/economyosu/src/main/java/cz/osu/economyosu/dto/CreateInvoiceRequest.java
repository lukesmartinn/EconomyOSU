package cz.osu.economyosu.dto;

import cz.osu.economyosu.entity.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO pro vytvoření nové faktury.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceRequest {

    @NotBlank(message = "Číslo faktury je povinné")
    @Size(max = 50, message = "Číslo faktury může mít maximálně 50 znaků")
    private String invoiceNumber;

    @NotNull(message = "Typ faktury je povinný")
    private InvoiceType type;

    @NotNull(message = "Partner je povinný")
    private Long partnerId;

    @NotNull(message = "Datum vystavení je povinné")
    private LocalDate issueDate;

    @NotNull(message = "Datum zdanitelného plnění je povinné")
    private LocalDate taxableSupplyDate;

    @NotNull(message = "Datum splatnosti je povinné")
    private LocalDate dueDate;

    @Size(max = 1000, message = "Poznámka může mít maximálně 1000 znaků")
    private String note;

    @Size(max = 20, message = "Variabilní symbol může mít maximálně 20 znaků")
    private String variableSymbol;

    @Size(max = 20, message = "Konstantní symbol může mít maximálně 20 znaků")
    private String constantSymbol;

    @Size(max = 20, message = "Specifický symbol může mít maximálně 20 znaků")
    private String specificSymbol;

    @NotEmpty(message = "Faktura musí obsahovat alespoň jednu položku")
    private List<InvoiceItemDto> items;
}
