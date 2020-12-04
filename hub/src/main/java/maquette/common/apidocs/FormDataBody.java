package maquette.common.apidocs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.commons.compress.utils.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FormDataBody implements Body {

   @JsonProperty("formdata")
   List<Field> formData;

   public static FormDataBody apply(Field ...fields) {
      return new FormDataBody(Lists.newArrayList(Arrays.stream(fields).iterator()));
   }

   @Override
   public Map<String, Object> getOptions() {
      Map<String, Object> map = Maps.newHashMap();
      map.put("formdata", new Object());
      return map;
   }

   public FormDataBody withField(Field field) {
      formData.add(field);
      return this;
   }

}
