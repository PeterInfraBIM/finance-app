package nl.infrabim.financeapp.models;

import java.util.List;

public record Tag(String name, int transactionsCount, float totalAmount, List<Company> companies) {
}
