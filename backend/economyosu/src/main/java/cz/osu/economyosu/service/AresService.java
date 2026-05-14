package cz.osu.economyosu.service;

import cz.osu.economyosu.dto.AresCompanyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AresService {

    // Nové REST API (staré XML API na paos/xml bylo zrušeno v 2023)
    private static final String ARES_API_URL =
            "https://ares.gov.cz/ekonomicke-subjekty-v-be/rest/ekonomicke-subjekty/";

    private final RestTemplate restTemplate;

    /**
     * Vyhledá firmu/subjekt v ARES podle IČO.
     * Používá nové REST JSON API (ares.gov.cz/ekonomicke-subjekty-v-be/rest).
     *
     * @param ico IČO (8 číslic)
     * @return AresCompanyDto s údaji z ARES
     * @throws IllegalArgumentException pokud IČO není validní
     * @throws RuntimeException         pokud subjekt nebyl nalezen nebo komunikace selže
     */
    public AresCompanyDto getCompanyByIco(String ico) {
        if (ico == null || ico.trim().isEmpty()) {
            throw new IllegalArgumentException("IČO nesmí být prázdné");
        }

        String cleanIco = ico.replaceAll("\\s+", "").replaceAll("[^0-9]", "");

        if (cleanIco.length() != 8) {
            throw new IllegalArgumentException(
                    "IČO musí mít přesně 8 číslic, zadáno: " + cleanIco.length());
        }

        String url = ARES_API_URL + cleanIco;
        log.info("Volám ARES REST API: {}", url);

        try {
            // ARES vrací JSON – mapujeme do generické Map a pak ručně vytáhneme pole
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null) {
                throw new RuntimeException("Subjekt s IČO " + ico + " nebyl v ARES nalezen");
            }

            AresCompanyDto dto = mapResponseToDto(response, cleanIco);
            log.info("ARES subjekt nalezen: {}", dto.getCompanyName());
            return dto;

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Subjekt s IČO {} nenalezen v ARES (404)", cleanIco);
            throw new RuntimeException("Subjekt s IČO " + ico + " nebyl v ARES nalezen");
        } catch (RestClientException e) {
            log.error("Chyba při komunikaci s ARES API: {}", e.getMessage());
            throw new RuntimeException("Nemohu se připojit k ARES – zkuste to později", e);
        }
    }

    /**
     * Mapuje surovou JSON odpověď z ARES na AresCompanyDto.
     *
     * Struktura odpovědi ARES REST API (zkrácená):
     * {
     *   "ico": "27082440",
     *   "obchodniJmeno": "Firma s.r.o.",
     *   "dic": "CZ27082440",
     *   "pravniForma": "112",
     *   "datumVzniku": "2004-01-01",
     *   "sidlo": {
     *     "nazevUlice": "Příkladná",
     *     "cisloDomovni": 1,
     *     "cisloOrientacni": 2,
     *     "nazevObce": "Praha",
     *     "psc": "11000",
     *     "textovaAdresa": "Příkladná 1/2, 11000 Praha"
     *   }
     * }
     */
    @SuppressWarnings("unchecked")
    private AresCompanyDto mapResponseToDto(Map<String, Object> response, String cleanIco) {
        // Obchodní jméno
        String companyName = (String) response.get("obchodniJmeno");

        // DIČ
        String dic = (String) response.get("dic");

        // Právní forma (ARES vrací kód, např. "112" = s.r.o.)
        String legalForm = toString(response.get("pravniForma"));

        // Datum vzniku
        String registrationDate = toString(response.get("datumVzniku"));

        // Adresa – ze struktury "sidlo"
        String address = extractAddress(response);

        return AresCompanyDto.builder()
                .ico(cleanIco)
                .companyName(companyName)
                .dic(dic)
                .address(address)
                .legalForm(legalForm)
                .registrationDate(registrationDate)
                .build();
    }

    /**
     * Sestaví čitelnou adresu z objektu "sidlo" v odpovědi ARES.
     * Preferuje hotový textový řetězec "textovaAdresa", pokud není k dispozici,
     * složí adresu z jednotlivých polí.
     */
    @SuppressWarnings("unchecked")
    private String extractAddress(Map<String, Object> response) {
        Object sidloObj = response.get("sidlo");
        if (!(sidloObj instanceof Map)) {
            return null;
        }

        Map<String, Object> sidlo = (Map<String, Object>) sidloObj;

        // Pokud ARES rovnou poskytuje textovou adresu, použijeme ji
        String textovaAdresa = (String) sidlo.get("textovaAdresa");
        if (textovaAdresa != null && !textovaAdresa.isBlank()) {
            return textovaAdresa;
        }

        // Jinak sestavíme adresu ručně
        StringBuilder sb = new StringBuilder();

        String ulice = (String) sidlo.get("nazevUlice");
        Object cisloDomovni = sidlo.get("cisloDomovni");
        Object cisloOrientacni = sidlo.get("cisloOrientacni");

        if (ulice != null && !ulice.isBlank()) {
            sb.append(ulice);
            if (cisloDomovni != null) {
                sb.append(" ").append(cisloDomovni);
                if (cisloOrientacni != null) {
                    sb.append("/").append(cisloOrientacni);
                }
            }
        }

        Object psc = sidlo.get("psc");
        String obec = (String) sidlo.get("nazevObce");

        if (psc != null || obec != null) {
            if (!sb.isEmpty()) sb.append(", ");
            if (psc != null) sb.append(psc).append(" ");
            if (obec != null) sb.append(obec);
        }

        return sb.isEmpty() ? null : sb.toString().trim();
    }

    private String toString(Object value) {
        return value != null ? value.toString() : null;
    }
}