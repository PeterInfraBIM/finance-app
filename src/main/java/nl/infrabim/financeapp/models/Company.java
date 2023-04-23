package nl.infrabim.financeapp.models;

import nl.infrabim.financeapp.csv.Mutation;

import java.time.LocalDate;

public record Company(String name, String account, int transactionsCount, float totalAmount, String tag) {
}
