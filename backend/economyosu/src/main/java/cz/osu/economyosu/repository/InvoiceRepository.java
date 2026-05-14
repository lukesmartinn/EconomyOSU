package cz.osu.economyosu.repository;

import cz.osu.economyosu.entity.Invoice;
import cz.osu.economyosu.entity.InvoiceStatus;
import cz.osu.economyosu.entity.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /**
     * Najde fakturu podle čísla faktury
     */
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    /**
     * Zkontroluje, zda již existuje faktura s daným číslem
     */
    boolean existsByInvoiceNumber(String invoiceNumber);

    /**
     * Najde všechny faktury podle typu (vydané/přijaté)
     */
    List<Invoice> findByType(InvoiceType type);

    /**
     * Najde všechny faktury podle stavu
     */
    List<Invoice> findByStatus(InvoiceStatus status);

    /**
     * Najde všechny faktury partnera
     */
    @Query("SELECT i FROM Invoice i WHERE i.partner.id = :partnerId ORDER BY i.issueDate DESC")
    List<Invoice> findByPartnerId(@Param("partnerId") Long partnerId);

    /**
     * Najde faktury podle typu a stavu
     */
    List<Invoice> findByTypeAndStatus(InvoiceType type, InvoiceStatus status);

    /**
     * Najde faktury s datem splatnosti v rozmezí
     */
    @Query("SELECT i FROM Invoice i WHERE i.dueDate BETWEEN :startDate AND :endDate ORDER BY i.dueDate ASC")
    List<Invoice> findByDueDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Najde faktury po splatnosti
     */
    @Query("SELECT i FROM Invoice i WHERE i.status IN ('SENT', 'PARTIALLY_PAID') AND i.dueDate < :today ORDER BY i.dueDate ASC")
    List<Invoice> findOverdueInvoices(@Param("today") LocalDate today);

    /**
     * Spočítá celkový počet faktur podle typu
     */
    long countByType(InvoiceType type);

    /**
     * Spočítá celkový počet faktur podle typu a stavu
     */
    long countByTypeAndStatus(InvoiceType type, InvoiceStatus status);
}
