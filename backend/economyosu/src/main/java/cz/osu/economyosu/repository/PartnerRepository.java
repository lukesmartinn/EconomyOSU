package cz.osu.economyosu.repository;

import cz.osu.economyosu.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartnerRepository extends JpaRepository<Partner, Long> {

    Optional<Partner> findByIco(String ico);

    boolean existsByIco(String ico);

    List<Partner> findByCompanyNameContainingIgnoreCase(String companyName);
}