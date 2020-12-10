package maquette.core.services.data.datasets;

import akka.Done;
import maquette.core.entities.data.datasets.model.Dataset;
import maquette.core.entities.data.datasets.model.DatasetProperties;
import maquette.core.entities.data.datasets.model.DatasetVersion;
import maquette.core.entities.data.datasets.model.records.Records;
import maquette.core.entities.data.datasets.model.revisions.CommittedRevision;
import maquette.core.entities.data.datasets.model.revisions.Revision;
import maquette.core.services.data.AccessRequestServices;
import maquette.core.services.data.MemberServices;
import maquette.core.values.UID;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;
import org.apache.avro.Schema;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface DatasetServices extends MemberServices, AccessRequestServices  {

   /*
    * General
    */
   CompletionStage<DatasetProperties> create(
      User executor, String title, String name, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation);

   CompletionStage<Done> remove(User executor, String dataset);

   CompletionStage<Dataset> get(User executor, String dataset);

   CompletionStage<List<DatasetProperties>> list(User executor);

   CompletionStage<Done> update(
      User executor, String name, String updatedName, String title, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation);

   /*
    * Data Management
    */
   CompletionStage<CommittedRevision> commitRevision(User executor, String dataset, UID revision, String message);

   CompletionStage<Revision> createRevision(User executor, String dataset, Schema schema);

   CompletionStage<Records> download(User executor, String dataset, DatasetVersion version);

   CompletionStage<Records> download(User executor, String dataset);

   CompletionStage<Done> upload(User executor, String dataset, UID revision, Records records);

}
