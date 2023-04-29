package nl.infrabim.financeapp.controllers;

import nl.infrabim.financeapp.models.Company;
import nl.infrabim.financeapp.models.Tag;
import nl.infrabim.financeapp.repositories.CompanyRepository;
import nl.infrabim.financeapp.repositories.TagRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class TagController {

    private final TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @QueryMapping
    public List<Tag> tags() {
        return tagRepository.listTags();
    }

    @QueryMapping
    public List<Company> tagCompanies(@Argument String tagName) {
        return tagRepository.listTagCompanies(tagName);
    }
}
