package cz.osu.economyosu.entity;

/**
 * Typ faktury - vydaná nebo přijatá.
 */
public enum InvoiceType {
    /** Vydaná faktura - my fakturujeme zákazníkovi */
    ISSUED,

    /** Přijatá faktura - dodavatel fakturuje nám */
    RECEIVED
}
