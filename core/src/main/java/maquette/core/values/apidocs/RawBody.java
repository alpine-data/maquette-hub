package maquette.core.values.apidocs;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Map;

@Value
@AllArgsConstructor(staticName = "apply")
public class RawBody implements Body {

    String raw;

    @Override
    public Map<String, Object> getOptions() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("raw", new Object());
        return map;
    }

}
