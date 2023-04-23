package nl.infrabim.financeapp.controllers;

import nl.infrabim.financeapp.models.Transaction;
import nl.infrabim.financeapp.repositories.TransactionRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;

@Controller
public class TransactionController {

    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @QueryMapping
    public List<Transaction> transactions() {
        return transactionRepository.listTransActions();
    }

    @QueryMapping
    public List<Transaction> companyTransactions(@Argument String companyName) {
        return transactionRepository.listCompanyTransactions(companyName);
    }

    @QueryMapping
    public List<Transaction> dateTransactions(@Argument LocalDate date) {
        return transactionRepository.listDateTransactions(date);
    }
}
