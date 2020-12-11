package maquette.core.services.data.datasources;

import akka.Done;
import maquette.core.entities.data.datasets.model.records.Records;
import maquette.core.entities.data.datasources.model.*;
import maquette.core.services.data.AccessRequestServices;
import maquette.core.services.data.MemberServices;
import maquette.core.values.data.DataClassification;
import maquette.core.values.data.DataVisibility;
import maquette.core.values.data.PersonalInformation;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface DataSourceServices extends MemberServices, AccessRequestServices {

   CompletionStage<DataSourceProperties> create(
      User executor, String title, String name, String summary,
      DataSourceDatabaseProperties properties, DataSourceAccessType type,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation);

   CompletionStage<Records> download(User executor, String dataSource);

   CompletionStage<DataSource> get(User executor, String dataSource);

   CompletionStage<List<DataSourceProperties>> list(User executor);

   CompletionStage<Done> remove(User executor, String dataSource);

   CompletionStage<Done> update(
      User executor, String name, String updatedName, String title, String summary,
      DataVisibility visibility, DataClassification classification, PersonalInformation personalInformation);

   CompletionStage<Done> updateDatabaseProperties(User executor, String dataSource, DataSourceDriver driver, String connection, String username, String password, String query, DataSourceAccessType accessType);

   CompletionStage<ConnectionTestResult> test(User executor, DataSourceDriver driver, String connection, String username, String password, String query);

}
