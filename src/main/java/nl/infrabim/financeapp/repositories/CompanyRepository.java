package nl.infrabim.financeapp.repositories;

import com.fasterxml.jackson.databind.JsonNode;
import nl.infrabim.financeapp.models.Company;
import nl.infrabim.financeapp.services.FusekiService;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CompanyRepository {

    private final FusekiService fusekiService;

    public CompanyRepository(FusekiService fusekiService) {
        this.fusekiService = fusekiService;
    }

    public List<Company> listCompanies() {
        try {
            List<Company> companyList = new ArrayList<>();
            final JsonNode companies = getCompanies();
            if (companies.size() > 0) {
                for (JsonNode node : companies) {
                    String name = null;
                    String counterAccount = null;
                    int transactionsCount = 0;
                    float totalAmount = 0.0f;
                    String tag = null;
                    String category = null;
                    JsonNode nameNode = node.get("name");
                    if (nameNode != null) {
                        name = nameNode.get("value").asText();
                    }
                    JsonNode counterAccountNode = node.get("counterAccount");
                    if (counterAccountNode != null) {
                        counterAccount = counterAccountNode.get("value").asText();
                    }
                    JsonNode tagNode = node.get("tag");
                    if (tagNode != null) {
                        tag = tagNode.get("value").asText();
                    }
                    JsonNode categoryNode = node.get("category");
                    if (categoryNode != null) {
                        category = categoryNode.get("value").asText();
                    }
                    JsonNode companyTransactionsAggregates = getCompanyTransactionsAggregates(name);
                    if (companyTransactionsAggregates != null) {
                        JsonNode countNode = companyTransactionsAggregates.get(0).get("transactionsCount");
                        transactionsCount = Integer.parseInt(countNode.get("value").asText());
                        JsonNode amountNode = companyTransactionsAggregates.get(0).get("totalAmount");
                        totalAmount = Float.parseFloat(amountNode.get("value").asText());
                    }

                    companyList.add(new Company(name, counterAccount, transactionsCount, totalAmount, tag));
                }
            }
            return companyList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonNode getCompanies() throws IOException {
        String queryString = """
                PREFIX : <http://infrabim.nl/finance#>
                PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
                PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                                
                SELECT DISTINCT ?name ?counterAccount ?tag ?category
                WHERE {
                    ?t rdf:type rdfs:Resource ;
                        :name ?name .
                    OPTIONAL { ?t :counterAccount ?counterAccount . }
                    OPTIONAL {
                     ?c rdf:type :Company ;
                         rdfs:label ?name ;
                         :category ?category .
                     ?category rdfs:label ?tag .
                     FILTER(LANG(?tag) = "nl" ) }
                }
                ORDER BY ASC(UCASE(str(?name)))
                """;

        return fusekiService.sendQuery(queryString);
    }

    public JsonNode getCompanyTransactionsAggregates(String companyName) throws IOException {
        String queryString = """
            PREFIX : <http://infrabim.nl/finance#>
            PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                            
               SELECT (COUNT(?description) AS ?transactionsCount) (SUM(?amount) as ?totalAmount)
               WHERE {
                ?s :name
            """
            + "\"" + companyName + "\"" + """
            ;
                :description ?description ;
                :amount ?amount .
            }
            """;
        return fusekiService.sendQuery(queryString);
    }

}
