const optionInterrogacao = '<option value="0">-?-</option>';

function abrirSimulacao() {
    iniciar.style.display = 'none';
    motivacao.style.display = 'none';
    obterDddsOrigem();
}

function obterDddsOrigem() {
    
    ocultarDestinoSimulacao();

    fetch('/tarifas/ddd/origens').then(function (resHttp) {
        if (resHttp.ok) {
            resHttp.json().then(function (json) {

                console.log(`DDDs de Origem: ${JSON.stringify(json)}`);

                ddd_origem.innerHTML = optionInterrogacao;
                json.forEach((item, i) => {
                    ddd_origem.innerHTML += `<option value="${item}">${item}</option>`
                })

                simulador.style.display = '';
                minutos.focus();
            });
        } else {
            console.error('Nenhum dado encontrado ou erro na API');
        }
    })
    .catch(function (error) {
        console.error(`Erro na obtenção dos DDD de origem ${error.message}`);
    });
}

function filtrarDestinos(dddOrigem) {
    if (dddOrigem == '0') {
        ocultarDestinoSimulacao();
    }
    fetch(`/tarifas/ddd/destinos/${dddOrigem}`).then(function (resHttp) {
        if (resHttp.ok) {
            resHttp.json().then(function (json) {

                console.log(`DDDs de Destino: ${JSON.stringify(json)}`);

                const variosDestinos = json.length > 1;

                ddd_destino.innerHTML = variosDestinos ? optionInterrogacao : '';

                btn_simular.style.display = variosDestinos ? 'none' : ''

                json.forEach((item, i) => {
                    ddd_destino.innerHTML += `<option value="${item}">${item}</option>`
                })

                destinos.style.display='';
            });
        } else {
            console.error('Nenhum dado encontrado ou erro na API');
        }
    })
    .catch(function (error) {
        console.error(`Erro na obtenção dos DDD de destino ${error.message}`);
    });
}

function ocultarDestinoSimulacao() {
    destinos.style.display = 'none';
    btn_simular.style.display = 'none';
}

function verificarBotaoSimulacao() {
    btn_simular.style.display = ddd_destino.value == '0' ? 'none' : '';
}

function simular() {
    let uri = `/tarifas/simulacao/${ddd_origem.value}/${ddd_destino.value}/${minutos.value}`
    if (plano.value != '0') {
        uri+=`/${plano.value}`
    }

    console.log(uri);

    fetch(uri, { cache: 'no-store' }).then(function (resHttp) {
        if (resHttp.ok) {
            resHttp.json().then(function (json) {

                console.log(`Dados da simulação: ${JSON.stringify(json)}`);

                simulado.innerHTML = '';

                json.forEach((item, i) => {
                    let linha = `<tr>`;
                    linha += `<td>${item.plano}</td>`;
                    linha += `<td>R$${item.semFaleMais}</td>`;
                    linha += `<td>R$${item.comFaleMais}</td>`;
                    linha += `<td>R$${item.semFaleMais-item.comFaleMais}</td>`;
                    linha += `</tr>`;
                    simulado.innerHTML += linha;
                })
                
            });
        } else {
            console.error('Nenhum dado encontrado ou erro na API');
        }
    })
    .catch(function (error) {
        console.error(`Erro na obtenção dos dados da simulação ${error.message}`);
    });    
}