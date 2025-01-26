package homework;

import java.util.ArrayDeque;
import java.util.Deque;

public class CustomerReverseOrder {

    // Храним клиентов в виде стека
    private final Deque<Customer> stack = new ArrayDeque<>();

    // Метод добавления клиента в стек
    public void add(Customer customer) {
        stack.addLast(customer);
    }

    // Метод извлечения последнего добавленного клиента
    public Customer take() {
        return stack.pollLast(); // null, если стек пуст
    }
}