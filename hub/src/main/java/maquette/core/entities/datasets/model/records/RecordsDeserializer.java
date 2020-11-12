package maquette.core.entities.datasets.model.records;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.avro.util.ByteBufferOutputStream;

import java.io.IOException;

public final class RecordsDeserializer extends StdDeserializer<Records> {

    private RecordsDeserializer() {
        super(Records.class);
    }

    @Override
    public Records deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ByteBufferOutputStream os = new ByteBufferOutputStream();
        p.readBinaryValue(os);
        return Records.fromByteBuffers(os.getBufferList());
    }

}
