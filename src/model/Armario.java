package model;

public class Armario {
    private int idArmario;
    private String armario;
    private String partileira;

    public Armario() {}

    public int getIdArmario() {
        return idArmario;
    }

    public void setIdArmario(int idArmario) {
        this.idArmario = idArmario;
    }

    public String getArmario() {
        return armario;
    }

    public void setArmario(String armario) {
        this.armario = armario;
    }

    public String getPartileira() {
        return partileira;
    }

    public void setPartileira(String partileira) {
        this.partileira = partileira;
    }

    @Override
    public String toString() {
        if (armario != null && partileira != null && !partileira.isEmpty()) {
            return armario + " - " + partileira;
        }
        return armario != null ? armario : "";
    }
}