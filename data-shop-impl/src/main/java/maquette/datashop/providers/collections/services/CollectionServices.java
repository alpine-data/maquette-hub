package maquette.datashop.providers.collections.services;

import akka.Done;
import maquette.core.values.binary.BinaryObject;
import maquette.core.values.user.User;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface CollectionServices {

   CompletionStage<List<String>> listFiles(User executor, String collection);

   CompletionStage<List<String>> listFiles(User executor, String collection, String tag);

   CompletionStage<Done> put(User executor, String collection, BinaryObject data, String file, String message);

   CompletionStage<Done> putAll(User executor, String collection, BinaryObject data, String basePath, String message);

   CompletionStage<BinaryObject> readAll(User executor, String collection);

   CompletionStage<BinaryObject> readAll(User executor, String collection, String tag);

   CompletionStage<BinaryObject> read(User executor, String collection, String file);

   CompletionStage<BinaryObject> read(User executor, String collection, String tag, String file);

   CompletionStage<Done> remove(User executor, String collection, String file);

   CompletionStage<Done> tag(User executor, String collection, String tag, String message);

}
