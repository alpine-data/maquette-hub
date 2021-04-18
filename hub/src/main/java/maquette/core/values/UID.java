package maquette.core.values;

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
import maquette.common.Operators;
import maquette.core.values.exceptions.DomainException;

import java.io.IOException;
import java.util.UUID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonSerialize(using = UID.Serializer.class)
@JsonDeserialize(using = UID.Deserializer.class)
public class UID {

   String value;

   public static UID apply(String value) {
      if (value == null || value.length() == 0) {
         throw InvalidUIDException.apply(value);
      }

      return new UID(value);
   }

   public static UID apply() {
      return apply(Operators.randomHash());
   }

   public static UID apply(int length) {
      String random = UUID.randomUUID().toString().replace("-", "").substring(0, length);
      return apply(random);
   }

   public String toString() {
      return value;
   }

   public UID withChildren(String other) {
      return UID.apply(value + "/" + other);
   }

   public UID withChildren(UID other) {
      return withChildren(other.getValue());
   }

   public UID withParent(String other) {
      return UID.apply(other + "/" + value);
   }

   public UID withParent(UID other) {
      return withChildren(other.getValue());
   }

   public static class Serializer extends StdSerializer<UID> {

      private Serializer() {
         super(UID.class);
      }

      @Override
      public void serialize(UID value, JsonGenerator gen, SerializerProvider provider) throws IOException {
         gen.writeString(value.getValue());
      }

   }

   public static class Deserializer extends StdDeserializer<UID> {

      private Deserializer() {
         super(UID.class);
      }

      @Override
      public UID deserialize(JsonParser p, DeserializationContext ignore) throws IOException {
         return UID.apply(p.readValueAs(String.class));
      }

   }

   public static class InvalidUIDException extends RuntimeException implements DomainException {

      private InvalidUIDException(String message) {
         super(message);
      }

      public static InvalidUIDException apply(String uid) {
         var msg = String.format("The provided id `%s` is not valid.", uid);
         return new InvalidUIDException(msg);
      }

   }

}
