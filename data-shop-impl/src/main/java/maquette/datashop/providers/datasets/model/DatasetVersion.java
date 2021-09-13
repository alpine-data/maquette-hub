package maquette.datashop.providers.datasets.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.datashop.providers.datasets.exceptions.InvalidVersionException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonSerialize(using = DatasetVersion.Serializer.class)
@JsonDeserialize(using = DatasetVersion.Deserializer.class)
public class DatasetVersion implements Comparable<DatasetVersion> {

   private static final Pattern PATCH_VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)$");
   private static final Pattern MINOR_VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)$");
   private static final Pattern MAJOR_VERSION_PATTERN = Pattern.compile("(\\d+)$");

   int major;

   int minor;

   int patch;

   public static DatasetVersion apply(int major, int minor, int patch) {
      if (major < 1 || minor < 0 || patch < 0) {
         throw InvalidVersionException.apply(String.format("%d.%d.%d", major, minor, patch));
      }

      return new DatasetVersion(major, minor, patch);
   }

   public static DatasetVersion apply(String s) {
      final Matcher patchMatcher = PATCH_VERSION_PATTERN.matcher(s);
      final Matcher minorMatcher = MINOR_VERSION_PATTERN.matcher(s);
      final Matcher majorMatcher = MAJOR_VERSION_PATTERN.matcher(s);

      if (patchMatcher.find()) {
         int major = Integer.parseInt(patchMatcher.group(1));
         int minor = Integer.parseInt(patchMatcher.group(2));
         int patch = Integer.parseInt(patchMatcher.group(3));

         return apply(major, minor, patch);
      } else if (minorMatcher.find()) {
         int major = Integer.parseInt(minorMatcher.group(1));
         int minor = Integer.parseInt(minorMatcher.group(2));

         return apply(major, minor, 0);
      } else if (majorMatcher.find()) {
         int major = Integer.parseInt(majorMatcher.group(1));

         return apply(major, 0, 0);
      } else {
         throw InvalidVersionException.apply(s);
      }
   }

   @Override
   public int compareTo(DatasetVersion o) {
      int major = Integer.compare(getMajor(), o.major);
      int minor = Integer.compare(getMinor(), o.minor);
      int patch = Integer.compare(getPatch(), o.patch);

      if (major == 0 && minor == 0) {
         return patch;
      } else if (major == 0) {
         return minor;
      } else {
         return major;
      }
   }

   public String toString() {
      return String.format("%d.%d.%d", major, minor, patch);
   }

   public static class Serializer extends StdSerializer<DatasetVersion> {

      private Serializer() {
         super(DatasetVersion.class);
      }

      @Override
      public void serialize(DatasetVersion value, JsonGenerator gen, SerializerProvider provider) throws IOException {
         gen.writeString(value.toString());
      }

   }

   public static class Deserializer extends StdDeserializer<DatasetVersion> {

      private Deserializer() {
         super(DatasetVersion.class);
      }

      @Override
      public DatasetVersion deserialize(JsonParser p, DeserializationContext ignore) throws IOException {
         return DatasetVersion.apply(p.readValueAs(String.class));
      }

   }

}
