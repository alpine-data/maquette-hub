package maquette.datashop.providers.collections.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;

@Value
@AllArgsConstructor()
public class CollectionTag {

   private static final String CREATED = "created";

   private static final String NAME = "name";

   private static final String MESSAGE = "message";

   private static final String CONTENT = "content";

   ActionMetadata created;

   String name;

   String message;

   FileEntry.Directory content;

   @JsonCreator
   public static CollectionTag apply(
           @JsonProperty(CREATED) ActionMetadata created,
           @JsonProperty(NAME) String name,
           @JsonProperty(MESSAGE) String message,
           @JsonProperty(CONTENT) FileEntry.Directory content) {
      return new CollectionTag(created, name, message, content);
   }
}
