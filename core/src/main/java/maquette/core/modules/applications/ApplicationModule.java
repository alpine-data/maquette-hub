package maquette.core.modules.applications;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.core.MaquetteRuntime;
import maquette.core.modules.MaquetteModule;
import maquette.core.modules.ports.ApplicationsRepository;
import maquette.core.server.commands.Command;

import java.util.Map;

@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public class ApplicationModule implements MaquetteModule {
    private final ApplicationEntities applications;

    public static ApplicationModule apply(MaquetteRuntime runtime, ApplicationsRepository applicationsRepository) {
        var applications = ApplicationEntities.apply(applicationsRepository, runtime
            .getObjectMapperFactory()
            .createJsonMapper());
        return apply(applications);
    }

    @Override
    public String getName() {
        return "applications";
    }

    @Override
    public void start(MaquetteRuntime runtime) {
        MaquetteModule.super.start(runtime);
    }

    @Override
    public void stop() {
        MaquetteModule.super.stop();
    }

    @Override
    public Map<String, Class<? extends Command>> getCommands() {
        return MaquetteModule.super.getCommands();
    }

    public ApplicationEntities getApplications() {
        return applications;
    }

}
