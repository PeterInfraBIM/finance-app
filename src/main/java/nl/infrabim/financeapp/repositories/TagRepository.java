package nl.infrabim.financeapp.repositories;

import com.fasterxml.jackson.databind.JsonNode;
import nl.infrabim.financeapp.models.Company;
import nl.infrabim.financeapp.models.Tag;
import nl.infrabim.financeapp.services.FusekiService;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TagRepository {
    private final FusekiService fusekiService;
    private final CompanyRepository companyRepository;

    public TagRepository(FusekiService fusekiService, CompanyRepository companyRepository) {
        this.fusekiService = fusekiService;
        this.companyRepository = companyRepository;
    }

    public List<Tag> listTags() {
        try {
            List<Tag> tagList = new ArrayList<>();
            final JsonNode tags = getTags();
            if (tags.size() > 0) {
                for (JsonNode node : tags) {
                    String tagName = null;
                    JsonNode nameNode = node.get("name");
                    if (nameNode != null) {
                        tagName = nameNode.get("value").asText();
                        int transactionsCount = 0;
                        float totalAmount = 0.0f;
                        List<Company> companyList = listTagCompanies(tagName);
                        for (Company company : companyList) {
                            transactionsCount += company.transactionsCount();
                            totalAmount += company.totalAmount();
                        }
                        tagList.add(new Tag(tagName, transactionsCount, totalAmount, companyList));
                    }
                }
            }
            return tagList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Company> listTagCompanies(String tagName) {
        try {
            List<Company> companyList = new ArrayList<>();
            final JsonNode tags = getTagCompanies(tagName);
            if (tags.size() > 0) {
                for (JsonNode node : tags) {
                    String companyName = null;
                    String companyAccount = null;
                    int transactionsCount = 0;
                    float totalAmount = 0.0f;
                    JsonNode nameNode = node.get("companyName");
                    JsonNode accountNode = node.get("companyAccount");
                    if (nameNode != null) {
                        companyName = nameNode.get("value").asText();
                        if (accountNode != null) {
                            companyAccount = accountNode.get("value").asText();
                        }
                        JsonNode companyTransactionsAggregates = companyRepository.getCompanyTransactionsAggregates(companyName);
                        if (companyTransactionsAggregates != null) {
                            JsonNode countNode = companyTransactionsAggregates.get(0).get("transactionsCount");
                            transactionsCount = Integer.parseInt(countNode.get("value").asText());
                            JsonNode amountNode = companyTransactionsAggregates.get(0).get("totalAmount");
                            totalAmount = Float.parseFloat(amountNode.get("value").asText());
                        }
                    }
                    companyList.add(new Company(companyName, companyAccount, transactionsCount, totalAmount, null));
                }
            }
            return companyList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonNode getTags() throws IOException {
        String queryString = """
                PREFIX : <http://infrabim.nl/finance#>
                PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
                PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                                
                SELECT DISTINCT ?name
                WHERE {
                    ?category rdf:type :Category ;
                        rdfs:label ?name .
                     FILTER(LANG(?name) = "nl" ) .
                }
                ORDER BY ASC(UCASE(str(?name)))
                """;

        return fusekiService.sendQuery(queryString);
    }

    public JsonNode getTagCompanies(String tagName) throws IOException {
        String queryString = """
                PREFIX : <http://infrabim.nl/finance#>
                PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
                PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                                
                SELECT DISTINCT ?companyName ?companyAccount
                WHERE {
                   ?company rdf:type :Company ;
                        rdfs:label ?companyName ;
                        :category ?category .
                   ?transaction rdf:type :Transaction ;
                        :name ?companyName .
                   OPTIONAL {
                        ?transaction :counterAccount ?companyAccount . }
                   ?category rdf:type :Category ;
                        rdfs:label
                """
                + " \"" + tagName + "\"@nl" + """
                .
                  }
                ORDER BY ASC(UCASE(str(?companyName)))
                """;

        return fusekiService.sendQuery(queryString);
    }

}
