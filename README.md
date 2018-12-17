# Descrição do projeto #

Splash Screen |  Main Screen | Friends Screen
:----------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------:
![Splash Screen](https://res.cloudinary.com/ufpe/image/upload/v1544986437/mf_splash.jpg)  |  ![Main Screen](https://res.cloudinary.com/ufpe/image/upload/v1544986409/mf_main.jpg) | ![Friends Screen](https://res.cloudinary.com/ufpe/image/upload/v1544986412/mf_friends.jpg) 


## Ideia ##
Temos um app com analytics de localização para usuários que precisem compartilhar seu histórico de localização de forma prática ou simplesmente queiram entender de como são seus comportamentos de localização no dia-a-dia.

## Público-alvo ##
O público que procuramos atingir são as pessoas que desejam compartilhar a localização delas (imediata ou histórica) de forma prática ou analisar de acordo com períodos de tempo, seus comportamentos de localização (quem nunca se pegou pensando, onde eu estava no dia tal?). 
O aplicativo também seria destinado às pessoas que tenham necessidade de comprovar informalmente que estiveram em algum local em determinado dia/horário.

## Ideias similares ##
O maior aplicativo que possui uma ferramenta parecida é nada mais nada menos que o whatsapp. A diferença principal é que o whatsapp possibilita compartilhar a localização atual (instantânea) ou dar permissão de localização em real-time. Se uma pessoa precisa comprovar que esteve em algum lugar, mas não havia dado permissão antes, não tem como voltar atrás. Se ela tivesse usando nosso aplicativo seria apenas selecionar a data num filtro e compartilhar! Outra diferença é que o whatsapp faz isso através da rede, nós propomos uma forma diferente de compartilhar a localização, que seria por SMS.

Após fecharmos a ideia pesquisamos um pouco e existe um app pequeno na Play Store que cumpre uma função parecida: o PathApp. Ele também salva o histórico de localização dos usuários e se propõe a atingir o mesmo público alvo. Porém ele não possui nenhuma feature para compartilhar ou ver localizações que foram compartilhadas com um usuário, o que seria nosso diferencial.

## Funcionalidades ##
Para usar o aplicativo, basta no primeiro acesso fornecer o nome do usuário (que seria utilizado para identificá-lo quando seu dado fosse compartilhado).
* A tela principal seria responsável por apresentar ao usuário duas features principais: a análise histórica do seu comportamento de localização e os comportamentos de localização de amigos que foram compartilhados com você.
* Se o usuário optar por ver seu comportamento de localização, haveria um filtro de data e horário para que fosse possível escolher um range para ser avaliado. Quando escolhido, seria exibido um mapinha onde as visitas seriam destacadas de forma que o percurso fique claro. Seria possível, ainda, ver detalhes de uma visita (que a princípio seria só o horário). Nessa mesma tela, haveria a possibilidade de compartilhar aqueles dados sendo apresentados por SMS.
* Se o usuário optar por ver os históricos de localização de amigos, seria apresentado uma nova tela, onde haveria uma lista de localizações compartilhadas e seria possível clicar em cada uma para ver o percurso.

## Implementação ##
A primeira tela exibida (após splash) será uma tela de "registro", onde o usuário poderá se registrar ou logar através da UI do Firebase (apenas com telefone). 

A MainActivity vai possuir dois fragments responsáveis cada um por uma funcionalidade principal:

- Na funcionalidade do comportamento do usuário, teríamos filtros (seletores) interagindo com o mapa (Maps SDK) e um botão para compartilhar a localização. 

  - As informações de localização vão ser coletadas por um LocationListener que vai persistir o dado num SQLite. 
  - A detecção de uma visita vai ser uma lógica bem primitiva (muito atrás da InLoco), na qual apenas veremos o quanto o usuário se moveu num curto espaço de tempo.

- Na funcionalidade do comportamento compartilhado com o usuário, teríamos uma RecyclerView alimentado por um Adapter com dados que seriam persistidos num SQLite. Quando o usuário clicar no item, exibiremos num modal a trajetória percorrida pelo usuário que compartilhou.

O processo de compartilhar a localização seria uma Service dedicado, para evitar que a interação seja travada enquanto o SMS é enviado. O Service indicaria o término da operação por broadcast.

A MainActivity teria um BroadcastReceiver para tratar eventos de conclusão do envio (mostrar toast de sucesso na UI).

Teremos ainda uma tela de Settings onde seria possivel definir preferências na acurácia da localização (shared preferences).

Ainda teríamos um Service dedicado a ler os SMS recebidos pelo usuário e sempre que detectar um SMS de um app nosso, mostrar uma notificação caso o app esteja em background e persistir a mensagem no SQLite.

A princípio, conseguimos detectar que vamos precisar das seguintes permissões: 
* ACCESS_FINE_LOCATION
* ACCESS_COARSE_LOCATION
* RECEIVE_SMS
* SEND_SMS
* READ_CONTACTS

## Divisão de atividades ##
Nenhum dos dois integrantes têm experiência com Android, por isso pretendemos dividir de forma mais ou menos igualitária. Higor possui um pouco mais de experiência com frontend e Ruy backend, por isso vamos tentar a princípio dividir dessa forma (visto que o trabalho de frontend neste projeto especificamente não parece ser tão simples pela necessidade de interagir com mapas). Ambos trabalhamos na InLoco, então a inspiração de um aplicativo com uso de localização não é totalmente aleatória.

## Demo ##

Assista no YouTube: https://youtu.be/Nt0TxbH8SEE

## Analises ##

Analise |  Link 
:---------------:|:--------------:
Memória | [Memória](https://github.com/ruybrito106/my-footprints/blob/master/memoria.md)
Testes | [Testes](https://github.com/ruybrito106/my-footprints/blob/master/testes.md)
CPU | [CPU](https://github.com/ruybrito106/my-footprints/blob/master/cpu.md)
Rede | [Rede](https://github.com/ruybrito106/my-footprints/blob/master/bandwidth.md)
Bateria | [Bateria](https://github.com/ruybrito106/my-footprints/blob/master/bateria.md)
Arquitetura | [Arquitetura](https://github.com/ruybrito106/my-footprints/blob/master/archcomponents.md)
