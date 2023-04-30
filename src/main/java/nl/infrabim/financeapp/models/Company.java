package nl.infrabim.financeapp.models;

import nl.infrabim.financeapp.csv.Mutation;

import java.time.LocalDate;
import java.util.List;

public record Company(String name, String account, int transactionsCount, float totalAmount,
                      List<Transaction> transactions, String tag) {
}
