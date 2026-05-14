package cz.osu.economyosu.service;

import cz.osu.economyosu.dto.AresCompanyDto;
import cz.osu.economyosu.dto.PartnerDto;
import cz.osu.economyosu.entity.Partner;
import cz.osu.economyosu.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final AresService aresService;

    /**
     * Vytvoří nového partnera s ověřením v ARES a uložením do DB.
     */
    public PartnerDto createPartnerWithAresVerification(String ico, String email, String phone) {
        if (partnerRepository.existsByIco(ico)) {
            throw new RuntimeException("Partner s IČO " + ico + " již existuje");
        }

        AresCompanyDto aresData = aresService.getCompanyByIco(ico);

        Partner partner = Partner.builder()
                .ico(ico)
                .companyName(aresData.getCompanyName())
                .dic(aresData.getDic())
                .address(aresData.getAddress())
                .email(email)
                .phone(phone)
                .build();

        Partner savedPartner = partnerRepository.save(partner);
        log.info("Partner vytvořen: {} (IČO: {})", savedPartner.getCompanyName(), savedPartner.getIco());
        return mapToDto(savedPartner);
    }

    /**
     * Vytvoří partnera s manuálně zadanými údaji.
     */
    public PartnerDto createPartner(PartnerDto partnerDto) {
        if (partnerRepository.existsByIco(partnerDto.getIco())) {
            throw new RuntimeException("Partner s IČO " + partnerDto.getIco() + " již existuje");
        }

        Partner partner = Partner.builder()
                .companyName(partnerDto.getCompanyName())
                .ico(partnerDto.getIco())
                .dic(partnerDto.getDic())
                .address(partnerDto.getAddress())
                .email(partnerDto.getEmail())
                .phone(partnerDto.getPhone())
                .build();

        Partner savedPartner = partnerRepository.save(partner);
        return mapToDto(savedPartner);
    }

    /**
     * Vyhledá partnera v DB podle ID.
     */
    public PartnerDto getPartnerById(Long id) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partner s ID " + id + " nenalezen"));
        return mapToDto(partner);
    }

    /**
     * Vyhledá partnera v DB podle IČO.
     */
    public PartnerDto getPartnerByIco(String ico) {
        Partner partner = partnerRepository.findByIco(ico)
                .orElseThrow(() -> new RuntimeException("Partner s IČO " + ico + " nenalezen"));
        return mapToDto(partner);
    }

    /**
     * Vyhledá partnery v DB podle názvu firmy (case-insensitive, částečná shoda).
     */
    public List<PartnerDto> searchPartnersByName(String companyName) {
        return partnerRepository.findByCompanyNameContainingIgnoreCase(companyName)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Aktualizuje existujícího partnera. IČO nelze měnit.
     */
    public PartnerDto updatePartner(Long id, PartnerDto partnerDto) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partner s ID " + id + " nenalezen"));

        partner.setCompanyName(partnerDto.getCompanyName());
        partner.setDic(partnerDto.getDic());
        partner.setAddress(partnerDto.getAddress());
        partner.setEmail(partnerDto.getEmail());
        partner.setPhone(partnerDto.getPhone());

        Partner updatedPartner = partnerRepository.save(partner);
        return mapToDto(updatedPartner);
    }

    /**
     * Odstraní partnera z DB.
     */
    public void deletePartner(Long id) {
        if (!partnerRepository.existsById(id)) {
            throw new RuntimeException("Partner s ID " + id + " nenalezen");
        }
        partnerRepository.deleteById(id);
        log.info("Partner s ID {} byl odstraněn", id);
    }

    /**
     * Vrátí všechny partnery z DB.
     */
    public List<PartnerDto> getAllPartners() {
        return partnerRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private PartnerDto mapToDto(Partner partner) {
        return PartnerDto.builder()
                .id(partner.getId())
                .companyName(partner.getCompanyName())
                .ico(partner.getIco())
                .dic(partner.getDic())
                .address(partner.getAddress())
                .email(partner.getEmail())
                .phone(partner.getPhone())
                .build();
    }
}
