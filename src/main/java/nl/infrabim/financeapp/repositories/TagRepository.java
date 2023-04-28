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

    public TagRepository(FusekiService fusekiService) {
        this.fusekiService = fusekiService;
    }

    public List<Tag> listTags() {
        try {
            List<Tag> tagList = new ArrayList<>();
            final JsonNode tags = getTags();
            if (tags.size() > 0) {
                for (JsonNode node : tags) {
                    String name = null;
                    JsonNode nameNode = node.get("name");
                    if (nameNode != null) {
                        name = nameNode.get("value").asText();
                    }
                    tagList.add(new Tag(name));
                }
            }
            return tagList;
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
}
