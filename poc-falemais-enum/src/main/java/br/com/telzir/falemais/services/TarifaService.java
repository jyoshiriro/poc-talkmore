package br.com.telzir.falemais.services;

import br.com.telzir.falemais.enums.PlanoFaleMais;
import br.com.telzir.falemais.enums.Tarifa;
import br.com.telzir.falemais.presenters.SimulacaoPresenter;
import br.com.telzir.falemais.presenters.TarifaPresenter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class TarifaService {

    public List<TarifaPresenter> getTarifas() {
        List<TarifaPresenter> tarifas = new ArrayList<>();
        for (Tarifa tarifa : Tarifa.values()) {
            tarifas.add(new TarifaPresenter(tarifa));
        }
        return tarifas;
    }

    /**
     *
     * @param origem
     * @param destino
     * @param minutos
     * @param plano
     * @return
     */
    public SimulacaoPresenter getSimulacao(String origem, String destino, int minutos, PlanoFaleMais plano) {

        int minutosExcedentes = minutos - plano.getMinutosFranquia();

        try {
            Tarifa tarifa = Tarifa.getPorOrigemDestino(origem, destino);

            BigDecimal valorComFaleMais = minutosExcedentes > 0
                    ? BigDecimal.valueOf(minutosExcedentes)
                    .multiply(BigDecimal.valueOf(tarifa.getTarifaMinuto()))
                    .multiply(BigDecimal.valueOf(1.1))
                    : BigDecimal.ZERO;

            BigDecimal valorSemFaleMais = BigDecimal.valueOf(minutos).multiply(BigDecimal.valueOf(tarifa.getTarifaMinuto()));

            return new SimulacaoPresenter(origem, destino, minutos, plano.getNome(), valorComFaleMais, valorSemFaleMais);

        } catch (IllegalArgumentException e) {
            BigDecimal valorTarifa = origem.equals(destino) ? BigDecimal.ZERO : null;
            return new SimulacaoPresenter(origem, destino, minutos, plano.getNome(), valorTarifa, valorTarifa);
        }
    }
}
