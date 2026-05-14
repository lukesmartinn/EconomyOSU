package cz.osu.economyosu.entity;

/**
 * Stavy faktury v životním cyklu.
 */
public enum InvoiceStatus {
    /** Návrh faktury - ještě nebyla odeslána */
    DRAFT,

    /** Odeslána partnerovi - čeká na úhradu */
    SENT,

    /** Zaplacená */
    PAID,

    /** Po splatnosti - neuhrazená */
    OVERDUE,

    /** Zrušená */
    CANCELLED,

    /** Částečně uhrazená */
    PARTIALLY_PAID
}
