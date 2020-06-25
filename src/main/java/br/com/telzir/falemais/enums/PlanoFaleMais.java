package br.com.telzir.falemais.enums;

public enum PlanoFaleMais {

    FALE_MAIS_30(30, "Fale Mais 30"),
    FALE_MAIS_60(60, "Fale Mais 60"),
    FALE_MAIS_120(120, "Fale Mais 120");

    int minutosFranquia;
    String descricao;

    PlanoFaleMais(int minutosFranquia, String descricao) {
        this.minutosFranquia = minutosFranquia;
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getMinutosFranquia() {
        return minutosFranquia;
    }
}
