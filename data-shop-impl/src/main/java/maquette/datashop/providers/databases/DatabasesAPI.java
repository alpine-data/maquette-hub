package maquette.datashop.providers.databases;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import lombok.AllArgsConstructor;
import maquette.core.common.DeleteOnCloseFileInputStream;
import maquette.core.common.Operators;
import maquette.core.values.user.User;

import java.nio.file.Files;
import java.util.Objects;

@AllArgsConstructor(staticName = "apply")
public class DatabasesAPI {

    private final Databases databases;

    public Handler getProfile() {
        var docs = OpenApiBuilder
            .document()
            .operation(op -> {
                op.summary("Get Data Profile HTML Page of database.");
                op.description("Downloads from a revision of a database.");
                op.addTagsItem("Data Assets");
            })
            .pathParam("database", String.class, p -> p.description("The name of the database"))
            .json("200", String.class);

        return OpenApiBuilder.documented(docs, ctx -> {
            var user = (User) Objects.requireNonNull(ctx.attribute("user"));
            var database = ctx.pathParam("database");
            var queryId = ctx.pathParam("query");

            var result = databases
                .getServices()
                .getAnalysisResult(user, database)
                .thenApply(analysisResult -> {
                    if (analysisResult.isPresent()) {
                        return analysisResult.get().getQueryById(queryId).getProfile();
                    } else {
                        return "No profiling data available";
                    }
                })
                .toCompletableFuture();

            ctx.header("Content-Type", "text/html");
            ctx.result(result);
        });
    }

    public Handler download() {
        var docs = OpenApiBuilder
            .document()
            .operation(op -> {
                op.summary("Download Database Data");
                op.description("Downloads data from a database's defined query.");
                op.addTagsItem("Databases");
            })
            .pathParam("database", String.class, p -> p.description("The name of the database"))
            .json("200", String.class);

        return OpenApiBuilder.documented(docs, ctx -> {
            var user = (User) Objects.requireNonNull(ctx.attribute("user"));
            var database = ctx.pathParam("database");
            var queryId = ctx.pathParam("query");

            var result = databases
                .getServices()
                .executeQueryById(user, database, queryId)
                .thenApply(records -> {
                    var file = Operators.suppressExceptions(() -> Files.createTempFile("mq", "download"));
                    records.toFile(file);
                    return DeleteOnCloseFileInputStream.apply(file);
                })
                .toCompletableFuture();

            ctx.header("Content-Disposition", "attachment; filename=" + database + "." + queryId + ".avro");
            ctx.header("Content-Type", "application/octet-stream");
            ctx.result(result);
        });
    }

    public Handler downloadCustomQuery() {
        var docs = OpenApiBuilder
            .document()
            .operation(op -> {
                op.summary("Download Database Data");
                op.description("Downloads data from a database's defined query.");
                op.addTagsItem("Databases");
            })
            .pathParam("database", String.class, p -> p.description("The name of the database"))
            .json("200", String.class);

        return OpenApiBuilder.documented(docs, ctx -> {
            var user = (User) Objects.requireNonNull(ctx.attribute("user"));
            var database = ctx.pathParam("database");
            var query = ctx.body();

            var result = databases
                .getServices()
                .executeCustomQuery(user, database, query)
                .thenApply(records -> {
                    var file = Operators.suppressExceptions(() -> Files.createTempFile("mq", "download"));
                    records.toFile(file);
                    return DeleteOnCloseFileInputStream.apply(file);
                })
                .toCompletableFuture();

            ctx.header("Content-Disposition", "attachment; filename=" + database + ".avro");
            ctx.header("Content-Type", "application/octet-stream");
            ctx.result(result);
        });
    }
}
