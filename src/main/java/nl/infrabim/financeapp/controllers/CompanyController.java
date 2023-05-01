package nl.infrabim.financeapp.controllers;

import nl.infrabim.financeapp.models.Company;
import nl.infrabim.financeapp.repositories.CompanyRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class CompanyController {

    private final CompanyRepository companyRepository;

    public CompanyController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @QueryMapping
    public List<Company> companies() {
        return companyRepository.listCompanies();
    }

    @QueryMapping
    public Company company(@Argument String companyName) {
        return companyRepository.getOneCompany(companyName);
    }
}
