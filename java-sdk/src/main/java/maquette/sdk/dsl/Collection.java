package maquette.sdk.dsl;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.jayway.jsonpath.JsonPath;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.common.Operators;
import maquette.sdk.commands.CreateCollectionTagCommand;
import maquette.sdk.commands.ListCollectionFilesCommand;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.apache.commons.io.FileUtils;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * DSL class which contains functions to read/ write data from Maquette Collections.
 */
@AllArgsConstructor(staticName = "apply")
public final class Collection {

   public static final String LATEST_TAG_ALIAS = "main";

   private final String name;

   private final MaquetteClient client;

   /**
    * Creates an Akka Streams flow to upload files to the collection.
    *
    * @param message The default message for uploaded files.
    * @param <T>     The specific type of the collection file.
    * @return The flow.
    */
   public <T extends CollectionFile> Flow<T, T, NotUsed> flow(String message) {
      return Flow.<T>create()
         .map(record -> {
            upload(record, message);
            return record;
         });
   }

   /**
    * Creates an Akka Streams sink to upload files to the collection.
    *
    * @param message The default message for the uploaded files.
    * @return The sink.
    */
   public Sink<CollectionFile, CompletionStage<Done>> sink(String message) {
      return sink(message, null);
   }

   /**
    * Creates an Akka Streams sink to upload files to the collection.
    *
    * @param message The default message for the uploaded files.
    * @param tag     A name for the tag to be created for the file set.
    * @return The sink.
    */
   public Sink<CollectionFile, CompletionStage<Done>> sink(String message, String tag) {
      return Flow.<CollectionFile>create()
         .via(flow(message))
         .toMat(Sink.ignore(), Keep.right())
         .mapMaterializedValue(done -> done.thenCompose(d -> {
            if (!Objects.isNull(tag)) {
               return tag(tag, message);
            } else {
               return CompletableFuture.completedFuture(Done.getInstance());
            }
         }));
   }

   /**
    * Creates an Akka Streams source to read latest files from the collection.
    *
    * @return The source.
    */
   public Source<CollectionFile, NotUsed> source() {
      return source(LATEST_TAG_ALIAS);
   }

   /**
    * Creates an Akka Streams source to read tagged files from the collection.
    *
    * @param tag The name of the tag.
    * @return The source.
    */
   public Source<CollectionFile, NotUsed> source(String tag) {
      return Source
         .fromCompletionStage(listFiles(tag))
         .mapConcat(files -> files)
         .map(file -> CollectionFiles.fromByteString(file, download(tag, file)));
   }

   /**
    * Lists all existing files in the collection.
    *
    * @return The file list.
    */
   public CompletionStage<List<String>> listFile() {
      return listFiles(LATEST_TAG_ALIAS);
   }

   /**
    * Lists all existing files in the collection tag.
    *
    * @param tag The name of the tag.
    * @return The file list.
    */
   public CompletionStage<List<String>> listFiles(String tag) {
      var result = client.executeCommand(ListCollectionFilesCommand.apply(name, tag), json -> JsonPath.<List<String>>read(json, "$.data"));
      return CompletableFuture.completedFuture(result);
   }

