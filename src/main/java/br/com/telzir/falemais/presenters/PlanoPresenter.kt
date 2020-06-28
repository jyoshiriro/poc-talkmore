package br.com.telzir.falemais.presenters

import br.com.telzir.falemais.enums.PlanoFaleMais

data class PlanoPresenter(
        val codigo:String,
        val descricao:String
) {
    constructor(plano: PlanoFaleMais) : this(plano.name, plano.descricao)
}