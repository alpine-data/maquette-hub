package maquette.datashop.ports;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.common.Templates;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import maquette.datashop.configuration.DataShopConfiguration;
import maquette.datashop.configuration.DatabaseConfiguration;
import maquette.datashop.values.DataAssetProperties;
import maquette.datashop.values.DataAssetState;
import maquette.datashop.values.access.DataAssetMemberRole;
import maquette.datashop.values.access_requests.DataAccessRequestProperties;
import maquette.datashop.values.metadata.*;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@AllArgsConstructor(staticName = "apply")
public final class JdbcDataAssetRepository implements DataAssetsRepository {

    private final Jdbi jdbi;

    private final ObjectMapper om;

    private final JdbcMembersRepository<DataAssetMemberRole> membersRepository;

    public static JdbcDataAssetRepository apply(MaquetteRuntime runtime, DatabaseConfiguration config) {
        var jdbi = Jdbi.create(config.getConnection(), config.getUsername(), config.getPassword());
        var om = runtime.getObjectMapperFactory().createJsonMapper(true);
        var members = JdbcMembersRepository.apply(
            "data-assets", jdbi, om, DataAssetMemberRole::getValue, DataAssetMemberRole::valueOf);

        return apply(jdbi, om, members);
    }

    public static JdbcDataAssetRepository apply(MaquetteRuntime runtime) {
        return apply(runtime, DataShopConfiguration.apply().getDatabase());
    }

    @Override
    public CompletionStage<List<GrantedAuthorization<DataAssetMemberRole>>> findAllMembers(UID parent) {
        return membersRepository.findAllMembers(parent);
    }

    @Override
    public CompletionStage<List<GrantedAuthorization<DataAssetMemberRole>>> findMembersByRole(UID parent,
                                                                                              DataAssetMemberRole role) {
        return membersRepository.findMembersByRole(parent, role);
    }

    @Override
    public CompletionStage<Done> insertOrUpdateMember(UID parent, GrantedAuthorization<DataAssetMemberRole> member) {
        return membersRepository.insertOrUpdateMember(parent, member);
    }

    @Override
    public CompletionStage<Done> removeMember(UID parent, Authorization member) {
        return membersRepository.removeMember(parent, member);
    }

