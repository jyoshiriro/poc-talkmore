# POC do Talk More
Instruções de uso

## Inicialização do projeto

### Opção 1: 
Executar a classe **br.com.telzir.falemais.PocFalemaisApplication** na IDE

### Opção 2:
Na pasta do projeto, executar `mvn package` e, depois, `java -jar target/poc-falemais-0.0.1.jar`

## Navegado no projeto
### Site 
  O site que acessa a API de simulação fica disponível em: http://localhost:8321/
  
  Basta ir no item "Quanta Economia!" (canto superior direito) e depois em "Simular!" (para onde a página va te levar e à esquerda).
  
  Foi feito assim para facilitar a vida do avaliador, bastando inicializar uma aplicação. A escolha da porta **8321** foi porque, provavelmente, a porta **8080** do avaliador vai estar ocupada.

### API
  Basta acessar o Swagger da API em http://localhost:8321/swagger-ui.html
