package cz.osu.economyosu.dto;

import lombok.*;

/**
 * DTO pro data z ARES REST API (ares.gov.cz/ekonomicke-subjekty-v-be/rest).
 *
 * Jackson anotace (@JsonProperty) zde nejsou potřeba – mapování z JSON do Map<String, Object>
 * probíhá ručně v AresService.mapResponseToDto().
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AresCompanyDto {

    /** IČO subjektu (8 číslic) */
    private String ico;

    /** Obchodní jméno / název subjektu */
    private String companyName;

    /** DIČ (např. "CZ27082440"), může být null pokud není plátcem DPH */
    private String dic;

    /** Adresa sídla jako čitelný řetězec */
    private String address;

    /** Kód právní formy (např. "112" = s.r.o., "121" = a.s.) */
    private String legalForm;

    /** Datum vzniku subjektu (ISO formát "yyyy-MM-dd") */
    private String registrationDate;
}