package maquette.core.services.data;

import akka.Done;
import maquette.core.entities.data.assets.DataAssetEntity;
import maquette.core.entities.data.datasets.model.tasks.Task;
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

   CompletionStage<Done> approve(User executor, String asset);

   CompletionStage<Done> deprecate(User executor, String asset, boolean deprecate);

   CompletionStage<List<Task>> getOpenTasks(User executor, String asset);

}
