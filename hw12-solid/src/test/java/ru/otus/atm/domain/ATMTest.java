package ru.otus.atm.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import ru.otus.atm.exceptions.WithdrawalException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ATMTest {

    private ATM atm;

    @BeforeEach
    void setUp() {
        atm = new ATM();
    }

    @Test
    void initialBalanceShouldBeZero() {
        // Arrange & Act: create a new ATM and get its balance
        int balance = atm.getBalance();

        // Assert: The balance should be zero
        assertThat(balance).isZero();
    }

    @Test
    void depositShouldIncreaseBalance() {
        // Arrange: deposit banknotes
        atm.deposit(100, 2);  // 200
        atm.deposit(50, 3);   // 150, total = 350

        // Act & Assert: check that the balance is as expected
        assertThat(atm.getBalance()).isEqualTo(350);
    }

    @Test
    void withdrawShouldDispenseCorrectBanknotes() {
        // Arrange: deposit banknotes into ATM
        atm.deposit(100, 2); // 200
        atm.deposit(50, 4);  // 200
        atm.deposit(20, 5);  // 100, total = 500

        // Act: withdraw 170, expected: 1x100, 1x50, 1x20 = 170
        Map<Integer, Integer> dispensed = atm.withdraw(170);

        // Assert: verify the withdrawal plan
        assertThat(dispensed)
                .containsEntry(100, 1)
                .containsEntry(50, 1)
                .containsEntry(20, 1);

        // And the new balance should be 500 - 170 = 330
        assertThat(atm.getBalance()).isEqualTo(330);
    }

    @Test
    void withdrawShouldThrowException_WhenInsufficientFunds() {
        // Arrange: deposit only 100
        atm.deposit(100, 1); // Total = 100

        // Act & Assert: attempt to withdraw 150, expect WithdrawalException
        assertThatThrownBy(() -> atm.withdraw(150))
                .isInstanceOf(WithdrawalException.class)
                .hasMessageContaining("Cannot dispense");
    }

    @Test
    void canWithdrawShouldReturnTrue_WhenWithdrawalIsPossible() {
        // Arrange: deposit banknotes
        atm.deposit(100, 2);
        atm.deposit(50, 2); // Total = 300

        // Act & Assert: 150 is possible (1x100 + 1x50)
        assertThat(atm.canWithdraw(150)).isTrue();
    }

    @Test
    void canWithdrawShouldReturnFalse_WhenWithdrawalIsNotPossible() {
        // Arrange: deposit banknotes that cannot form the requested amount
        atm.deposit(100, 2);
        atm.deposit(50, 2); // Total = 300

        // For example, 80 is impossible with only 100 and 50 banknotes.
        assertThat(atm.canWithdraw(80)).isFalse();
    }

    @Test
    void withdrawShouldThrowException_WhenAmountIsNegative() {
        // Act & Assert: negative withdrawal should throw IllegalArgumentException
        assertThatThrownBy(() -> atm.withdraw(-50))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be positive");
    }
}