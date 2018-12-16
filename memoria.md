# Memória #

### Possíveis gargalos ###

Inicialmente tinhamos a noção de que o uso intensivo de gráficos ia ser o nosso maior gargalo de memória, visto que a biblioteca do Google Maps coloca boa parte dos dados em memória para se aproveitar de Data Locality. Conseguimos confirmar essa teoria através do Android Profiler. 

### Análise AndroidProfiler ###

![Memory Analysis](https://res.cloudinary.com/ufpe/image/upload/v1544988408/mf_memory.png)

Observações:
* Tempo de uso do app: 2m12s
* O pico no uso de memória do device, foi cerca de 220Mb
* Quando o uso de memória se estabilizou (app idle), tivemos cerca de 31.3% da memória usada por Graphics, e apenas 15.6% usado pelo código em si.

### Melhorias ###

A quantidade irrisória de memória usada aplicação (código) foi um indicativo de que mudanças na lógica da aplicação a fim de reduzir o uso de memória seriam pouco eficientes.
