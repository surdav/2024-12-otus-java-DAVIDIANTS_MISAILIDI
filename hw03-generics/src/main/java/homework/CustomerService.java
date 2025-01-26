package homework;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class CustomerService {

    // важно подобрать подходящую Map-у, посмотрите на редко используемые методы, они тут полезны
    private final TreeMap<Customer, String> customers = new TreeMap<>(Comparator.comparingLong(Customer::getScores));

    public Map.Entry<Customer, String> getSmallest() {
        // Возможно, чтобы реализовать этот метод, потребуется посмотреть как Map.Entry сделан в jdk
        Map.Entry<Customer, String> entry = customers.firstEntry();

        if (entry == null) {return null;}

        // Возвращаем копию ключа (Customer) и старое значение
        return Map.entry(new Customer(entry.getKey()), entry.getValue());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {

        Map.Entry<Customer, String> entry = customers.higherEntry(customer);

        if (entry == null) {return null;}

        // Возвращаем копию ключа (Customer) и старое значение
        return Map.entry(new Customer(entry.getKey()), entry.getValue());
    }

    public void add(Customer customer, String data) {
        customers.put(customer, data);
    }
}
