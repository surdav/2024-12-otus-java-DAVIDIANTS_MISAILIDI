package ru.otus.atm.domain;

/**
 * CashCell represents a container that holds banknotes of a specific denomination
 */
public class CashCell {
    private final int denomination;
    private int count;

    public CashCell(int denomination, int initialCount) {
        if (denomination <= 0 || initialCount < 0) {
            throw new IllegalArgumentException("Invalid denomination or count");
        }
        this.denomination = denomination;
        this.count = initialCount;
    }

    /**
     * Deposits additional banknotes into this cell
     *
     * @param additional the number of banknotes to add
     */
    public void deposit(int additional) {
        if (additional <= 0) {
            throw new IllegalArgumentException("Additional banknotes must be positive");
        }
        count += additional;
    }

    /**
     * Withdraws banknotes from this cell
     *
     * @param number the number of banknotes to withdraw
     * @return true if withdrawal is successful, false otherwise
     */
    public boolean withdraw(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Number of banknotes must be positive");
        }
        if (number > count) {
            return false;
        }
        count -= number;
        return true;
    }

    /**
     * Returns the total amount stored in this cell
     *
     * @return the total cash in this cell
     */
    public int getTotal() {
        return denomination * count;
    }

    public int getDenomination() {
        return denomination;
    }

    public int getCount() {
        return count;
    }
}
