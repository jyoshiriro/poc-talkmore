package br.com.telzir.falemais.controllers

import br.com.telzir.falemais.enums.Tarifa
import br.com.telzir.falemais.services.TarifaService
import br.com.telzir.falemais.presenters.SimulacaoPresenter
import br.com.telzir.falemais.presenters.TarifaPresenter
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Unroll

import static br.com.telzir.falemais.enums.PlanoFaleMais.FALE_MAIS_120
import static br.com.telzir.falemais.enums.PlanoFaleMais.FALE_MAIS_30
import static br.com.telzir.falemais.enums.PlanoFaleMais.FALE_MAIS_60

@SpringBootTest(classes = TarifaController)
class TarifaControllerTest extends Specification {

    @Autowired
    TarifaController controller

    @SpringBean
    TarifaService service = Mock()

    @Unroll
    def "GET /tarifas deve retornar status #codigoStatus e corpo #corpo"() {
        setup:
        service.getTarifas() >> lista

        expect:
        def resposta = controller.getTarifas()
        resposta.statusCode == codigoStatus
        resposta.body == corpo

        where:
        lista                                                  | codigoStatus          | corpo
        []                                                     | HttpStatus.NO_CONTENT | null
        [new TarifaPresenter(Tarifa.values()[0])]              | HttpStatus.OK         | lista
        Tarifa.values()[0..1].collect{new TarifaPresenter(it)} | HttpStatus.OK         | lista

    }

    @Unroll
    def "GET /simulacao/#origem/#destino/#minutos/#plano deve retornar \$#com com Fale Mais e \$#sem sem"() {
        setup:
        def simulacao = new SimulacaoPresenter(
                                    origem, destino, minutos, plano.getNome(), com?.toBigDecimal(), sem?.toBigDecimal())
        service.getSimulacao(origem, destino, minutos, plano) >> simulacao

        expect:
        def resposta = controller.getSimulacao(origem, destino, minutos, plano)
        resposta.statusCode == HttpStatus.OK
        resposta.body == simulacao

        where:
        origem | destino | minutos | plano         | com   | sem
        '11'   | '16'    | 20      | FALE_MAIS_30  | 0     | 38.0
        '11'   | '17'    | 80      | FALE_MAIS_60  | 37.4  | 38.0
        '18'   | '11'    | 200     | FALE_MAIS_120 | 167.2 | 38.0
        '18'   | '17'    | 100     | FALE_MAIS_30  | null  | null
        '17'   | '18'    | 100     | FALE_MAIS_30  | null  | null
        '16'   | '18'    | 100     | FALE_MAIS_30  | null  | null
        '16'   | '17'    | 100     | FALE_MAIS_30  | null  | null
        '11'   | '18'    | 300     | FALE_MAIS_30  | 0     | 38.0

    }

}
