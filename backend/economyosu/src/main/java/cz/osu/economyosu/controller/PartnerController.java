package cz.osu.economyosu.controller;

import cz.osu.economyosu.dto.AresCompanyDto;
import cz.osu.economyosu.dto.PartnerDto;
import cz.osu.economyosu.service.AresService;
import cz.osu.economyosu.service.PartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/partners")
@RequiredArgsConstructor
@Slf4j
public class PartnerController {

    private final PartnerService partnerService;
    private final AresService aresService;

    // -----------------------------------------------------------------------
    // ARES lookup – musí být PŘED /{id}, jinak Spring zkusí parsovat
    // "ares" jako Long a vyhodí NumberFormatException (400 místo 200)
    // -----------------------------------------------------------------------

    /**
     * Vrátí předvyplněná data z ARES pro daný IČO, aniž by partnera uložil.
     * Vrací pouze pole potřebná pro formulář: companyName, ico, dic, address.
     *
     * GET /api/partners/ares?ico=27082440
     */
    @GetMapping("/ares")
    public ResponseEntity<?> getPartnerDataFromAres(@RequestParam String ico) {
        try {
            AresCompanyDto aresData = aresService.getCompanyByIco(ico);

            // Vrátíme jen to, co frontend potřebuje k předvyplnění formuláře.
            // Nepoužíváme PartnerDto (nemá legalForm/registrationDate),
            // ale jednoduchý Map – nebo můžeme vrátit celý AresCompanyDto.
            return ResponseEntity.ok(Map.of(
                    "companyName",      nullToEmpty(aresData.getCompanyName()),
                    "ico",              nullToEmpty(ico),
                    "dic",              nullToEmpty(aresData.getDic()),
                    "address",          nullToEmpty(aresData.getAddress()),
                    "legalForm",        nullToEmpty(aresData.getLegalForm()),
                    "registrationDate", nullToEmpty(aresData.getRegistrationDate())
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Neplatné IČO {}: {}", ico, e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Chyba při získávání dat z ARES pro IČO {}: {}", ico, e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Vyhledá partnera podle IČO v lokální DB.
     * GET /api/partners/ico/{ico}
     */
    @GetMapping("/ico/{ico}")
    public ResponseEntity<PartnerDto> getPartnerByIco(@PathVariable String ico) {
        try {
            PartnerDto partner = partnerService.getPartnerByIco(ico);
            return ResponseEntity.ok(partner);
        } catch (Exception e) {
            log.error("Partner s IČO {} nenalezen", ico);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Vytvoří nového partnera s ověřením v ARES a uložením do DB.
     * POST /api/partners/ares-verify
     */
    @PostMapping("/ares-verify")
    public ResponseEntity<?> createPartnerWithAresVerification(
            @RequestParam String ico,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone) {
        try {
            PartnerDto partner = partnerService.createPartnerWithAresVerification(ico, email, phone);
            return new ResponseEntity<>(partner, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Chyba při vytváření partnera s ARES: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Vytvoří nového partnera s manuálně zadanými údaji.
     * POST /api/partners
     */
    @PostMapping
    public ResponseEntity<?> createPartner(@RequestBody PartnerDto partnerDto) {
        try {
            PartnerDto partner = partnerService.createPartner(partnerDto);
            return new ResponseEntity<>(partner, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Chyba při vytváření partnera: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Vyhledá partnery podle názvu firmy.
     * GET /api/partners/search?name={name}
     */
    @GetMapping("/search")
    public ResponseEntity<List<PartnerDto>> searchPartnersByName(@RequestParam String name) {
        try {
            List<PartnerDto> partners = partnerService.searchPartnersByName(name);
            return ResponseEntity.ok(partners);
        } catch (Exception e) {
            log.error("Chyba při vyhledávání partnerů: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Vrátí všechny partnery.
     * GET /api/partners
     */
    @GetMapping
    public ResponseEntity<List<PartnerDto>> getAllPartners() {
        try {
            List<PartnerDto> partners = partnerService.getAllPartners();
            return ResponseEntity.ok(partners);
        } catch (Exception e) {
            log.error("Chyba při načítání partnerů: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Vrátí partnera podle ID.
     * GET /api/partners/{id}
     *
     * POZOR: Tento endpoint MUSÍ být definován AŽ PO /ares, /ico/{ico} a /search,
     * aby Spring MVC přednostně matchoval konkrétní cesty před path variable {id}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PartnerDto> getPartnerById(@PathVariable Long id) {
        try {
            // Opraveno: přímý dotaz do DB místo načítání všech partnerů
            PartnerDto partner = partnerService.getPartnerById(id);
            return ResponseEntity.ok(partner);
        } catch (Exception e) {
            log.error("Partner s ID {} nenalezen", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Aktualizuje partnera.
     * PUT /api/partners/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePartner(
            @PathVariable Long id,
            @RequestBody PartnerDto partnerDto) {
        try {
            PartnerDto updatedPartner = partnerService.updatePartner(id, partnerDto);
            return ResponseEntity.ok(updatedPartner);
        } catch (Exception e) {
            log.error("Chyba při aktualizaci partnera: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Odstraní partnera.
     * DELETE /api/partners/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePartner(@PathVariable Long id) {
        try {
            partnerService.deletePartner(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Chyba při odstraňování partnera: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /** Zabrání React controlled/uncontrolled varování při null hodnotách z ARES */
    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }
}
