package cz.osu.economyosu.service;

import cz.osu.economyosu.dto.CreateInvoiceRequest;
import cz.osu.economyosu.dto.InvoiceDto;
import cz.osu.economyosu.dto.InvoiceItemDto;
import cz.osu.economyosu.entity.*;
import cz.osu.economyosu.repository.InvoiceItemRepository;
import cz.osu.economyosu.repository.InvoiceRepository;
import cz.osu.economyosu.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final PartnerRepository partnerRepository;

    /**
     * Vytvoří novou fakturu s položkami.
     */
    @Transactional
    public InvoiceDto createInvoice(CreateInvoiceRequest request) {
        // Validace partnera
        Partner partner = partnerRepository.findById(request.getPartnerId())
                .orElseThrow(() -> new RuntimeException("Partner s ID " + request.getPartnerId() + " nenalezen"));

        // Validace čísla faktury
        if (invoiceRepository.existsByInvoiceNumber(request.getInvoiceNumber())) {
            throw new RuntimeException("Faktura s číslem " + request.getInvoiceNumber() + " již existuje");
        }

        // Vytvoření faktury
        Invoice invoice = Invoice.builder()
                .invoiceNumber(request.getInvoiceNumber())
                .type(request.getType())
                .partner(partner)
                .issueDate(request.getIssueDate())
                .taxableSupplyDate(request.getTaxableSupplyDate())
                .dueDate(request.getDueDate())
                .status(InvoiceStatus.DRAFT)
                .note(request.getNote())
                .variableSymbol(request.getVariableSymbol())
                .constantSymbol(request.getConstantSymbol())
                .specificSymbol(request.getSpecificSymbol())
                .build();

        // Přidání položek
        int orderIndex = 0;
        for (InvoiceItemDto itemDto : request.getItems()) {
            InvoiceItem item = InvoiceItem.builder()
                    .description(itemDto.getDescription())
                    .quantity(itemDto.getQuantity())
                    .unit(itemDto.getUnit() != null ? itemDto.getUnit() : "ks")
                    .unitPrice(itemDto.getUnitPrice())
                    .vatRate(itemDto.getVatRate())
                    .orderIndex(orderIndex++)
                    .build();
            invoice.addItem(item);
        }

        // Přepočítání celkových částek
        invoice.recalculateTotals();

        // Uložení
        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Vytvořena faktura: {} typu {} pro partnera {}",
                savedInvoice.getInvoiceNumber(),
                savedInvoice.getType(),
                partner.getCompanyName());

        return mapToDto(savedInvoice);
    }

    /**
     * Aktualizuje existující fakturu.
     */
    @Transactional
    public InvoiceDto updateInvoice(Long id, CreateInvoiceRequest request) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faktura s ID " + id + " nenalezena"));

        // Validace partnera
        Partner partner = partnerRepository.findById(request.getPartnerId())
                .orElseThrow(() -> new RuntimeException("Partner s ID " + request.getPartnerId() + " nenalezen"));

        // Aktualizace základních údajů
        invoice.setInvoiceNumber(request.getInvoiceNumber());
        invoice.setType(request.getType());
        invoice.setPartner(partner);
        invoice.setIssueDate(request.getIssueDate());
        invoice.setTaxableSupplyDate(request.getTaxableSupplyDate());
        invoice.setDueDate(request.getDueDate());
        invoice.setNote(request.getNote());
        invoice.setVariableSymbol(request.getVariableSymbol());
        invoice.setConstantSymbol(request.getConstantSymbol());
        invoice.setSpecificSymbol(request.getSpecificSymbol());

        // Odstranění starých položek
        invoice.getItems().clear();

        // Přidání nových položek
        int orderIndex = 0;
        for (InvoiceItemDto itemDto : request.getItems()) {
            InvoiceItem item = InvoiceItem.builder()
                    .description(itemDto.getDescription())
                    .quantity(itemDto.getQuantity())
                    .unit(itemDto.getUnit() != null ? itemDto.getUnit() : "ks")
                    .unitPrice(itemDto.getUnitPrice())
                    .vatRate(itemDto.getVatRate())
                    .orderIndex(orderIndex++)
                    .build();
            invoice.addItem(item);
        }

        // Přepočítání celkových částek
        invoice.recalculateTotals();

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        log.info("Aktualizována faktura: {}", updatedInvoice.getInvoiceNumber());

        return mapToDto(updatedInvoice);
    }

    /**
     * Změní stav faktury.
     */
    @Transactional
    public InvoiceDto changeInvoiceStatus(Long id, InvoiceStatus newStatus) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faktura s ID " + id + " nenalezena"));

        invoice.setStatus(newStatus);
        Invoice updatedInvoice = invoiceRepository.save(invoice);

        log.info("Změněn stav faktury {} na {}", invoice.getInvoiceNumber(), newStatus);

        return mapToDto(updatedInvoice);
    }

    /**
     * Vrátí fakturu podle ID.
     */
    @Transactional(readOnly = true)
    public InvoiceDto getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faktura s ID " + id + " nenalezena"));
        return mapToDto(invoice);
    }

    /**
     * Vrátí všechny faktury.
     */
    @Transactional(readOnly = true)
    public List<InvoiceDto> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Vrátí faktury podle typu.
     */
    @Transactional(readOnly = true)
    public List<InvoiceDto> getInvoicesByType(InvoiceType type) {
        return invoiceRepository.findByType(type).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Vrátí faktury podle stavu.
     */
    @Transactional(readOnly = true)
    public List<InvoiceDto> getInvoicesByStatus(InvoiceStatus status) {
        return invoiceRepository.findByStatus(status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Vrátí faktury po splatnosti.
     */
    @Transactional(readOnly = true)
    public List<InvoiceDto> getOverdueInvoices() {
        return invoiceRepository.findOverdueInvoices(LocalDate.now()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Smaže fakturu.
     */
    @Transactional
    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new RuntimeException("Faktura s ID " + id + " nenalezena");
        }
        invoiceRepository.deleteById(id);
        log.info("Faktura s ID {} byla smazána", id);
    }

    /**
     * Mapuje Invoice entitu na InvoiceDto.
     */
    private InvoiceDto mapToDto(Invoice invoice) {
        return InvoiceDto.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .type(invoice.getType())
                .partnerId(invoice.getPartner().getId())
                .partnerName(invoice.getPartner().getCompanyName())
                .partnerIco(invoice.getPartner().getIco())
                .issueDate(invoice.getIssueDate())
                .taxableSupplyDate(invoice.getTaxableSupplyDate())
                .dueDate(invoice.getDueDate())
                .status(invoice.getStatus())
                .totalAmountWithoutVat(invoice.getTotalAmountWithoutVat())
                .totalVatAmount(invoice.getTotalVatAmount())
                .totalAmountWithVat(invoice.getTotalAmountWithVat())
                .note(invoice.getNote())
                .variableSymbol(invoice.getVariableSymbol())
                .constantSymbol(invoice.getConstantSymbol())
                .specificSymbol(invoice.getSpecificSymbol())
                .items(invoice.getItems().stream()
                        .map(this::mapItemToDto)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Mapuje InvoiceItem entitu na InvoiceItemDto.
     */
    private InvoiceItemDto mapItemToDto(InvoiceItem item) {
        return InvoiceItemDto.builder()
                .id(item.getId())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unit(item.getUnit())
                .unitPrice(item.getUnitPrice())
                .vatRate(item.getVatRate())
                .orderIndex(item.getOrderIndex())
                .totalWithoutVat(item.getTotalWithoutVat())
                .vatAmount(item.getVatAmount())
                .totalWithVat(item.getTotalWithVat())
                .build();
    }
}
