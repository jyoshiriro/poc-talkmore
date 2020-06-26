package br.com.telzir.falemais.controllers

import br.com.telzir.falemais.enums.PlanoFaleMais
import br.com.telzir.falemais.enums.Tarifa
import br.com.telzir.falemais.presenters.PlanoPresenter
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

    def 'Deve retorna Ok ou NoContent'() {
        given:
        def controller = new TarifaController()
        def listaVazia = []
        def listaPreenchida = [1,2,3]

        when:
        def respostaVazia = controller.okOuNoContent(listaVazia)
        def respostaPrenchida = controller.okOuNoContent(listaPreenchida)

        then:
        respostaVazia.getStatusCode() == HttpStatus.NO_CONTENT
        respostaPrenchida.getStatusCode() == HttpStatus.OK
    }

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
    def "GET /tarifas/planos deve retornar status #codigoStatus e corpo #corpo"() {
        setup:
        service.getPlanos() >> lista
        def resposta = controller.getPlanos()

        expect:
        resposta.statusCode == codigoStatus
        resposta.body == corpo

        where:
        lista                                                         | codigoStatus          | corpo
        []                                                            | HttpStatus.NO_CONTENT | null
        [new PlanoPresenter(PlanoFaleMais.values()[0])]               | HttpStatus.OK         | lista
        PlanoFaleMais.values()[0..1].collect{new PlanoPresenter(it)}  | HttpStatus.OK         | lista
    }

    def "GET /tarifas/ddd/origens deve retornar todos os DDDs de origem fornecidos pela Service"() {
        given:
        def dddsOrigem = ['11','22','33']
        service.getDddsOrigem() >> dddsOrigem

        when:
        def resposta = controller.getDddsOrigem()

        then:
        resposta.statusCode == HttpStatus.OK
        resposta.body == dddsOrigem
    }

    @Unroll
    def "GET /tarifas/ddd/destinos deve retornar todos os DDDs #lista com status #codigoStatus para o DDD de origem #dddOrigem"() {
        setup:
        service.getDddsDestino(dddOrigem) >> lista
        def resposta = controller.getDddsDestino(dddOrigem)

        expect:
        resposta.statusCode == codigoStatus
        resposta.body == corpo

        where:
        dddOrigem | lista                       | codigoStatus          | corpo
        '10'      | ['11','12','13']            | HttpStatus.OK         | lista
        '20'      | ['21','22','23','24','25']  | HttpStatus.OK         | lista
        '30'      | []                          | HttpStatus.NO_CONTENT | null
    }

    @Unroll
    def "GET /tarifas/simulacao/#origem/#destino/#minutos/#plano deve retornar \$#com com Fale Mais e \$#sem sem"() {
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
    def "GET /tarifas/simulacao/ com minutos inválidos (#minutos) deve retornar status 400"() {
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

    @Unroll
    def "GET /tarifas/simulacao/#origem/#destino/#minutos deve retornar todos as simulações #lista"() {
        setup:
        service.getSimulacaoTodosPlanos(origem,destino,minutos) >> lista
        def resposta = controller.getSimulacaoTodosPlanos(origem, destino, minutos)

        expect:
        resposta.statusCode == codigoStatus
        resposta.body == corpo

        where:
        origem | destino | minutos | sem  | com | lista                       | codigoStatus          | corpo
        '1'    | '2'     | 100     | 10   | 1   | []                          | HttpStatus.NO_CONTENT | null
        '3'    | '4'     | 200     | 20   | 12  | [new SimulacaoPresenter(
                                                    origem, destino, minutos,
                                                    'p', com.toBigDecimal(),
                                                    sem.toBigDecimal())]      | HttpStatus.OK         | lista
        '5'    | '6'     | 300     | 30   | 0.3 | [new SimulacaoPresenter(
                                                    origem, destino, minutos,
                                                    'p', com.toBigDecimal(),
                                                    sem.toBigDecimal()),
                                                   new SimulacaoPresenter(
                                                    origem, destino, minutos,
                                                    'pp', com.toBigDecimal(),
                                                    sem.toBigDecimal())]      | HttpStatus.OK         | lista
    }
}
