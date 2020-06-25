package br.com.telzir.falemais.presenters

import java.math.BigDecimal

data class SimulacaoPresenter(
        val origem:String,
        val destino:String,
        val minutos:Int,
        val plano:String,
        val comFaleMais:BigDecimal?,
        val semFaleMais:BigDecimal?)