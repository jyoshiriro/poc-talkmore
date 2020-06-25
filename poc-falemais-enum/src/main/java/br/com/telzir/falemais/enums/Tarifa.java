package br.com.telzir.falemais.enums;

public enum Tarifa {

    A("11","16", 1.9),
    B("16","11", 2.9),
    C("11","17", 1.7),
    D("17","11", 2.7),
    E("11","18", 0.9),
    F("18","11", 1.9);

    String origem;
    String destino;
    double tarifaMinuto;

    Tarifa(String origem, String destino, double tarifa) {
        this.origem = origem;
        this.destino = destino;
        this.tarifaMinuto = tarifa;
    }

    public String getOrigem() {
        return origem;
    }

    public String getDestino() {
        return destino;
    }

    public double getTarifaMinuto() {
        return tarifaMinuto;
    }

    public static Tarifa getPorOrigem(String origem) {
        for (Tarifa tarifa : values()) {
            if (tarifa.origem.equals(origem)) {
                return tarifa;
            }
        }
        throw new IllegalArgumentException(
                String.format("Tarifa de origem '%s' não encontrada", origem));
    }

    public static Tarifa getPorOrigemDestino(String origem, String destino) {
        for (Tarifa tarifa : values()) {
            if (tarifa.origem.equals(origem) && tarifa.destino.equals(destino)) {
                return tarifa;
            }
        }
        throw new IllegalArgumentException(
                String.format("Tarifa de origem '%s' e destino '%s' não encontrada", origem, destino));
    }
}
