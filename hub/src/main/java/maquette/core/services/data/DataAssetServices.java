package maquette.core.services.data;

import akka.Done;
import maquette.core.entities.data.DataAssetEntity;
import maquette.core.values.data.DataAsset;
import maquette.core.values.data.DataAssetProperties;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public interface DataAssetServices<P extends DataAssetProperties<P>, E extends DataAssetEntity<P>>
 extends MemberServices, AccessRequestServices {

   <T extends DataAsset<T>> CompletionStage<T> get(User executor, String asset, Function<E, CompletionStage<T>> mapEntityToAsset);

   CompletionStage<List<P>> list(User executor);

   CompletionStage<Done> remove(User executor, String asset);

}
