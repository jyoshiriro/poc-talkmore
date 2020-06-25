package br.com.telzir.falemais.services

import br.com.telzir.falemais.presenters.SimulacaoPresenter
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Unroll

import static br.com.telzir.falemais.enums.PlanoFaleMais.FALE_MAIS_120
import static br.com.telzir.falemais.enums.PlanoFaleMais.FALE_MAIS_30
import static br.com.telzir.falemais.enums.PlanoFaleMais.FALE_MAIS_60

class TarifaServiceTest extends Specification {
    def "GetTarifas"() {
    }

    @Unroll
    def "Simulação c/ origem: #origem, destino: #destino, minutos: #minutos, plano: #plano - Com Fale Mais: \$#com, Sem Fale Mais: \$#sem"() {
        expect:
        def simulacao = new TarifaService().getSimulacao(origem, destino, minutos, plano)
        simulacao.comFaleMais?.doubleValue() == com
        simulacao.semFaleMais?.doubleValue() == sem

        where:
        origem | destino | minutos | plano         | com   | sem
        '11'   | '16'    | 20      | FALE_MAIS_30  | 0     | 38  // do exemplo do PDF
        '11'   | '16'    | 60      | FALE_MAIS_30  | 62.7  | 114
        '11'   | '16'    | 40      | FALE_MAIS_60  | 0     | 76
        '11'   | '16'    | 80      | FALE_MAIS_60  | 41.80 | 152
        '11'   | '16'    | 90      | FALE_MAIS_120 | 0     | 171
        '11'   | '16'    | 150     | FALE_MAIS_120 | 62.7     | 285

        '11'   | '17'    | 20      | FALE_MAIS_30  | 0     | 34
        '11'   | '17'    | 60      | FALE_MAIS_30  | 56.1  | 102
        '11'   | '17'    | 40      | FALE_MAIS_60  | 0     | 68
        '11'   | '17'    | 80      | FALE_MAIS_60  | 37.4  | 136  // do exemplo do PDF
        '11'   | '17'    | 90      | FALE_MAIS_120 | 0     | 153
        '11'   | '17'    | 150     | FALE_MAIS_120 | 56.1  | 255

        '11'   | '18'    | 20      | FALE_MAIS_30  | 0     | 18
        '11'   | '18'    | 60      | FALE_MAIS_30  | 29.7  | 54
        '11'   | '18'    | 40      | FALE_MAIS_60  | 0     | 36
        '11'   | '18'    | 80      | FALE_MAIS_60  | 19.8  | 72
        '11'   | '18'    | 90      | FALE_MAIS_120 | 0     | 81
        '11'   | '18'    | 150     | FALE_MAIS_120 | 29.7  | 135


        '11'   | '20'    | 20      | FALE_MAIS_30  | null  | null
        '11'   | '20'    | 40      | FALE_MAIS_60  | null  | null
        '11'   | '11'    | 40      | FALE_MAIS_120  | null  | null
        '11'   | '20'    | 40      | FALE_MAIS_120  | null  | null
        '16'   | '16'    | 100     | FALE_MAIS_30  | null  | null
        '16'   | '17'    | 100     | FALE_MAIS_30  | null  | null
        '18'   | '11'    | 200     | FALE_MAIS_120 | 167.2 | 380  // do exemplo do PDF
        '18'   | '17'    | 100     | FALE_MAIS_30  | null  | null  // do exemplo do PDF
        '17'   | '18'    | 100     | FALE_MAIS_30  | null  | null


        '17'   | '11'    | 300     | FALE_MAIS_120 | 534.6 | 810

        '19'   | '19'    | 100     | FALE_MAIS_30  | null  | null


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
}
