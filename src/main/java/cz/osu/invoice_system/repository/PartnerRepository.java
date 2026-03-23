package cz.osu.invoice_system.repository;

import cz.osu.invoice_system.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {
    
    // Spring Data JPA díky názvu metody samo vygeneruje SQL dotaz na hledání podle IČO
    Optional<Partner> findByIco(String ico);
}
