package maquette.core.entities.data.collections.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;

@Value
@AllArgsConstructor(staticName = "apply")
public class CollectionTag {

   ActionMetadata created;

   String name;

   String message;

   FileEntry.Directory content;

}
