package maquette.datashop.providers.collections.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class CollectionDetails {

   FileEntry.Directory files;

   List<CollectionTag> tags;

}
