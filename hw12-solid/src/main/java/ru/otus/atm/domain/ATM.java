package ru.otus.atm.domain;

import ru.otus.atm.exceptions.WithdrawalException;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * ATM class implements ATMOperations.
 * It aggregates multiple CashCell objects, each for a specific banknote denomination.
 */
public class ATM implements ATMOperations {

    // TreeMap to store banknotes (key: denomination, value: CashCell), sorted in descending order
    private final Map<Integer, CashCell> cashCells = new TreeMap<>(Comparator.reverseOrder());

    /**
     * Deposits banknotes of a given denomination into the ATM
     *
     * @param denomination the banknote denomination
     * @param count        the number of banknotes to deposit
     */
    @Override
    public void deposit(int denomination, int count) {
        if(cashCells.containsKey(denomination)) {
            cashCells.get(denomination).deposit(count);
        } else {
            cashCells.put(denomination, new CashCell(denomination, count));
        }
    }

    /**
     * Withdraws the requested amount from the ATM using available banknotes
     *
     * @param amount the amount to withdraw
     * @return a map of denomination to the count of banknotes dispensed
     * @throws WithdrawalException if the requested amount cannot be dispensed
     */
    @Override
    public Map<Integer, Integer> withdraw(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        int remaining = amount;
        Map<Integer, Integer> withdrawalPlan = new HashMap<>();

        // Build the withdrawal plan using available denominations in descending order
        for (CashCell cell : cashCells.values()) {
            int denom = cell.getDenomination();
            int available = cell.getCount();
            int needed = remaining / denom;
            if (needed > 0) {
                int used = Math.min(needed, available);
                if (used > 0) {
                    withdrawalPlan.put(denom, used);
                    remaining -= used * denom;
                }
            }
        }

        if (remaining != 0) {
            throw new WithdrawalException("Cannot dispense the requested amount: " + amount);
        }

        // Update the cash cells by deducting the dispensed banknotes
        for (var entry : withdrawalPlan.entrySet()) {
            int denom = entry.getKey();
            int used = entry.getValue();
            cashCells.get(denom).withdraw(used);
        }
        return withdrawalPlan;
    }

    /**
     * Returns the total amount of cash in the ATM
     *
     * @return the total cash amount
     */
    @Override
    public int getBalance() {
        return cashCells.values().stream()
                .mapToInt(CashCell::getTotal)
                .sum();
    }

    /**
     * Checks if the ATM can dispense the requested amount without modifying its state
     *
     * @param amount the amount to check
     * @return true if the amount can be dispensed, false otherwise
     */
    public boolean canWithdraw(int amount) {
        // If the requested amount is invalid (e.g., negative or zero), return false
        if (amount <= 0) {
            return false;
        }

        // Create a copy of the current state of cash cells (denominations and their counts)
        Map<Integer, Integer> tempCells = new TreeMap<>(Comparator.reverseOrder());
        for (var entry : cashCells.entrySet()) {
            tempCells.put(entry.getKey(), entry.getValue().getCount());
        }

        // Remaining amount to calculate
        int remaining = amount;

        // Iterate through denominations in descending order
        for (var entry : tempCells.entrySet()) {
            int denomination = entry.getKey();    // Current denomination (e.g., 100)
            int available = entry.getValue();    // Number of banknotes available for this denomination

            // Calculate how many banknotes of this denomination are needed
            int needed = remaining / denomination;

            // Use as many banknotes as possible, but no more than what is available
            int used = Math.min(needed, available);

            // Deduct the used banknotes from the remaining amount
            remaining -= used * denomination;

            // If the remaining amount is zero, the withdrawal is possible
            if (remaining == 0) {
                return true;
            }
        }

        // If the loop completes but there is still a remaining amount, withdrawal is not possible
        return false;
    }

}
