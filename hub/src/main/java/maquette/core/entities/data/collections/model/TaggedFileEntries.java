package maquette.core.entities.data.collections.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.ActionMetadata;

@Value
@AllArgsConstructor(staticName = "apply")
public class TaggedFileEntries {

   ActionMetadata created;

   String name;

   FileEntry.Directory content;

}
