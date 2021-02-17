package maquette.core.services.data.streams;

import akka.Done;
import maquette.core.entities.data.streams.model.Retention;
import maquette.core.entities.data.streams.model.Stream;
import maquette.core.entities.data.streams.model.StreamProperties;
import maquette.core.services.data.AccessRequestServices;
import maquette.core.services.data.MemberServices;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.DataZone;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;
import org.apache.avro.Schema;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface StreamServices extends MemberServices, AccessRequestServices {

   CompletionStage<StreamProperties> create(
      User executor, String title, String name, String summary, Retention retention, Schema schema,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation, DataZone zone,
      Authorization owner, Authorization steward);

   CompletionStage<Stream> get(User executor, String asset);

   CompletionStage<List<StreamProperties>> list(User executor);

   CompletionStage<Done> remove(User executor, String asset);

   CompletionStage<Done> update(
      User executor, String name, String updatedName, String title, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation, DataZone zone);

   CompletionStage<Done> updateProperties(User executor, String name, Retention retention, Schema schema);

}
