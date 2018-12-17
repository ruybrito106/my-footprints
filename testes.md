# Testes #

Como boas práticas no projeto, optamos por adicionar testes unitários em classes chaves e usar a ferramenta *Espresso* para testes de interface.

### Unitários ###

A elaboração de testes unitários não foi tão simples, pois desde o começo não adotamos uma abordagem de injeção de dependência em nossos métodos, o que dificultou estes fossem autocontidos e fácilmente testáveis (sem usar bibliotecas de mock).

Por isso, as únicas classes para as quais elaboramos testes foram:
* DateHelper
* FriendSharedLocationParser
* LocationUpdate

### Interface ###

Para os testes de UI, utilizamos a biblioteca Espresso para simular o fluxo de interface do app. Incluímos testes para certificar de que o app estava abrindo corretamente, passando da splash screen e tela de login, e chegando até a home. Também testamos se o fluxo de menus da tela principal estava funcionando, simulando clicks em cada botão de navegação e verificando se houve uma troca de contexto como esperado.
