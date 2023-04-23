package nl.infrabim.financeapp.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import nl.infrabim.financeapp.FinanceAppApplication;
import nl.infrabim.financeapp.csv.CreditDebit;
import nl.infrabim.financeapp.csv.Mutation;
import org.apache.jena.datatypes.xsd.impl.XSDDateType;
import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import nl.infrabim.financeapp.csv.Column;

@Service
public class FusekiService {
    public static final String QUERY_URL = "http://localhost:3330/rdf/query";

    private Model model;
    private FusekiServer server;


    public FusekiService() {
        init(FinanceAppApplication.csvFiles);
        startFuseki();
    }

    private void init(List<File> csvFiles) {
        try {
            model = ModelFactory.createOntologyModel();
            RDFDataMgr.read(model, "models/finance.ttl");
            for (File csvFile : csvFiles) {
                load(csvFile);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void startFuseki() {
        init(FinanceAppApplication.csvFiles);
        Dataset ds = DatasetFactory.create(model);
        server = FusekiServer.create()
                .add("/rdf", ds)
                .build();
        server.start();

        System.out.println("Port: " + server.getPort());
    }

    public void load(File csvFile) throws Exception {
        List<String[]> list = readAllLines(csvFile.toPath());
        boolean firstLine = true;
        for (String[] line : list) {
            if (firstLine) {
                firstLine = false;
                assert (line.length == Column.values().length);
                for (int col = 0; col < line.length; col++) {
                    assert (Column.values()[col].getName().equals(line[col]));
                    System.out.println(line[col]);
                }
                continue;
            }
            changeSignOfDebit(line);
            Resource transaction = model.createResource();
            final Statement statement = model.createStatement(transaction, RDF.type, RDFS.Resource);
            model.add(statement);
            for (Column column : Column.values()) {
                addPropertyValue(column, line, transaction);
            }
        }
    }

    private void changeSignOfDebit(String[] line) {
        if (line[Column.AfBij.ordinal()].equals(CreditDebit.Af.name())) {
            line[Column.Bedrag.ordinal()] = "-" + line[Column.Bedrag.ordinal()];
        }
    }

    private void addPropertyValue(Column column, String[] line, Resource transaction) {
        final Property property = column.getProperty(model);
        if (property != null) {
            Literal literal = null;
            final Object value = column.getValue(line[column.ordinal()]);
            if (value instanceof String) {
                literal = !value.equals("") ? model.createLiteral((String) value) : null;
            } else if (value instanceof Float) {
                Float number = (Float) value;
                literal = model.createTypedLiteral(number, XSDDateType.XSDfloat);
            } else if (value instanceof LocalDate) {
                LocalDate date = (LocalDate) value;
                literal = model.createTypedLiteral(date, XSDDateType.XSDdate);
            } else if (value instanceof Mutation) {
                Mutation mutation = (Mutation) value;
                literal = model.createLiteral(mutation.toString());
            } else if (value instanceof CreditDebit) {
                CreditDebit creditDebit = (CreditDebit) value;
                literal = model.createLiteral(creditDebit.toString());
            }
            if (literal != null) {
                model.add(transaction, property, literal);
            }
        }
    }

    private List<String[]> readAllLines(Path filePath) throws Exception {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(';')
                    .withIgnoreQuotations(true)
                    .build();
            try (CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withSkipLines(0)
                    .withCSVParser(parser)
                    .build()) {
                return csvReader.readAll();
            }
        }
    }

    public JsonNode sendQuery(String query) throws IOException {
        return sendQuery(getQueryConnection(), query);
    }

    private JsonNode sendQuery(HttpURLConnection con, String query) throws IOException {
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(query);
        wr.flush();
        wr.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jTree = mapper.readTree(in);
        JsonNode bindings = jTree.findValue("bindings");
        return bindings;
    }

    private HttpURLConnection getQueryConnection() throws IOException {
        URL obj = new URL(QUERY_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/sparql-query");
        return con;
    }
}
