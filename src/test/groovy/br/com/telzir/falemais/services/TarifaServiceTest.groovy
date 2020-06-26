package br.com.telzir.falemais.services

import br.com.telzir.falemais.enums.PlanoFaleMais
import br.com.telzir.falemais.enums.Tarifa
import spock.lang.Specification
import spock.lang.Unroll

import static br.com.telzir.falemais.enums.PlanoFaleMais.*

class TarifaServiceTest extends Specification {

    TarifaService service

    def setup() {
        service = new TarifaService()
    }

    def 'Lista de tarifas da service deve estar com os valores recuperados da base de Tarifa'() {
        given: 'Todas as tarifas cadastradas'
        def tarifasBase = Tarifa.values()

        when: 'Recuperando as tarifas da Service'
        def presenters = service.getTarifas()

        then: 'A lista tem que possuir mesmo tamanho'
        presenters.size() == tarifasBase.size()

        and: 'E os valores dos itens devem estar corretos'
        tarifasBase.eachWithIndex { tarifa, i ->
            assert tarifa.origem == presenters[i].origem
            assert tarifa.destino == presenters[i].destino
            assert tarifa.tarifaMinuto == presenters[i].tarifa
        }
    }

    def 'Lista de planos da service deve estar com os valores recuperados da base de Planos'() {
        given: 'Todas as tarifas cadastradas'
        def planosBase = PlanoFaleMais.values()

        when: 'Recuperando as tarifas da Service'
        def presenters = service.getPlanos()

        then: 'A lista tem que possuir mesmo tamanho'
        presenters.size() == planosBase.size()

        and: 'E os valores dos itens devem estar corretos'
        planosBase.eachWithIndex { plano, i ->
            assert plano.name() == presenters[i].codigo
            assert plano.descricao == presenters[i].descricao
        }
    }

    def 'Lista de DDDs de origem da service deve possuir todos os DDD que estão em origem na base de Tarifa'() {
        given: 'Todos os DDDs de origem cadastrados'
        def origensBase = Tarifa.values().collect {it.origem}.toUnique()

        when: 'Recuperando os DDDs da Service'
        def presenters = service.getDddsOrigem()

        then: 'A lista tem que possuir mesmo tamanho'
        presenters.size() == origensBase.size()

        and: 'E os valores dos itens devem estar corretos'
        origensBase.eachWithIndex { origem, i ->
            assert origem == presenters[i]
        }
    }

    def 'Lista de DDDs de destino da service deve possuir todos os DDD de destino conforme uma origem informada'() {
        given: 'Todos os DDDs de origem cadastrados'
        def origensBase = Tarifa.values().collect {it.origem}.toUnique()

        when: 'Recuperando os DDDs da Service'
        List<List> presenters = []
        List<List> destinos = []
        origensBase.each { origem ->
            presenters.add(service.getDddsDestino(origem))
            destinos.add(Tarifa.values().findAll {it.origem == origem}.collect {it.destino}.unique())
        }

        then: 'A lista tem que possuir mesmo tamanho e os valores dos itens devem estar corretos'
        presenters.eachWithIndex { listaPresenters, i ->
            def listaDestinos = destinos[i]
            assert listaPresenters.size() == listaDestinos.size()
            assert listaPresenters.containsAll(listaDestinos)
        }

    }

