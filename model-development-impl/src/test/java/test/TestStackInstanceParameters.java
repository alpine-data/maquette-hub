package test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import maquette.core.databind.DefaultObjectMapperFactory;
import maquette.development.values.stacks.StackInstanceParameters;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestStackInstanceParameters {

    @Test
    public void test() throws JsonProcessingException {
        var om = DefaultObjectMapperFactory
            .apply()
            .createJsonMapper(true);
        var params = Maps.<String, String>newHashMap();

        params.put("foo", "bar");
        params.put("lorem", "ipsum");

        var parameters = StackInstanceParameters.encodeAndCreate("http://foo.de", "test", params);
        var json = om.writeValueAsString(parameters);

        var parametersParsed = om.readValue(json, StackInstanceParameters.class);
        System.out.println(json);

        assertFalse(json.contains("bar"));
        assertFalse(json.contains("ipsum"));
        assertEquals(parameters, parametersParsed);
    }

}
