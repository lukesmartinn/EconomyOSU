package cz.osu.economyosu.repository;

import cz.osu.economyosu.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

    /**
     * Najde všechny položky dané faktury
     */
    @Query("SELECT i FROM InvoiceItem i WHERE i.invoice.id = :invoiceId ORDER BY i.orderIndex ASC")
    List<InvoiceItem> findByInvoiceId(@Param("invoiceId") Long invoiceId);

    /**
     * Smaže všechny položky dané faktury
     */
    void deleteByInvoiceId(Long invoiceId);
}
