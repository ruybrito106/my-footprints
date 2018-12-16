# CPU #

### Possíveis gargalos ###

#### Alto custo de funções relacionadas a mapas (operações com Floating Point) ####

O que nos preocupou inicialmente sobre o uso de CPU foi que teríamos uma aplicação fazendo uso intensivo de funções relacionadas a mapas (Haversine, Geohash conversion...) e isso nos parecia um gargalo de uso de CPU.
Por isso, desde o começo fizemos o máximo pra implementar do nosso lado essas funções e garantir que teríamos controle sobre a performance delas (evitando usar dependências não confiáveis).

Implementamos a Haversine:

``` kotlin
private fun distance(lat1: Double, lat2: Double, lon1: Double, lon2: Double): Double {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLong = Math.toRadians(lon2 - lon1)

    var lat1 = Math.toRadians(lat1)
    var lat2 = Math.toRadians(lat2)

    val hLat = Math.pow(Math.sin(dLat / 2), 2.0)
    val hLng = Math.pow(Math.sin(dLong / 2), 2.0)

    val a = hLat + Math.cos(lat1) * Math.cos(lat2) * hLng
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    return EARTH_RADIUS.toDouble() * c * 1000.0
}
```

Não implementamos conversão Geohash <-> (lat, lng) devido a complexidade da operação. Optamos por confiar na [Geohash-Java](https://github.com/kungfoo/geohash-java/blob/master/src/main/java/ch/hsr/geohash/GeoHash.java).

A primeira, implementada do nosso lado, é chamada sempre que o usuário solicita que a localização seja atualizada (ou a MainActivity é criada). Enquanto que a segunda é chamada apenas quando o usuário compartilha localização ou recebe de algum amigo.

#### Reprocessamento desnecessário ####

Notamos que sempre que o usuário clica em *Update* nós fazemos uma chamada ao banco pra recuperar todos os LocationUpdate num intervalo de dias e atualizamos a view. Mas isso só é necessário se existir um novo LocationUpdate. A alternativa que implementamos para evitar esse gargalo está descrita na seção de melhorias.

### Análise AndroidProfiler ###

![CPU Analysis](https://res.cloudinary.com/ufpe/image/upload/v1544988407/mf_cpu.png)

Observações:
* Tempo de uso do app: 2m12s
* O AndroidProfiler mostrou que o uso de CPU da aplicação em si durante o uso não chegou a 30% da capacidade do device.

### Melhorias ###

Sobre o reprocessamento desnecessário, optamos por implementar uma "cache" em memória (penalizamos memória por economia de CPU).

``` kotlin
private var locationUpdateCachedCount: Int = 0

...
val pathButton = findViewById<View>(R.id.findRoute) as Button
pathButton.setOnClickListener { 
  val path = getLocationUpdatesByTimeRange(startDate, endDate)
  if (path.size != locationUpdateCachedCount) { 
    updateMap(false)
  }
}
```

Não foi necessário considerar período na "cache" dado que isso já está embutido na lógica da contagem. Voltamos a analisar o AndroidProfiler e não encontramos diferenças significativas.
