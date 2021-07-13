package maquette.datashop.ports;

import maquette.core.MaquetteRuntime;

@FunctionalInterface
public interface DataAssetsRepositoryFactory {

   DataAssetsRepository create(MaquetteRuntime runtime);

}
