package maquette.core.entities.infrastructure;

import akka.Done;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.Operators;
import maquette.core.entities.infrastructure.model.DeploymentConfig;
import maquette.core.ports.InfrastructureProvider;
import maquette.core.ports.InfrastructureRepository;
import maquette.core.ports.MlflowProxyPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class InfrastructureManager implements MlflowProxyPort {

   private static final Logger LOG = LoggerFactory.getLogger(InfrastructureManager.class);

   private final InfrastructureProvider infrastructureProvider;

   private final InfrastructureRepository repository;

   private final MlflowProxyPort mlflowProxy;

   private final HashMap<String, Deployment> deployments;

   public static InfrastructureManager apply(
      InfrastructureProvider infrastructureProvider, InfrastructureRepository repository, MlflowProxyPort mlflowProxy) {
      HashMap<String, Deployment> deployments = Maps.newHashMap();

      var mgr = new InfrastructureManager(infrastructureProvider, repository, mlflowProxy, deployments);
      Operators.suppressExceptions(mgr::initialize);

      return mgr;
   }

   private CompletionStage<Done> initialize() {
      return repository
         .getDeployments()
         .thenCompose(deployments -> Operators.allOf(deployments
            .stream()
            .map(deployment -> applyConfig$internal(deployment.getConfig(), deployment.getCreated()))
            .collect(Collectors.toList())))
         .thenApply(deployments -> {
            deployments.forEach(d -> this.deployments.put(d.getConfig().getName(), d));
            return Done.getInstance();
         })
         .thenApply(done -> {
            LOG.info("Infrastructure initialization complete");
            return Done.getInstance();
         })
         .exceptionally(ex -> {
            LOG.warn("Exception during infrastructure initialization", ex);
            return Done.getInstance();
         });
   }

   private CompletionStage<Deployment> applyConfig$internal(DeploymentConfig config, Instant created) {
      return infrastructureProvider
         .runDeployment(config)
         .thenApply(deployment -> deployment.withCreated(created));
   }

   public CompletionStage<Done> applyConfig(DeploymentConfig config) {
      if (deployments.containsKey(config.getName()) && deployments.get(config.getName()).getConfig().equals(config)) {
         return CompletableFuture.completedFuture(Done.getInstance());
      } else if (deployments.containsKey(config.getName())) {
         throw new RuntimeException("Name already added.");
      } else {
         return applyConfig$internal(config, Instant.now())
            .thenCompose(deployment -> repository
               .insertOrUpdateDeployment(deployment.toMemento())
               .thenApply(done -> deployment))
            .thenApply(deployment -> {
               deployments.put(deployment.getConfig().getName(), deployment);
               return Done.getInstance();
            });
      }
   }

   public Optional<Deployment> findDeployment(String name) {
      if (deployments.containsKey(name)) {
         return Optional.of(deployments.get(name));
      } else {
         return Optional.empty();
      }
   }

   public Deployment getDeployment(String name) {
      return findDeployment(name).orElseThrow(() -> new RuntimeException("Deployment not found ...")); // TODO ...
   }

   public CompletionStage<Done> startDeployment(String name) {
      if (!deployments.containsKey(name)) {
         throw new RuntimeException("Deployment not found ...");
      } else {
         var deployment = deployments.get(name);
         return deployment
            .start()
            .thenCompose(done -> repository.insertOrUpdateDeployment(deployment.toMemento()));
      }
   }

   public CompletionStage<Done> stopDeployment(String name) {
      if (!deployments.containsKey(name)) {
         throw new RuntimeException("Deployment not found ...");
      } else {
         var deployment = deployments.get(name);

         return deployment
            .stop()
            .thenCompose(done -> repository.insertOrUpdateDeployment(deployment.toMemento()));
      }
   }

   public CompletionStage<Done> removeDeployment(String name) {
      if (!deployments.containsKey(name)) {
         return CompletableFuture.completedFuture(Done.getInstance());
      } else {
         return Operators
            .allOf(deployments.get(name).getContainers().stream().map(Container::stop).collect(Collectors.toList()))
            .thenCompose(done -> repository.removeDeployment(name))
            .thenApply(done -> {
               deployments.remove(name);
               return Done.getInstance();
            });
      }
   }

   @Override
   public CompletionStage<Done> registerRoute(String id, String route, String target) {
      return mlflowProxy.registerRoute(id, route, target);
   }

   @Override
   public CompletionStage<Done> removeRoute(String id) {
      return mlflowProxy.removeRoute(id);
   }

}
