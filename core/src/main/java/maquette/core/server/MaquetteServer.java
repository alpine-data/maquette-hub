package maquette.core.server;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import io.javalin.http.Context;
import io.javalin.plugin.json.JavalinJackson;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.Maquette;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Operators;
import maquette.core.common.exceptions.ApplicationException;
import maquette.core.modules.applications.ApplicationModule;
import maquette.core.modules.users.UserModule;
import maquette.core.modules.users.exceptions.InvalidAuthenticationTokenException;
import maquette.core.server.commands.AboutCommand;
import maquette.core.server.commands.ErrorResult;
import maquette.core.server.commands.MessageResult;
import maquette.core.server.resource.AboutResource;
import maquette.core.server.resource.CommandResource;
import maquette.core.server.resource.PostmanDocsResource;
import maquette.core.values.UID;
import maquette.core.values.user.AnonymousUser;
import maquette.core.values.user.AuthenticatedUser;
import maquette.core.values.user.OauthProxyUser;
import maquette.core.values.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MaquetteServer {

    private static final Logger LOG = LoggerFactory.getLogger(Maquette.class);

    private final MaquetteRuntime runtime;

    /**
     * Creates a new Maquette server instance. This method configures all HTTP endpoints
     * of Maquette.
     *
     * @param runtime Maquette's runtime configuration.
     * @return The server instance.
     */
    public static MaquetteServer apply(MaquetteRuntime runtime) {
        var om = runtime
            .getObjectMapperFactory()
            .createJsonMapper(true);
        JavalinJackson.configure(om);

        var adminResource = new AboutResource(runtime);
        var docsResource = new PostmanDocsResource(runtime, om);
        var commandResource = new CommandResource(runtime);
        var server = new MaquetteServer(runtime);

        /*
         * Register core commands
         */
        var aboutCommandType = new NamedType(AboutCommand.class, "about");
        om.registerSubtypes(aboutCommandType);

        /*
         * Register commands from modules
         */
        runtime
            .getModules()
            .forEach(module -> module
                .getCommands()
                .forEach((key, command) -> {
                    var type = new NamedType(command, key);
                    om.registerSubtypes(type);
                }));

        /*
         * Setup routes
         */
        runtime
            .getApp()
            .before(server::handleAuthentication)
            .get("/api/about", adminResource.getAbout())
            .get("/api/about/user", adminResource.getUserInfo())
            .get("/api/about/config", adminResource.getConfiguration())
            .get("/api/commands", docsResource.getDocs())
            .post("/api/commands", commandResource.getCommand());

        /*
         * Default exception handling
         */
        runtime
            .getApp()
            .exception(Exception.class, (e, ctx) -> {
                var maybeApplicationException = Operators.hasCause(e, ApplicationException.class);

                if (maybeApplicationException.isPresent()) {
                    var error = maybeApplicationException.get();
                    ctx.status(error.getHttpStatus());
                    ctx.json(ErrorResult.create(error.getMessage()));
                    LOG.warn("A domain exception was caught and returned to the client", e);
                } else {
                    LOG.warn("Unhandled exception upon API call", e);
                    ctx.status(500);
                    ctx.json(MessageResult.create("Internal Server Error"));
                }
            });

        return server;
    }

    private void handleAuthentication(Context ctx) {
        /*
         * use custom authentication handler
         */
        ctx.attribute("user", runtime
            .getAuthenticationHandler()
            .handleAuthentication(ctx, runtime));

        /*
         * handle user details if present in request
         */
        var user = (User) ctx.attribute("user");
        var userDetails = runtime
            .getAuthenticationHandler()
            .getUserDetailsFromRequest(ctx, runtime);

        if (userDetails.isPresent() && user instanceof AuthenticatedUser) {
            Operators.ignoreExceptions(() -> runtime
                .getModule(UserModule.class)
                .getServices()
                .updateUserDetails(ctx.attribute("user"), userDetails.get())
                .toCompletableFuture()
                .get());
        }

        /*
         * handle technical authentication tokens
         */
        var headers = ctx.headerMap();

        if (user instanceof AnonymousUser) {
            var cfg = runtime
                .getConfig()
                .getCore();

            try {
                // check for application based auth
                if (headers.containsKey(cfg.getApplicationIdHeaderName())
                    && headers.containsKey(cfg.getApplicationSecretHeaderName())) {
                    String id = headers.get(cfg.getApplicationIdHeaderName());
                    String secret = headers.get(cfg.getApplicationSecretHeaderName());
                    var appUser = Operators.suppressExceptions(() -> runtime
                        .getModule(ApplicationModule.class)
                        .getApplications()
                        .getApplicationUserByIdAndSecret(UID.apply(id), secret)
                        .toCompletableFuture()
                        .get());
                    if (appUser.isEmpty())
                        throw InvalidAuthenticationTokenException.apply("invalid application id or secret");
                    ctx.attribute("user", appUser.get());
                }
                // trusted oauth login for OauthProxyUser
                else if (headers.containsKey(cfg.getOauthAppNameHeaderName())
                    && headers.containsKey(cfg.getOauthWorkspaceHeaderName())) {
                    String name = headers.get(cfg.getOauthAppNameHeaderName());
                    String workspace = headers.get(cfg.getOauthWorkspaceHeaderName());
                    if (name.isEmpty() || workspace.isEmpty())
                        throw InvalidAuthenticationTokenException.apply("invalid application name or workspace name");
                    ctx.attribute("user", OauthProxyUser.apply(name, workspace));
                }
                // check for token id/secret auth
                else if (headers.containsKey(cfg.getAuthTokenSecretHeaderName())
                    && headers.containsKey(cfg.getAuthTokenIdHeaderName())) {
                    var tokenId = headers.get(cfg.getAuthTokenIdHeaderName());
                    var tokenSecret = headers.get(cfg.getAuthTokenSecretHeaderName());
                    var authUser = Operators.suppressExceptions(() -> runtime
                        .getModule(UserModule.class)
                        .getServices()
                        .getUserForAuthenticationToken(tokenId, tokenSecret)
                        .toCompletableFuture()
                        .get());

                    ctx.attribute("user", authUser);
                }
            } catch (Exception ex) {
                if (Operators
                    .hasCause(ex, InvalidAuthenticationTokenException.class)
                    .isEmpty()) {
                    throw ex;
                }
            }
        }
    }

    public void start() {
        runtime
            .getApp()
            .start(
                runtime
                    .getConfig()
                    .getCore()
                    .getHost(),
                runtime
                    .getConfig()
                    .getCore()
                    .getPort());
    }

    public void stop() {
        runtime
            .getApp()
            .stop();
    }

}
