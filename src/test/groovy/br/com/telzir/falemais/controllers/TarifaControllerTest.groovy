package br.com.telzir.falemais.controllers

import br.com.telzir.falemais.enums.Tarifa
import br.com.telzir.falemais.presenters.SimulacaoPresenter
import br.com.telzir.falemais.presenters.TarifaPresenter
import br.com.telzir.falemais.services.TarifaService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Unroll

import javax.validation.ConstraintViolationException

import static br.com.telzir.falemais.enums.PlanoFaleMais.*

@SpringBootTest
class TarifaControllerTest extends Specification {

    @Autowired
    TarifaController controller

    @SpringBean
    TarifaService service = Mock()

    @Unroll
    def "GET /tarifas deve retornar status #codigoStatus e corpo #corpo"() {
        setup:
        service.getTarifas() >> lista
        def resposta = controller.getTarifas()

        expect:
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
                                    origem, destino, minutos, plano.getDescricao(), com?.toBigDecimal(), sem?.toBigDecimal())
        service.getSimulacao(origem, destino, minutos, plano) >> simulacao

        and:
        def resposta = controller.getSimulacao(origem, destino, minutos, plano)

        expect:
        resposta.statusCode == HttpStatus.OK
        resposta.body == simulacao

        where:
        origem | destino | minutos | plano         | com   | sem
        '11'   | '16'    | 20      | FALE_MAIS_30  | 0     | 38.0
        '11'   | '17'    | 80      | FALE_MAIS_60  | 37.4  | 136.0
        '18'   | '11'    | 200     | FALE_MAIS_120 | 167.2 | 380.0
        '18'   | '17'    | 100     | FALE_MAIS_30  | null  | null

    }

    @Unroll
    def "GET /simulacao/ com minutos inválidos (#minutos) deve retornar status 400"() {
        when: 'Invocando o EndPoint GET /simulacao com "minutos" inválidos'
        controller.getSimulacao(
                Tarifa.values()[0].getOrigem(), Tarifa.values()[0].getDestino(), minutos, FALE_MAIS_30)

        then: 'Na impossibilidade de ter o status 400, verifique se é lançada uma Exceção apropriada'
        def exception = thrown(ConstraintViolationException.class)
        exception.constraintViolations.size() == 1
        exception.constraintViolations[0].propertyPath.last().name == 'minutos'

        where:
        minutos << [-1, 0, 1441, 2000]

    }
}