   /**
    * Creates a new tag in the collection.
    *
    * @param tag     The name of the tag.
    * @param message The message for the tag.
    * @return Done.
    */
   public CompletionStage<Done> tag(String tag, String message) {
      client.executeCommand(CreateCollectionTagCommand.apply(name, tag, message));
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   /**
    * Adds all files of a local directory to the collection.
    *
    * @param directory The local directory.
    * @param message   The message for the uploaded files.
    * @return Done.
    */
   public CompletionStage<Done> writeDirectory(Path directory, String message) {
      return writeDirectory(directory, message, directory.toAbsolutePath());
   }

   /**
    * Adds all files of a local directory to the collection.
    *
    * @param directory The local directory.
    * @param message   The message for the uploaded files.
    * @param basePath  The common base path which should be substituted from the absolute path of each file.
    *                  This must be a parent directory of the directory.
    * @return Done.
    */
   public CompletionStage<Done> writeDirectory(Path directory, String message, Path basePath) {
      Operators
         .suppressExceptions(() -> Files.walk(directory))
         .filter(Files::isRegularFile)
         .forEach(path -> upload(CollectionFiles.fromPath(path, basePath), message));

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   /**
    * Writes a single file to the collection.
    *
    * @param file    The local file.
    * @param message The message for the uploaded file.
    * @return Done.
    */
   public CompletionStage<Done> writeFile(Path file, String message) {
      return writeFile(file.toAbsolutePath(), message, file.getParent().toAbsolutePath());
   }

   /**
    * Writes a single file to the collection.
    *
    * @param file     The local file.
    * @param message  The message for the uploaded file.
    * @param basePath The basePath which should be substituted from the absolute path of the file.
    *                 This must be a parent directory of the file.
    * @return Done.
    */
   public CompletionStage<Done> writeFile(Path file, String message, Path basePath) {
      upload(CollectionFiles.fromPath(file, basePath), message);
      return CompletableFuture.completedFuture(Done.getInstance());
   }

   /**
    * Reads a file from the collection and stores it in the local file system.
    *
    * @param localFile The target file.
    * @param file      The name of the file to save from the collection.
    * @return Done.
    */
   public CompletionStage<Done> saveFile(Path localFile, String file) {
      return saveFile(localFile, file, LATEST_TAG_ALIAS);
   }

   /**
    * Reads a file from the collection and stores it in the local file system.
    *
    * @param localFile The target file.
    * @param file      The name of the file to save from the collection.
    * @param tag       The name of the tag to read the file from.
    * @return Done.
    */
   public CompletionStage<Done> saveFile(Path localFile, String file, String tag) {
      return readFile(file, tag)
         .thenApply(bs -> Operators.suppressExceptions(() -> {
            Files.createDirectories(localFile.getParent());

            try (var fc = new FileOutputStream(localFile.toFile()).getChannel()) {
               fc.write(bs.toByteBuffer());
            }

            return Done.getInstance();
         }));
   }

   /**
    * Reads all files of a collection and stores them in a local directory.
    *
    * @param localDirectory The target directory.
    * @return Done.
    */
   public CompletionStage<Done> saveFiles(Path localDirectory) {
      return saveFiles(localDirectory, LATEST_TAG_ALIAS);
   }

   /**
    * Reads all files of a collection tag and stores them in a local directory.
    *
    * @param localDirectory The target directory.
    * @param tag            The name of the tag.
    * @return Done.
    */
   public CompletionStage<Done> saveFiles(Path localDirectory, String tag) {
      Operators.suppressExceptions(() -> Files.createDirectories(localDirectory));

      return listFiles(tag)
         .thenAccept(files -> files.forEach(file -> saveFile(localDirectory.resolve(file), file, tag)))
         .thenApply(i -> Done.getInstance());
   }

   /**
    * Reads a single file from a collection.
    *
    * @param file The name of the file.
    * @return The file.
    */
   public CompletionStage<ByteString> readFile(String file) {
      return readFile(file, LATEST_TAG_ALIAS);
   }

   /**
    * Reads a single file from a collection tag.
    *
    * @param file The name of the file.
    * @param tag  The name of the tag.
    * @return The file.
    */
   public CompletionStage<ByteString> readFile(String file, String tag) {
      var bs = download(tag, file);
      return CompletableFuture.completedFuture(bs);
   }

   /**
    * Reads all files form a collection.
    *
    * @return All files.
    */
   public CompletionStage<Stream<CollectionFile>> readFiles() {
      return readFiles(LATEST_TAG_ALIAS);
   }

   /**
    * Reads all files from a collection tag.
    *
    * @param tag The name of the tag.
    * @return All files.
    */
   public CompletionStage<Stream<CollectionFile>> readFiles(String tag) {
      return listFiles(tag).thenApply(files -> files
         .stream()
         .map(file -> CollectionFiles.fromByteString(file, download(tag, file))));
   }

   private void upload(CollectionFile file, String message) {
      var requestBody = new MultipartBody.Builder()
         .setType(MultipartBody.FORM)
         .addFormDataPart(
            "file", "file",
            RequestBody.create(file.getFile().toArray(), MediaType.parse("application/octet-stream")))
         .addFormDataPart("message", file.getMessage().orElse(message))
         .addFormDataPart("name", file.getName())
         .build();

      var request = client
         .createRequestFor("/api/data/collections/%s", this.name)
         .post(requestBody)
         .build();

      client.executeRequest(request);
   }

   private ByteString download(String tag, String file) {
      var request = client
         .createRequestFor("/api/data/collections/%s/tags/%s/%s", name, tag, file)
         .get()
         .build();

      return client.executeRequest(request, res -> ByteString.fromArray(res.bytes()));
   }

   /**
    * Simple interface to bundle information from a file stored in a collection.
    */
   public interface CollectionFile {

      /**
       * The name of the file (may include path information e.g. dir/some-file.ext)
       *
       * @return the name.
       */
      String getName();

      /**
       * The byte representation of the file.
       *
       * @return The file's data.
       */
      ByteString getFile();

      /**
       * When uploading files a separate message might be supplied for each file.
       *
       * @return The message.
       */
      Optional<String> getMessage();

      /**
       * Adds a message to a file.
       *
       * @param message The message.
       * @return The updated collection file.
       */
      CollectionFile withMessage(String message);

   }

   /**
    * Contains factory methods for {@link CollectionFile}
    */
   public final static class CollectionFiles {

      private CollectionFiles() {

      }

      /**
       * Creates a collection file form a {@link ByteString}.
       *
       * @param name The name of the file.
       * @param file The data of the file.
       * @return A new {@link CollectionFile}
       */
      public static CollectionFile fromByteString(String name, ByteString file) {
         return ByteStringCollectionFile.apply(name, file, null);
      }

      /**
       * Creates a new collection file from a local file.
       *
       * @param file The file.
       * @return A new {@link CollectionFile}
       */
      public static CollectionFile fromPath(Path file) {
         return fromPath(file, file.getParent());
      }

      /**
       * Creates a new collection file from a local file.
       *
       * @param file     The local file.
       * @param basePath A parent directory of the file. The relative path from the basePath to the file will be used as a name for the file.
       * @return A new {@link CollectionFile}
       */
      public static CollectionFile fromPath(Path file, Path basePath) {
         return PathCollectionFile.apply(basePath, file, null);
      }

      /**
       * Allows to add a pass through for Akka Streams flows to an collection ile.
       *
       * @param pt The pass through value.
       * @param file The actual collection file.
       * @param <T> The type of the pass through.
       * @return A new {@link CollectionFile}
       */
      public static <T> CollectionFileWithPassThrough<T> withPassThrough(T pt, CollectionFile file) {
         return CollectionFileWithPassThrough.apply(pt, file, null);
      }

   }

   @AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
   public static class CollectionFileWithPassThrough<T> implements CollectionFile {

      T passThrough;

      CollectionFile file;

      String message;

      @Override
      public String getName() {
         return file.getName();
      }

      @Override
      public ByteString getFile() {
         return file.getFile();
      }

      @Override
      public Optional<String> getMessage() {
         return Optional.ofNullable(message);
      }

      @Override
      public CollectionFileWithPassThrough<T> withMessage(String message) {
         return apply(passThrough, file, message);
      }

      public T getPassThrough() {
         return passThrough;
      }

   }

   @Value
   @AllArgsConstructor(staticName = "apply")
   private static class ByteStringCollectionFile implements CollectionFile {

      String name;

      ByteString file;

      String message;

      @Override
      public Optional<String> getMessage() {
         return Optional.ofNullable(message);
      }

      @Override
      public CollectionFile withMessage(String message) {
         return null;
      }
   }

   @AllArgsConstructor(staticName = "apply")
   private static class PathCollectionFile implements CollectionFile {

      Path basePath;

      Path file;

      String message;

      @Override
      public String getName() {
         return basePath.relativize(file).toString();
      }

      @Override
      public ByteString getFile() {
         var bytes = Operators.suppressExceptions(() -> FileUtils.readFileToByteArray(file.toFile()));
         return ByteString.fromArray(bytes);
      }

      @Override
      public Optional<String> getMessage() {
         return Optional.ofNullable(message);
      }

      @Override
      public CollectionFile withMessage(String message) {
         return null;
      }
   }

}
