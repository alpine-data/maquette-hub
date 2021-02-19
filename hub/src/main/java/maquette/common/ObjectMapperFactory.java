package maquette.common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.scala.DefaultScalaModule;
import org.apache.avro.Schema;

public final class ObjectMapperFactory {

    private ObjectMapperFactory() {

    }

    public static ObjectMapperFactory apply() {
        return new ObjectMapperFactory();
    }

    public ObjectMapper create(boolean pretty) {
        ObjectMapper om = new ObjectMapper();
        configureMapper(om, pretty);
        return om;
    }

    public ObjectMapper create() {
        return create(false);
    }

    public ObjectMapper createYaml() {
        var jf = new YAMLFactory();
        var om = new ObjectMapper(jf);

        jf.disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID);
        configureMapper(om, false);

        return om;
    }

    private void configureMapper(ObjectMapper om, boolean pretty) {
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.registerModule(new JavaTimeModule());
        om.registerModule(new Jdk8Module());
        om.registerModule(new DefaultScalaModule());

        om.getSerializationConfig()
           .getDefaultVisibilityChecker()
           .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
           .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
           .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
           .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
           .withCreatorVisibility(JsonAutoDetect.Visibility.ANY);

        om.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        om.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        om.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        om.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        om.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY);

        SimpleModule module = new SimpleModule();
        module.addSerializer(Schema.class, new SchemaSerializer());
        module.addDeserializer(Schema.class, new SchemaDeserializer());
        om.registerModule(module);

        if (pretty) {
            om.enable(SerializationFeature.INDENT_OUTPUT);
        }

        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

}
