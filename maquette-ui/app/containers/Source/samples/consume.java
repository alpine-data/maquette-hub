import maquette.sdk.dsl.Maquette;

var data = Maquette
   .create()
   .source("__ASSET__")
   .read(JsonNode.class);