package maquette.asset_providers.datasets.services;

import akka.Done;
import maquette.asset_providers.datasets.model.CommittedRevision;
import maquette.asset_providers.datasets.model.DatasetVersion;
import maquette.asset_providers.datasets.model.Revision;
import maquette.core.values.UID;
import maquette.core.values.data.records.Records;
import maquette.core.values.user.User;
import org.apache.avro.Schema;

import java.util.concurrent.CompletionStage;

public interface DatasetServices {

   CompletionStage<CommittedRevision> commit(User executor, String dataset, UID revision, String message);

   CompletionStage<Revision> create(User executor, String dataset, Schema schema);

   CompletionStage<Records> download(User executor, String dataset, DatasetVersion version);

   CompletionStage<Records> download(User executor, String dataset);

   CompletionStage<Done> upload(User executor, String dataset, UID revision, Records records);

}