    @Unroll
    def "Simulação c/ origem: #origem, destino: #destino, minutos: #minutos, plano: #plano - Com Fale Mais: \$#com, Sem Fale Mais: \$#sem"() {
        setup:
        def simulacao = service.getSimulacao(origem, destino, minutos, plano)

        expect: 'Os valores de tarifa com e sem o plano FaleMais devem estar corretos e demais valores inalterados'
        simulacao.comFaleMais?.doubleValue() == com
        simulacao.semFaleMais?.doubleValue() == sem
        simulacao.origem == origem
        simulacao.destino == destino
        simulacao.plano == plano.getDescricao()

        where: 'Valores com e sem plano FaleMais em diferentes combinações'
        origem | destino | minutos | plano         | com   | sem
        '11'   | '16'    | 20      | FALE_MAIS_30  | 0     | 38  // do exemplo do PDF
        '11'   | '16'    | 60      | FALE_MAIS_30  | 62.7  | 114
        '11'   | '16'    | 40      | FALE_MAIS_60  | 0     | 76
        '11'   | '16'    | 80      | FALE_MAIS_60  | 41.80 | 152
        '11'   | '16'    | 90      | FALE_MAIS_120 | 0     | 171
        '11'   | '16'    | 200     | FALE_MAIS_120 | 167.2 | 380

        '11'   | '17'    | 20      | FALE_MAIS_30  | 0     | 34
        '11'   | '17'    | 60      | FALE_MAIS_30  | 56.1  | 102
        '11'   | '17'    | 40      | FALE_MAIS_60  | 0     | 68
        '11'   | '17'    | 80      | FALE_MAIS_60  | 37.4  | 136  // do exemplo do PDF
        '11'   | '17'    | 90      | FALE_MAIS_120 | 0     | 153
        '11'   | '17'    | 200     | FALE_MAIS_120 | 149.6 | 340

        '11'   | '18'    | 20      | FALE_MAIS_30  | 0     | 18
        '11'   | '18'    | 60      | FALE_MAIS_30  | 29.7  | 54
        '11'   | '18'    | 40      | FALE_MAIS_60  | 0     | 36
        '11'   | '18'    | 80      | FALE_MAIS_60  | 19.8  | 72
        '11'   | '18'    | 90      | FALE_MAIS_120 | 0     | 81
        '11'   | '18'    | 200     | FALE_MAIS_120 | 79.2  | 180

        '17'   | '11'    | 20      | FALE_MAIS_30  | 0     | 54
        '17'   | '11'    | 60      | FALE_MAIS_30  | 89.1  | 162
        '17'   | '11'    | 40      | FALE_MAIS_60  | 0     | 108
        '17'   | '11'    | 80      | FALE_MAIS_60  | 59.4  | 216
        '17'   | '11'    | 90      | FALE_MAIS_120 | 0     | 243
        '17'   | '11'    | 200     | FALE_MAIS_120 | 237.6 | 540
        
        '18'   | '11'    | 20      | FALE_MAIS_30  | 0     | 38
        '18'   | '11'    | 60      | FALE_MAIS_30  | 62.7  | 114
        '18'   | '11'    | 40      | FALE_MAIS_60  | 0     | 76
        '18'   | '11'    | 80      | FALE_MAIS_60  | 41.8  | 152
        '18'   | '11'    | 90      | FALE_MAIS_120 | 0     | 171
        '18'   | '11'    | 200     | FALE_MAIS_120 | 167.2 | 380  // do exemplo do PDF

        and: 'Combinações de DDD origem e destino não previstas, tarifa impossível de calcular'
        '16'   | '17'    | 100     | FALE_MAIS_30  | null  | null
        '16'   | '17'    | 100     | FALE_MAIS_60  | null  | null
        '16'   | '17'    | 100     | FALE_MAIS_120 | null  | null
        '16'   | '18'    | 100     | FALE_MAIS_30  | null  | null
        '16'   | '18'    | 100     | FALE_MAIS_60  | null  | null
        '16'   | '18'    | 100     | FALE_MAIS_120 | null  | null
        '17'   | '16'    | 100     | FALE_MAIS_30  | null  | null
        '17'   | '16'    | 100     | FALE_MAIS_60  | null  | null
        '17'   | '16'    | 100     | FALE_MAIS_120 | null  | null
        '17'   | '18'    | 100     | FALE_MAIS_30  | null  | null
        '17'   | '18'    | 100     | FALE_MAIS_60  | null  | null
        '17'   | '18'    | 100     | FALE_MAIS_120 | null  | null
        '18'   | '16'    | 100     | FALE_MAIS_30  | null  | null
        '18'   | '16'    | 100     | FALE_MAIS_60  | null  | null
        '18'   | '16'    | 100     | FALE_MAIS_120 | null  | null
        '18'   | '17'    | 100     | FALE_MAIS_30  | null  | null  // do exemplo do PDF
        '18'   | '17'    | 100     | FALE_MAIS_60  | null  | null
        '18'   | '17'    | 100     | FALE_MAIS_120 | null  | null

        and: 'Mesmos DDD origem e destino, sem tarifa de DDD'
        '11'   | '11'    | 20      | FALE_MAIS_30  | 0     | 0
        '11'   | '11'    | 60      | FALE_MAIS_30  | 0     | 0
        '11'   | '11'    | 40      | FALE_MAIS_60  | 0     | 0
        '11'   | '11'    | 80      | FALE_MAIS_60  | 0     | 0
        '11'   | '11'    | 90      | FALE_MAIS_120 | 0     | 0
        '11'   | '11'    | 150     | FALE_MAIS_120 | 0     | 0
        '16'   | '16'    | 20      | FALE_MAIS_30  | 0     | 0
        '16'   | '16'    | 60      | FALE_MAIS_30  | 0     | 0
        '16'   | '16'    | 40      | FALE_MAIS_60  | 0     | 0
        '16'   | '16'    | 80      | FALE_MAIS_60  | 0     | 0
        '16'   | '16'    | 90      | FALE_MAIS_120 | 0     | 0
        '16'   | '16'    | 150     | FALE_MAIS_120 | 0     | 0
        '17'   | '17'    | 20      | FALE_MAIS_30  | 0     | 0
        '17'   | '17'    | 60      | FALE_MAIS_30  | 0     | 0
        '17'   | '17'    | 40      | FALE_MAIS_60  | 0     | 0
        '17'   | '17'    | 80      | FALE_MAIS_60  | 0     | 0
        '17'   | '17'    | 90      | FALE_MAIS_120 | 0     | 0
        '17'   | '17'    | 150     | FALE_MAIS_120 | 0     | 0
    }

    @Unroll
    def "Simulação para todos os planos deve conter a lista de simulações correta"() {
        given:
        def origem = Tarifa.values().first().origem
        def destino = Tarifa.values().first().destino
        def minutos = 50

        when:
        def simulacoes = service.getSimulacaoTodosPlanos(origem, destino, minutos)

        then: 'Os valores de tarifa com e sem o plano FaleMais devem estar corretos e demais valores inalterados'
        for (simulacao in simulacoes) {
            def simulacaoPlano = service.getSimulacao(origem, destino, minutos, PlanoFaleMais.getPorDescricao(simulacao.plano))
            assert simulacao == simulacaoPlano
        }

    }

}
