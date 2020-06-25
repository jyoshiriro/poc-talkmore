package br.com.telzir.falemais.presenters

import br.com.telzir.falemais.enums.Tarifa

data class TarifaPresenter(
        val origem:String,
        val destino:String,
        val tarifa:Double
) {
    constructor(tarifa: Tarifa) : this(tarifa.origem, tarifa.destino, tarifa.tarifaMinuto)
}