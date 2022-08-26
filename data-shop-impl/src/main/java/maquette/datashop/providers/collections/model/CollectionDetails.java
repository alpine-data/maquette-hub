package maquette.datashop.providers.collections.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;

import java.util.List;

@Value
@AllArgsConstructor()
public class CollectionDetails {

   private static final String FILES = "files";

   private static final String TAGS = "tags";

   FileEntry.Directory files;

   List<CollectionTag> tags;

   @JsonCreator
   public static CollectionDetails apply(
           @JsonProperty(FILES) FileEntry.Directory files,
           @JsonProperty(TAGS) List<CollectionTag> tags) {
      return new CollectionDetails(files, tags);
   }
}
