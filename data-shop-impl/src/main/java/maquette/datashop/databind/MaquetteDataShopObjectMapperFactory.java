package maquette.datashop.databind;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.AllArgsConstructor;
import maquette.core.databind.ObjectMapperFactory;
import org.apache.avro.Schema;

@AllArgsConstructor(staticName = "apply")
public final class MaquetteDataShopObjectMapperFactory implements ObjectMapperFactory {

    ObjectMapperFactory delegate;

    @Override
    public ObjectMapper createJsonMapper(boolean pretty) {
        var om = delegate.createJsonMapper(pretty);

        var module = new SimpleModule();
        module.addSerializer(Schema.class, new SchemaSerializer());
        module.addDeserializer(Schema.class, new SchemaDeserializer());
        om.registerModule(module);

        return om;
    }

    @Override
    public ObjectMapper createYamlMapper() {
        return null;
    }

}
