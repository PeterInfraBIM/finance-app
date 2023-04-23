package nl.infrabim.financeapp.csv;

public enum Mutation { BA, DV, GM, GT, IC, ID, OV, VZ;

    public String getMutationTitle() {
        return switch(this){
            case BA -> "Betaalautomaat";
            case DV -> "Diversen";
            case GM -> "Geldautomaat";
            case GT -> "Online bankieren";
            case IC -> "Incasso";
            case ID -> "iDEAL";
            case OV -> "Overschrijving";
            case VZ -> "Verzamelbetaling";
        };
    }

    public static Mutation getByMutationType(String mutationType){
        return switch(mutationType) {
            case "Betaalautomaat" -> BA;
            case "Diversen" -> DV;
            case "Geldautomaat" -> GM;
            case "Online bankieren" -> GT;
            case "Incasso" -> IC;
            case "iDEAL" -> ID;
            case "Overschrijving" -> OV;
            case "Verzamelbetaling" -> VZ;
            default -> null;
        };
    }
}
