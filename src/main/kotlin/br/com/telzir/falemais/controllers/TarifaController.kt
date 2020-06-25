package br.com.telzir.falemais.controllers

import br.com.telzir.falemais.enums.PlanoFaleMais
import br.com.telzir.falemais.presenters.PlanoPresenter
import br.com.telzir.falemais.presenters.SimulacaoPresenter
import br.com.telzir.falemais.presenters.TarifaPresenter
import br.com.telzir.falemais.services.TarifaService
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.noContent
import org.springframework.http.ResponseEntity.ok
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@RestController
@RequestMapping("/tarifas")
@Validated
class TarifaController {

    @Autowired
    private lateinit var tarifaService: TarifaService

    private fun <T> okOuNoContent(lista: List<T>): ResponseEntity<List<T>> { // criada apenas p/ brincar com Generics da kotlin
        return if (lista.isEmpty()) noContent().build() else ok(lista)
    }

    @GetMapping
    fun getTarifas():ResponseEntity<List<TarifaPresenter>> {
        // se fosse fazer direto com kotlin: val tarifas = Tarifa.values().map { TarifaPresenter(it) }
        val tarifas = this.tarifaService.tarifas
        return okOuNoContent(tarifas)
    }

    @GetMapping("/planos")
    fun getPlanos():ResponseEntity<List<PlanoPresenter>> {
        val planos = this.tarifaService.planos
        return okOuNoContent(planos)
    }

    @GetMapping("/ddd/origens")
    fun getDddsOrigem():ResponseEntity<List<String>> {
        val tarifas = this.tarifaService.dddsOrigem
        return okOuNoContent(tarifas)
    }

    @GetMapping("/ddd/destinos/{dddOrigem}")
    fun getDddsDestino(@PathVariable dddOrigem: String):ResponseEntity<List<String>> {
        val tarifas = this.tarifaService.getDddsDestino(dddOrigem)
        return okOuNoContent(tarifas)
    }

    @GetMapping("/simulacao/{origem}/{destino}/{minutos}/{plano}")
    fun getSimulacao(@PathVariable origem:String,
                     @PathVariable destino:String,
                     @PathVariable @Min(1) @Max(1440) @ApiParam(allowableValues = "min:1, max:1440") minutos:Int,
                     @PathVariable plano:PlanoFaleMais
    ):ResponseEntity<SimulacaoPresenter> {
        return ok(this.tarifaService.getSimulacao(origem, destino, minutos, plano))
    }

    @GetMapping("/simulacao/{origem}/{destino}/{minutos}")
    fun getSimulacaoTodosPlanos(@PathVariable origem:String,
                     @PathVariable destino:String,
                     @PathVariable @Min(1) @Max(1440) @ApiParam(allowableValues = "min:1, max:1440") minutos:Int
    ):ResponseEntity<List<SimulacaoPresenter>> {
        val simulacoes = this.tarifaService.getSimulacaoTodosPlanos(origem, destino, minutos)
        return okOuNoContent(simulacoes)
    }
}