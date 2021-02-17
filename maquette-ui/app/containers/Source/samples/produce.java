import maquette.sdk.dsl.Maquette;

var data = List.of(/* ... */);

Maquette
   .datasets("__ASSET__")
   .put(data);