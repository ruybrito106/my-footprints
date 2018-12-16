# Testes #

Como boas práticas no projeto, optamos por adicionar testes unitários em classes chaves e usar a ferramenta *UIAutomator* (_higor substitua pelo correto_) para testes de interface.

## Unitários ##

A elaboração de testes unitários não foi tão simples, pois desde o começo não adotamos uma abordagem de injeção de dependência em nossos métodos, o que dificultou estes fossem autocontidos e fácilmente testáveis (sem usar bibliotecas de mock).

Por isso, as únicas classes para as quais elaboramos testes foram:
* DateHelper
* FriendSharedLocationParser
* LocationUpdate

## Interface ##

(_higor faz teu nome_)
