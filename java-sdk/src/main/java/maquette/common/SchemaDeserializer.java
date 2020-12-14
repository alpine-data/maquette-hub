package maquette.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.avro.Schema;

import java.io.IOException;

public final class SchemaDeserializer extends StdDeserializer<Schema> {

    public SchemaDeserializer() {
        super(Schema.class);
    }


    @Override
    public Schema deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        var s = p.getCodec().readTree(p).toString();

        if (s == null || s.length() == 0) {
            return null;
        } else {
            return new Schema.Parser().setValidate(true).parse(s);
        }
    }

}
