package maquette.core.server.commands;

import com.google.common.collect.Maps;
import maquette.core.MaquetteRuntime;
import maquette.core.common.Templates;
import maquette.core.server.resource.AboutResource;
import maquette.core.values.user.User;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class AboutCommand implements Command {

    @Override
    public CompletionStage<CommandResult> run(User user, MaquetteRuntime runtime) {
        var about = AboutResource.About.apply(runtime
            .getConfig()
            .getEnvironment(), runtime
            .getConfig()
            .getVersion());
        return CompletableFuture.completedFuture(new AboutDataResult(about));
    }

    @Override
    public Command example() {
        return new AboutCommand();
    }

    private static class AboutDataResult extends DataResult<AboutResource.About> {

        protected AboutDataResult(AboutResource.About data) {
            super(data);
        }

        @Override
        public String toPlainText(MaquetteRuntime runtime) {
            var map = Maps.<String, Object>newHashMap();
            map.put("version", data.getVersion());
            map.put("environment", data.getEnvironment());

            return Templates.renderTemplateFromResources(runtime
                .getConfig()
                .getCore()
                .getBanner(), map);
        }
    }

}
