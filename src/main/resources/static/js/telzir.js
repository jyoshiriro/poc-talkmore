const optionInterrogacao = '<option value="0">-?-</option>';
const optionPlano = '<option value="0">Simular com todos</option>';

function abrirSimulacao() {
    iniciar.style.display = 'none';
    obterDadosIniciais();
}

function obterDadosIniciais() {
    obterPlanos();
    obterDddsOrigem();
}

function obterPlanos() {

    fetch('/tarifas/planos').then(function (resHttp) {
        if (resHttp.ok) {
            resHttp.json().then(function (json) {

                console.log(`Planos: ${JSON.stringify(json)}`);

                plano.innerHTML = optionPlano;
                json.forEach((item, i) => {
                    plano.innerHTML += `<option value="${item.codigo}">${item.descricao}</option>`
                });

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

function obterDddsOrigem() {
    
    ocultarDestinoSimulacao();

    fetch('/tarifas/ddd/origens').then(function (resHttp) {
        if (resHttp.ok) {
            resHttp.json().then(function (json) {

                console.log(`DDDs de Origem: ${JSON.stringify(json)}`);

                ddd_origem.innerHTML = optionInterrogacao;
                json.forEach((item, i) => {
                    ddd_origem.innerHTML += `<option value="${item}">${item}</option>`
                });

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

                btn_simular.style.visibility = variosDestinos ? 'hidden' : 'visible';

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
    btn_simular.style.visibility = 'hidden';
}

function verificarBotaoSimulacao() {
    btn_simular.style.visibility = ddd_destino.value == '0' ? 'hidden' : 'visible';
}

function simular() {
    let uri = `/tarifas/simulacao/${ddd_origem.value}/${ddd_destino.value}/${minutos.value}`
    if (plano.value != '0') {
        uri+=`/${plano.value}`
    }

    fetch(uri, { cache: 'no-store' }).then(function (resHttp) {
        if (resHttp.ok) {
            resHttp.json().then(function (json) {

                simulacao_minutos.innerHTML = minutos.value;
                simulacao_origem.innerHTML = ddd_origem.value;
                simulacao_destino.innerHTML = ddd_destino.value;

                console.log(`Dados da simulação: ${JSON.stringify(json)}`);

                simulado.innerHTML = '';

                if (Array.isArray(json)) {
                    json.forEach((item, i) => {
                        simulado.innerHTML += linhaSimulacao(item);
                    });
                } else {
                    simulado.innerHTML += linhaSimulacao(json);
                }

                resultado.style.display = '';
                
            });
        } else {
            console.error('Nenhum dado encontrado ou erro na API');
        }
    })
    .catch(function (error) {
        console.error(`Erro na obtenção dos dados da simulação ${error.message}`);
    });    
}

function linhaSimulacao(jsonSimulacao) {
    let linha = `
    <tr>
        <td><b>${jsonSimulacao.plano}</b></td>
        <td><span class="text-dark">${formatoMoeda(jsonSimulacao.semFaleMais)}</span></td>
        <td><b class="text-primary">${formatoMoeda(jsonSimulacao.comFaleMais)}</b></td>
    </tr>
    `;
    return linha;
}

function formatoMoeda(numero) {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(numero);
}