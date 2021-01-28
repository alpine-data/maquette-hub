package maquette.streams.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJackson;
import lombok.AllArgsConstructor;
import maquette.streams.common.Operators;
import maquette.streams.common.databind.ObjectMapperFactory;
import maquette.streams.core.config.ApplicationConfiguration;
import maquette.streams.core.entities.topic.TopicEntities;
import maquette.streams.core.entities.topic.TopicProperties;
import maquette.streams.core.entities.topic.requests.*;
import maquette.streams.core.ports.RecordsRepository;
import maquette.streams.core.ports.TopicsRepository;
import maquette.streams.core.services.TopicService;
import org.apache.commons.io.FileUtils;

import java.nio.file.Files;
import java.util.Objects;

@AllArgsConstructor(staticName = "apply")
public final class CoreApplication {

   private final ApplicationConfiguration configuration;

   private final TopicService topics;

   public static CoreApplication apply(ApplicationConfiguration config, TopicsRepository topics, RecordsRepository records) {
      var topicEntities = TopicEntities.apply(topics, records);
      var services = TopicService.apply(topicEntities);

      return apply(config, services);
   }

   public void run() {
      JavalinJackson.configure(ObjectMapperFactory.apply().create(true));

      Javalin
         .create(config -> {
            config.showJavalinBanner = false;
            // maybe more ...
         })
         .get("/topics", ctx -> {
            var result = topics.getTopics().toCompletableFuture();
            ctx.json(result);
         })
         .put("/topics", ctx -> {
            var request = ctx.bodyAsClass(TopicProperties.class);
            var result = topics
               .createTopic(request)
               .thenApply(d -> "ok")
               .toCompletableFuture();

            ctx.result(result);
         })
         .post("/topics/:topic", ctx -> {
            var contentType = ctx.req.getContentType();
            var topic = ctx.pathParam("topic");

            if (!Objects.isNull(contentType) && contentType.contains("multipart/form-data")) {
               var uploaded = Objects.requireNonNull(ctx.uploadedFile("file"));
               var file = Files.createTempFile("maquette", "upload");
               FileUtils.copyInputStreamToFile(uploaded.getContent(), file.toFile());

               var result = topics
                  .append(topic, file)
                  .thenApply(done -> {
                     Operators.suppressExceptions(() -> Files.deleteIfExists(file));
                     return "ok";
                  })
                  .toCompletableFuture();

               ctx.result(result);
            } else {
               var request = ctx.bodyAsClass(AppendRequest.class);
               var result = topics
                  .append(topic, request)
                  .thenApply(d -> "ok")
                  .toCompletableFuture();
               ctx.result(result);
            }
         })
         .post("/topics/:topic/read", ctx -> {
            var request = ctx.bodyAsClass(ReadRequest.class);
            var topic = ctx.pathParam("topic");
            var acceptRaw = ctx.header("Accept");
            var accept = acceptRaw != null ? acceptRaw : "application/json";

            if (accept.equals("application/octet-stream")) {
               var result = topics
                  .readAvro(topic, request)
                  .thenApply(is -> {
                     ctx.header("Content-Disposition", "attachment; filename=records.avro");
                     ctx.header("Content-Type", "application/octet-stream");
                     return is;
                  })
                  .toCompletableFuture();

               ctx.result(result);
            } else {
               var result = topics
                  .read(topic, request)
                  .thenApply(RecordsResult::apply)
                  .toCompletableFuture();

               ctx.json(result);
            }
         })
         .post("/topics/:topic/poll", ctx -> {
            var request = ctx.bodyAsClass(PollRequest.class);
            var topic = ctx.pathParam("topic");

            var result = topics
               .poll(topic, request)
               .thenApply(RecordsResult::apply)
               .toCompletableFuture();

            ctx.json(result);
         })
         .post("/topics/:topic/commit", ctx -> {
            var topic = ctx.pathParam("topic");
            var request = ctx.bodyAsClass(CommitRequest.class);
            var result = topics
               .commit(topic, request)
               .thenApply(done -> "ok")
               .toCompletableFuture();

            ctx.result(result);
         })
         .start(configuration.getHost(), configuration.getPort());
   }

}