    @Override
    public CompletionStage<Optional<DataAssetProperties>> findDataAssetByName(String name) {
        var query = Templates.renderTemplateFromResources("db/sql/assets/find-data-asset-by-name.sql");

        var result = jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("name", name)
            .map(new DataAssetPropertiesMapper())
            .stream()
            .findFirst());

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Optional<DataAssetProperties>> findDataAssetById(UID id) {
        var query = Templates.renderTemplateFromResources("db/sql/assets/find-data-asset-by-id.sql");

        var result = jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("id", id.getValue())
            .map(new DataAssetPropertiesMapper())
            .stream()
            .findFirst());

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public <T> CompletionStage<Optional<T>> fetchCustomSettings(UID id, Class<T> expectedType) {
        var query = Templates.renderTemplateFromResources("db/sql/assets/fetch-custom-settings.sql");

        var result = jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("id", id.getValue())
            .map(new CustomObjectMapper<>(expectedType))
            .stream()
            .findFirst());

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public <T> CompletionStage<Optional<T>> fetchCustomProperties(UID id, Class<T> expectedType) {
        var query = Templates.renderTemplateFromResources("db/sql/assets/fetch-custom-properties.sql");

        var result = jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("id", id.getValue())
            .map(new CustomObjectMapper<>(expectedType))
            .stream()
            .findFirst());

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Done> insertOrUpdateDataAsset(DataAssetProperties updated) {
        var query = Templates.renderTemplateFromResources("db/sql/assets/upsert-asset.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("id", updated.getId().getValue())
            .bind("type", updated.getType())
            .bind("title", updated.getMetadata().getTitle())
            .bind("name", updated.getMetadata().getName())
            .bind("summary", updated.getMetadata().getSummary())
            .bind("visibility", updated.getMetadata().getVisibility().getValue())
            .bind("classification", updated.getMetadata().getClassification().getValue())
            .bind("personal_information", updated.getMetadata().getPersonalInformation().getValue())
            .bind("data_zone", updated.getMetadata().getZone().getValue())
            .bind("state", updated.getState().getValue())
            .bind("created_by", updated.getCreated().getBy())
            .bind("created_at", updated.getCreated().getAt())
            .bind("updated_by", updated.getUpdated().getBy())
            .bind("updated_at", updated.getUpdated().getAt())
            .bind("custom_properties", "{}")
            .bind("custom_settings", "{}")
            .execute());

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Done> insertOrUpdateCustomSettings(UID id, Object customSettings) {
        var query = Templates.renderTemplateFromResources("db/sql/assets/insert-custom-settings.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("id", id)
            .bind("custom_settings", Operators.suppressExceptions(() -> om.writeValueAsString(customSettings)))
            .execute());

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Done> insertOrUpdateCustomProperties(UID id, Object customProperties) {
        var query = Templates.renderTemplateFromResources("db/sql/assets/insert-custom-properties.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("id", id)
            .bind("custom_properties", Operators.suppressExceptions(() -> om.writeValueAsString(customProperties)))
            .execute());

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Stream<DataAssetProperties>> listDataAssets() {
        var query = Templates.renderTemplateFromResources("db/sql/assets/list-assets.sql");

        var result = jdbi.withHandle(handle -> handle
            .createQuery(query)
            .map(new DataAssetPropertiesMapper())
            .stream());

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Done> removeDataAssetById(UID id) {
        var query = Templates.renderTemplateFromResources("db/sql/assets/remove-asset.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("id", id.getValue())
            .execute());

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Optional<DataAccessRequestProperties>> findDataAccessRequestById(UID asset, UID request) {
        var query = Templates.renderTemplateFromResources("db/sql/assets/find-access-request-by-id.sql");

        var result = jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("id", request.getValue())
            .bind("asset", asset.getValue())
            .map(new AccessRequestPropertiesMapper())
            .stream()
            .findFirst());

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Done> insertOrUpdateDataAccessRequest(DataAccessRequestProperties request) {
        var query = Templates.renderTemplateFromResources("db/sql/assets/upsert-access-request.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("id", request.getId().getValue())
            .bind("asset", request.getAsset().getValue())
            .bind("workspace", request.getWorkspace().getValue())
            .bind("request", Operators.suppressExceptions(() -> om.writeValueAsString(request)))
            .execute());

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<List<DataAccessRequestProperties>> findDataAccessRequestsByWorkspace(UID workspace) {
        var query = Templates.renderTemplateFromResources("db/sql/assets/find-access-requests-by-workspace.sql");

        var result = jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("workspace", workspace.getValue())
            .map(new AccessRequestPropertiesMapper())
            .list());

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<List<DataAccessRequestProperties>> findDataAccessRequestsByAsset(UID asset) {
        var query = Templates.renderTemplateFromResources("db/sql/assets/find-access-requests-by-asset.sql");

        var result = jdbi.withHandle(handle -> handle
            .createQuery(query)
            .bind("asset", asset.getValue())
            .map(new AccessRequestPropertiesMapper())
            .list());

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<Done> removeDataAccessRequest(UID asset, UID id) {
        var query = Templates.renderTemplateFromResources("db/sql/assets/remove-access-request.sql");

        jdbi.withHandle(handle -> handle
            .createUpdate(query)
            .bind("id", id.getValue())
            .bind("asset", asset.getValue())
            .execute());

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    private static class DataAssetPropertiesMapper implements RowMapper<DataAssetProperties> {

        @Override
        public DataAssetProperties map(ResultSet rs, StatementContext ctx) throws SQLException {
            var visibility = DataVisibility.forValue(rs.getString("visibility"));
            var classification = DataClassification.forValue(rs.getString("classification"));
            var personalInformation = PersonalInformation.forValue(rs.getString("personal_information"));
            var dataZone = DataZone.valueOf(rs.getString("data_zone"));

            var metadata = DataAssetMetadata.apply(
                rs.getString("title"), rs.getString("name"), rs.getString("summary"),
                visibility, classification, personalInformation, dataZone);

            var created = ActionMetadata.apply(rs.getString("created_by"), rs.getDate("created_at").toInstant());
            var updated = ActionMetadata.apply(rs.getString("updated_by"), rs.getDate("updated_at").toInstant());

            return DataAssetProperties.apply(
                UID.apply(rs.getString("id")), rs.getString("type"), metadata,
                DataAssetState.valueOf(rs.getString("state")), created, updated);
        }

    }

    private class AccessRequestPropertiesMapper implements RowMapper<DataAccessRequestProperties> {

        @Override
        public DataAccessRequestProperties map(ResultSet rs, StatementContext ctx) {
            return Operators.suppressExceptions(() -> om.readValue(rs.getString("request"), DataAccessRequestProperties.class));
        }

    }

    private class CustomObjectMapper<T> implements RowMapper<T> {

        private final Class<T> expectedType;

        public CustomObjectMapper(Class<T> expectedType) {
            this.expectedType = expectedType;
        }

        @Override
        public T map(ResultSet rs, StatementContext ctx) {
            return Operators.suppressExceptions(() -> om.readValue(rs.getString("object"), expectedType));
        }
    }

}
