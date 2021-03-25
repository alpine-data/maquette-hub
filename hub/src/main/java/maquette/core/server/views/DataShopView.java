package maquette.core.server.views;

import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.entities.data.model.DataAssetProperties;
import maquette.core.server.CommandResult;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class DataShopView implements CommandResult {

   List<DataAssetProperties> userAssets;

   List<DataAssetProperties> allAssets;

}
