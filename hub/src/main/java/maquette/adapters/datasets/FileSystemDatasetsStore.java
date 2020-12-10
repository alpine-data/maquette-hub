package maquette.adapters.datasets;

import akka.Done;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.data.datasets.model.records.Records;
import maquette.core.ports.RecordsStore;
import org.apache.avro.generic.GenericData;
import org.apache.commons.io.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class FileSystemDatasetsStore implements RecordsStore {

   private final FileSystemDatasetsStoreConfiguration config;

   @Override
   public CompletionStage<Done> append(String key, Records records) {
      Operators.suppressExceptions(() -> {
         Path versionDirectory = config.getDirectory().resolve(key);
         Files.createDirectories(versionDirectory);

         ArrayList<Path> existing = Lists.newArrayList(Files.newDirectoryStream(versionDirectory).iterator());
         records.toFile(versionDirectory.resolve("records-" + existing.size() + ".avro"));
      });

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Done> clear(String key) {
      Operators.suppressExceptions(() -> {
         Path versionDirectory = config.getDirectory().resolve(key);
         FileUtils.deleteDirectory(versionDirectory.toFile());
      });

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Records> get(String key) {
      return Operators.suppressExceptions(() -> {
         Path versionDirectory = config.getDirectory().resolve(key);

         if (!Files.exists(versionDirectory)) {
            return CompletableFuture.completedFuture(Records.empty());
         } else {
            ArrayList<Path> existing = Lists.newArrayList(Files.newDirectoryStream(versionDirectory).iterator());
            List<GenericData.Record> records = Lists.newArrayList();

            for (Path path : existing) {
               Records r = Records.fromFile(path);
               records.addAll(r.getRecords());
            }

            return CompletableFuture.completedFuture(Records.fromRecords(records));
         }
      });
   }

}
