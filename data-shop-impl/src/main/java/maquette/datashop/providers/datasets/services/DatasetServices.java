package maquette.datashop.providers.datasets.services;

import akka.Done;
import maquette.core.values.UID;
import maquette.core.values.user.User;
import maquette.datashop.providers.datasets.model.CommittedRevision;
import maquette.datashop.providers.datasets.model.DatasetVersion;
import maquette.datashop.providers.datasets.model.Revision;
import maquette.datashop.providers.datasets.records.Records;
import org.apache.avro.Schema;

import java.util.concurrent.CompletionStage;

public interface DatasetServices {

   CompletionStage<Done> analyze(User executor, String dataset, DatasetVersion version);

   CompletionStage<CommittedRevision> commit(User executor, String dataset, UID revision, String message);

   CompletionStage<Revision> create(User executor, String dataset, Schema schema);

   CompletionStage<Records> download(User executor, String dataset, DatasetVersion version);

   CompletionStage<Records> download(User executor, String dataset);

   CompletionStage<Done> upload(User executor, String dataset, UID revision, Records records);

}
