package cz.osu.economyosu.entity;

/**
 * Sazby DPH platné v ČR.
 */
public enum VatRate {
    /** Základní sazba DPH 21% */
    STANDARD(21),

    /** Snížená sazba DPH 12% */
    REDUCED_FIRST(12),

    /** Druhá snížená sazba DPH 0% */
    REDUCED_SECOND(0),

    /** Bez DPH - neplátce, osvobozené plnění */
    ZERO(0);

    private final int rate;

    VatRate(int rate) {
        this.rate = rate;
    }

    public int getRate() {
        return rate;
    }

    public double getMultiplier() {
        return 1 + (rate / 100.0);
    }
}
