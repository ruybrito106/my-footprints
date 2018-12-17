# Rede #

### Possíveis gargalos ###

Inicialmente identificamos em quais momentos nosso app iria interagir com a rede. Como optamos por fazer o compartilhamento de dados de localização via SMS, nosso uso se resumiu a 2 casos:
- Login com firebase (API do Firebase)
- Download do maps (API do Google Maps)

Em ambos os casos, nos limitamos ao uso de bibliotecas que interagem com a rede de forma autônoma. E isso reduz nossa capacidade de otimizar o consumo de rede.

### Análise do AndroidProfiler ###

![Bandwidth Analysis](https://res.cloudinary.com/ufpe/image/upload/v1544988407/mf_bandwidth.png)

Observações:
- Tempo de uso do app: 2m12s
- O AndroidProfiler não funcionou com o login do Firebase (na sessão de login, o app dava crash), por isso ambos os picos acima são de carregamentos do Maps, no primeiro temos a tela principal e no segundo a funcionalidade de Friends.
- O segundo carregamento é muito mais leve, visto que boa parte do dado já estava em memória.

### Melhorias ###

Não tivemos condições de aplicar nenhuma melhoria, dado a limitações de dependências.
