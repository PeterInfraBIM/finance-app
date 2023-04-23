package nl.infrabim.financeapp.csv;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public enum Column {
    Datum, NaamOmschrijving, Rekening, TegenRekening, Code, AfBij, Bedrag, Mutatiesoort, Mededelingen, SaldoNaMutatie, Tag;

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

    public String getType() {
        return switch (this) {
            case Datum -> "Date";
            case NaamOmschrijving -> "String";
            case Rekening -> "String";
            case TegenRekening -> "String";
            case Code -> "MutationCode";
            case AfBij -> "CreditDebit";
            case Bedrag -> "Float";
            case Mutatiesoort -> "MutationType";
            case Mededelingen -> "String";
            case SaldoNaMutatie -> "Float";
            case Tag -> "String";
        };
    }

    public String getName() {
        return switch (this) {
            case Datum -> "Datum";
            case NaamOmschrijving -> "Naam / Omschrijving";
            case Rekening -> "Rekening";
            case TegenRekening -> "Tegenrekening";
            case Code -> "Code";
            case AfBij -> "Af Bij";
            case Bedrag -> "Bedrag (EUR)";
            case Mutatiesoort -> "Mutatiesoort";
            case Mededelingen -> "Mededelingen";
            case SaldoNaMutatie -> "Saldo na mutatie";
            case Tag -> "Tag";
        };
    }

    private static final String NAME_SPACE = "http://infrabim.nl/finance#";

    public Property getProperty(Model model) {
        return switch (this) {
            case Datum -> model.getProperty(NAME_SPACE, "date");
            case NaamOmschrijving -> model.getProperty(NAME_SPACE, "name");
            case Rekening -> model.getProperty(NAME_SPACE, "account");
            case TegenRekening -> model.getProperty(NAME_SPACE, "counterAccount");
            case Code -> model.getProperty(NAME_SPACE, "mutationCode");
            case AfBij -> model.getProperty(NAME_SPACE, "credit");
            case Bedrag -> model.getProperty(NAME_SPACE, "amount");
            case Mutatiesoort -> model.getProperty(NAME_SPACE, "mutationType");
            case Mededelingen -> model.getProperty(NAME_SPACE, "description");
            case SaldoNaMutatie -> null;
            case Tag -> model.getProperty(NAME_SPACE, "tag");
        };
    }


    public Object getValue(String strVal) {
        try {
            return switch (this.getType()) {
                case "CreditDebit" -> CreditDebit.valueOf(strVal.trim());
                case "Date" -> LocalDate.parse(strVal, DateTimeFormatter.ofPattern("yyyyMMdd"));
                case "Float" -> Float.valueOf(strVal.trim().replace(',', '.'));
                case "MutationCode" -> Mutation.valueOf(strVal.trim());
                case "MutationType" -> Mutation.getByMutationType(strVal.trim()).getMutationTitle();
                case "String" -> strVal;
                default -> null;
            };
        } catch (DateTimeParseException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    public static Column getColumn(String name) {
        return switch (name) {
            case "Af Bij" -> AfBij;
            case "Bedrag (EUR)" -> Bedrag;
            case "Code" -> Code;
            case "Datum" -> Datum;
            case "Mededelingen" -> Mededelingen;
            case "Mutatiesoort" -> Mutatiesoort;
            case "Naam / Omschrijving" -> NaamOmschrijving;
            case "Rekening" -> Rekening;
            case "Saldo na mutatie" -> SaldoNaMutatie;
            case "Tag" -> Tag;
            case "Tegenrekening" -> TegenRekening;
            default -> null;
        };
    }

}
