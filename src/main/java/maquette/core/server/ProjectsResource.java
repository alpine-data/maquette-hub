package maquette.core.server;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import lombok.AllArgsConstructor;
import maquette.core.api.Projects;
import maquette.core.values.projects.ProjectSummary;

@AllArgsConstructor()
public final class ProjectsResource {

    private final Projects projects;

    public Handler createProject() {
        var docs = OpenApiBuilder
                .document()
                .operation(op -> {
                    op.summary("Create Project");
                    op.description("Creates a new Maquette Project.");
                    op.addTagsItem("Projects");
                })
                .result("200");

        return OpenApiBuilder.documented(docs, ctx -> {
           ctx.result("Created");
        });
    }

    public Handler getProjects() {
        var docs = OpenApiBuilder
                .document()
                .operation(op -> {
                    op.summary("List Projects");
                    op.description("Returns the list of the users projects.");
                    op.addTagsItem("Projects");
                })
                .jsonArray("200", ProjectSummary.class);

        return OpenApiBuilder.documented(docs, ctx -> {
            // TODO: How about some logic?
            ctx.result("Hello World");
        });
    }

}
