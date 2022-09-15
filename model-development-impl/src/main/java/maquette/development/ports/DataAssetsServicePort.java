package maquette.development.ports;

import com.fasterxml.jackson.databind.JsonNode;
import maquette.core.values.UID;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface DataAssetsServicePort {

    /**
     * Get data access requests which are related to a provided workspace. This will return any JSON description
     * of the access requests to display this information to users.
     *
     * @param workspace The unique id of the workspace.
     * @return The list of access requests.
     */
    CompletionStage<List<JsonNode>> findDataAccessRequestsByWorkspace(UID workspace);

    /**
     * Get data assets which are accessible by a workspace. The assets will be returned as JSON, only used for
     * displaying the information to the users.
     *
     * @param workspace The unique id of the workspace.
     * @return The list of data assets.
     */
    CompletionStage<List<JsonNode>> findDataAssetsByWorkspace(UID workspace);

    /**
     * Return details of a data asset. The details are returned as a JSON object and are used to display then to
     * a user only.
     *
     * @param id The unique id of the data asset.
     * @return The data asset
     */
    CompletionStage<JsonNode> getDataAssetEntity(UID id);

}
