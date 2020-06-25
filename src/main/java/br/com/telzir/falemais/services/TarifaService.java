package br.com.telzir.falemais.services;

import br.com.telzir.falemais.enums.PlanoFaleMais;
import br.com.telzir.falemais.enums.Tarifa;
import br.com.telzir.falemais.presenters.PlanoPresenter;
import br.com.telzir.falemais.presenters.SimulacaoPresenter;
import br.com.telzir.falemais.presenters.TarifaPresenter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TarifaService {

    public List<TarifaPresenter> getTarifas() {
        List<TarifaPresenter> tarifas = new ArrayList<>();
        for (Tarifa tarifa : Tarifa.values()) { // old school, é bom saber devido a legado ;)
            tarifas.add(new TarifaPresenter(tarifa));
        }
        return tarifas;
    }

    public List<PlanoPresenter> getPlanos() {
        List<PlanoPresenter> tarifas = Arrays.stream(PlanoFaleMais.values())
                .map(plano -> new PlanoPresenter(plano))
                .collect(Collectors.toList());
        return tarifas;
    }

    public List<String> getDddsOrigem() {
        return Arrays.stream(Tarifa.values())
                .map(Tarifa::getOrigem).distinct()
                .collect(Collectors.toList());
    }

    public List<String> getDddsDestino(String dddOrigem) {
        return Arrays.stream(Tarifa.values())
                .filter(tarifa -> (tarifa.getOrigem().equals(dddOrigem)))
                .map(Tarifa::getDestino).distinct()
                .collect(Collectors.toList());
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

            return new SimulacaoPresenter(origem, destino, minutos, plano.getDescricao(), valorComFaleMais, valorSemFaleMais);

        } catch (IllegalArgumentException e) {
            BigDecimal valorTarifa = origem.equals(destino) ? BigDecimal.ZERO : null;
            return new SimulacaoPresenter(origem, destino, minutos, plano.getDescricao(), valorTarifa, valorTarifa);
        }
    }

    @NotNull
    public List<SimulacaoPresenter> getSimulacaoTodosPlanos(String origem, String destino, int minutos) {
        return Arrays.stream(PlanoFaleMais.values())
                .map(plano -> getSimulacao(origem, destino, minutos, plano))
                .collect(Collectors.toList());
    }
}
