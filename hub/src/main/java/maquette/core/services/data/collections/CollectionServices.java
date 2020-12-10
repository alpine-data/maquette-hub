package maquette.core.services.data.collections;

import akka.Done;
import maquette.core.entities.data.collections.model.Collection;
import maquette.core.entities.data.collections.model.CollectionProperties;
import maquette.core.services.data.AccessRequestServices;
import maquette.core.services.data.MemberServices;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface CollectionServices extends MemberServices, AccessRequestServices {

   CompletionStage<CollectionProperties> create(
      User executor, String title, String name, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation);

   CompletionStage<Collection> get(User executor, String asset);

   CompletionStage<List<CollectionProperties>> list(User executor);

   CompletionStage<Done> remove(User executor, String asset);

   CompletionStage<Done> update(
      User executor, String name, String updatedName, String title, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation);

}
