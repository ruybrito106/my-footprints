# Bateria #

### Analise ###

Fizemos um teste com a versão beta do app instalado num aparelho de teste e obtivemos o seguinte resultado.

Datalhes do uso | Consumo de bateria
:--------------:|:--------------:
![Usage details](https://res.cloudinary.com/ufpe/image/upload/v1545007962/mf_battery_2.jpg) | ![Battery Usage](https://res.cloudinary.com/ufpe/image/upload/v1545007962/mf_battery_1.jpg)

Observações:
- Tempo considerado: aproximadamente 2 dias (desde último carregamento completo).
- Tivemos apenas 2% de uso de bateria.
- O consumo de bateria em background é apontado como superior ao consumo em foreground.

### Melhorias ###

Não conseguimos obter nenhum insight para melhorias no consumo de bateria baseado nessa análise acima. Como o consumo em background foi maior que em foreground, talvez uma ideia seria testar uma solução com _JobScheduler_ (pedindo dados de localização apenas em situações convenientes) ao invés do _LocationListener_ e fazer uma análise comparativa, no entanto não conseguimos fazer a tempo.
