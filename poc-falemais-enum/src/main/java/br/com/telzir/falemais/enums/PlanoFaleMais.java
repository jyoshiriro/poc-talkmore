package br.com.telzir.falemais.enums;

public enum PlanoFaleMais {

    FALE_MAIS_30(30, "Fale Mais 30"),
    FALE_MAIS_60(60, "Fale Mais 60"),
    FALE_MAIS_120(120, "Fale Mais 120");

    int minutosFranquia;
    String nome;

    PlanoFaleMais(int minutosFranquia, String nome) {
        this.minutosFranquia = minutosFranquia;
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public int getMinutosFranquia() {
        return minutosFranquia;
    }
}
