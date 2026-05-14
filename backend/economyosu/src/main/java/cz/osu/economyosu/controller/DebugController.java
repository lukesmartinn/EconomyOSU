package cz.osu.economyosu.controller;

import cz.osu.economyosu.dto.AresCompanyDto;
import cz.osu.economyosu.service.AresService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
@Slf4j
public class DebugController {

    private final RestTemplate restTemplate;
    private final AresService aresService;

    /**
     * Vrátí surovou JSON odpověď přímo z ARES REST API.
     * GET /api/debug/ares/raw?ico=27082440
     */
    @GetMapping("/ares/raw")
    public ResponseEntity<String> testAresRaw(@RequestParam String ico) {
        try {
            String cleanIco = ico.replaceAll("\\s+", "").replaceAll("[^0-9]", "");
            if (cleanIco.length() != 8) {
                return ResponseEntity.badRequest()
                        .body("IČO musí mít 8 číslic, zadáno: " + cleanIco.length());
            }

            String url = "https://ares.gov.cz/ekonomicke-subjekty-v-be/rest/ekonomicke-subjekty/"
                    + cleanIco;
            log.info("Volám ARES REST: {}", url);

            String response = restTemplate.getForObject(url, String.class);
            log.info("ARES vrátil {} znaků", response != null ? response.length() : 0);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Chyba při komunikaci s ARES", e);
            return ResponseEntity.status(500).body("Chyba: " + e.getMessage());
        }
    }

    /**
     * Vrátí parsovaná data z ARES (přes AresService).
     * GET /api/debug/ares?ico=27082440
     */
    @GetMapping("/ares")
    public ResponseEntity<?> testAresParsed(@RequestParam String ico) {
        try {
            AresCompanyDto dto = aresService.getCompanyByIco(ico);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("Chyba při získání dat z ARES", e);
            return ResponseEntity.status(500).body("Chyba: " + e.getMessage());
        }
    }

    /**
     * Health check.
     * GET /api/debug/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Debug server běží – ARES REST API aktivní");
    }
}