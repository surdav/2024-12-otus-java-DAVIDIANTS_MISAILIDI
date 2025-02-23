package ru.otus.atm.domain;

import java.util.Map;
import ru.otus.atm.exceptions.WithdrawalException;

/**
 * ATMOperations defines basic operations for the ATM
 */
public interface ATMOperations {
    /**
     * Deposits banknotes into the ATM.
     *
     * @param denomination the banknote denomination
     * @param count the number of banknotes to deposit
     */
    void deposit(int denomination, int count);

    /**
     * Withdraws the requested amount from the ATM.
     *
     * @param amount the amount to withdraw
     * @return a map of banknote denomination to the number of banknotes dispensed
     * @throws WithdrawalException if the requested amount cannot be dispensed
     */
    Map<Integer, Integer> withdraw(int amount);

    /**
     * Returns the total amount of cash in the ATM.
     *
     * @return the ATM balance
     */
    int getBalance();
}
