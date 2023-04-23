package nl.infrabim.financeapp.models;

import nl.infrabim.financeapp.csv.Mutation;

import java.time.LocalDate;

public record Transaction(LocalDate date, String name, String account, String counterAccount, String description,
                          Float amount, Boolean credit, String mutationType, Mutation mutationCode, String tag) {
}
