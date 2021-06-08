import maquette.sdk.dsl.Maquette;

var data = List.of(/* ... */);

Maquette
   .create()
   .datasets("__ASSET__")
   .put(data);