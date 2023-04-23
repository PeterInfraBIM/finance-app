package nl.infrabim.financeapp.repositories;

import com.fasterxml.jackson.databind.JsonNode;
import nl.infrabim.financeapp.csv.Mutation;
import nl.infrabim.financeapp.models.Transaction;
import nl.infrabim.financeapp.services.FusekiService;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionRepository {

    private final FusekiService fusekiService;

    public TransactionRepository(FusekiService fusekiService) {
        this.fusekiService = fusekiService;
    }

    public List<Transaction> listTransActions() {
        try {
            List<Transaction> transactionList = new ArrayList<>();
            final JsonNode transactions = getTransactions();
            if (transactions.size() > 0) {
                for (JsonNode node : transactions) {
                    transactionList.add(composeTransaction(node, null));
                }
            }
            return transactionList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Transaction> listCompanyTransactions(String companyName) {
        try {
            List<Transaction> transactionList = new ArrayList<>();
            final JsonNode transactions = getCompanyTransactions(companyName);
            if (transactions.size() > 0) {
                for (JsonNode node : transactions) {
                    Transaction transaction = composeTransaction(node, companyName);
                    transactionList.add(transaction);
                }
            }
            return transactionList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Transaction> listDateTransactions(LocalDate date) {
        try {
            List<Transaction> transactionList = new ArrayList<>();
            final JsonNode transactions = getDateTransactions(date);
            if (transactions.size() > 0) {
                for (JsonNode node : transactions) {
                    transactionList.add(composeTransaction(node, null));
                }
            }
            return transactionList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Transaction composeTransaction(JsonNode node, String companyName) {
        LocalDate date = null;
        String name = companyName;
        String account = null;
        String counterAccount = null;
        String description = null;
        Float amount = null;
        Boolean credit = null;
        String mutationType = null;
        Mutation mutationCode = null;
        String tag = null;
        JsonNode dateNode = node.get("date");
        if (dateNode != null) {
            date = LocalDate.parse(dateNode.get("value").asText());
        }
        JsonNode nameNode = node.get("name");
        if (nameNode != null) {
            name = nameNode.get("value").asText();
        }
        JsonNode accountNode = node.get("account");
        if (accountNode != null) {
            account = accountNode.get("value").asText();
        }
        JsonNode counterAccountNode = node.get("counterAccount");
        if (counterAccountNode != null) {
            counterAccount = counterAccountNode.get("value").asText();
        }
        JsonNode descrNode = node.get("description");
        if (descrNode != null) {
            description = descrNode.get("value").asText();
        }
        JsonNode amountNode = node.get("amount");
        if (amountNode != null) {
            double v = amountNode.get("value").asDouble();
            amount = (float) v;
        }
        JsonNode creditNode = node.get("credit");
        if (creditNode != null) {
            credit = creditNode.get("value").asBoolean();
        }
        JsonNode mutationTypeNode = node.get("mutationType");
        if (mutationTypeNode != null) {
            mutationType = mutationTypeNode.get("value").asText();
        }
        JsonNode mutationCodeNode = node.get("mutationCode");
        if (mutationCodeNode != null) {
            mutationCode = Mutation.valueOf(mutationCodeNode.get("value").asText());
        }
        JsonNode tagNode = node.get("tag");
        if (tagNode != null) {
            tag = tagNode.get("value").asText();
        }
        return new Transaction(date, name, account, counterAccount, description, amount, credit, mutationType, mutationCode, tag);
    }

    public JsonNode getTransactions() throws IOException {
        String queryString = """
                SELECT ?date ?name ?account ?counterAccount ?description ?date ?amount ?credit ?mutationType ?mutationCode ?tag
                WHERE {
                 ?s <http://infrabim.nl/finance#name> ?name ;
                  <http://infrabim.nl/finance#account> ?account ;
                  <http://infrabim.nl/finance#description> ?description ;
                  <http://infrabim.nl/finance#date> ?date ;
                  <http://infrabim.nl/finance#amount> ?amount ;
                  <http://infrabim.nl/finance#credit> ?credit ;
                  <http://infrabim.nl/finance#mutationType> ?mutationType ;
                  <http://infrabim.nl/finance#mutationCode> ?mutationCode .
                  OPTIONAL {
                    ?s <http://infrabim.nl/finance#counterAccount> ?counterAccount .
                  }
                  OPTIONAL {
                    ?s <http://infrabim.nl/finance#tag> ?tag .
                  }
                }
                ORDER BY ?date
                """;

        return fusekiService.sendQuery(queryString);
    }

    public JsonNode getCompanyTransactions(String companyName) throws IOException {
        String queryString = """
                PREFIX : <http://infrabim.nl/finance#>
                PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
                PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                
                SELECT ?date ?account ?counterAccount ?description ?amount ?credit ?mutationType ?mutationCode ?tag
                WHERE {
                 ?s :name """
                + "\"" + companyName + "\"" + """
                   ;
                  :account ?account ;
                  :description ?description ;
                  :date ?date ;
                  :amount ?amount ;
                  :credit ?credit ;
                  :mutationType ?mutationType ;
                  :mutationCode ?mutationCode .
                  OPTIONAL {
                    ?s :counterAccount ?counterAccount .
                  }
                  OPTIONAL {
                    ?c rdf:type :Company ;
                      rdfs:label """
                    + "\"" + companyName + "\"" + """
                       ;
                       :category ?category .
                    ?category rdfs:label ?tag .
                      FILTER(LANG(?tag) = "nl" )
                  }
                }
                ORDER BY ?date
                """;
        return fusekiService.sendQuery(queryString);
    }

    public JsonNode getDateTransactions(LocalDate date) throws IOException {
        String queryString = """
                PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>
                SELECT ?date ?name ?account ?counterAccount ?description ?amount ?credit ?mutationType ?mutationCode ?tag
                WHERE {
                 ?s <http://infrabim.nl/finance#name> ?name ;
                  <http://infrabim.nl/finance#account> ?account ;
                  <http://infrabim.nl/finance#description> ?description ;
                  <http://infrabim.nl/finance#date> ?date ;
                  <http://infrabim.nl/finance#amount> ?amount ;
                  <http://infrabim.nl/finance#credit> ?credit ;
                  <http://infrabim.nl/finance#mutationType> ?mutationType ;
                  <http://infrabim.nl/finance#mutationCode> ?mutationCode .
                  FILTER (?date = """
                + "\"" + date + "\"^^xsd:date) ." + """
                  OPTIONAL {
                    ?s <http://infrabim.nl/finance#counterAccount> ?counterAccount .
                  }
                  OPTIONAL {
                    ?s <http://infrabim.nl/finance#tag> ?tag .
                  }
                }
                ORDER BY ?name
                """;
        return fusekiService.sendQuery(queryString);
    }
}
