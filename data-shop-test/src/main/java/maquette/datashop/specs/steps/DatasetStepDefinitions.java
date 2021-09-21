package maquette.datashop.specs.steps;

import com.google.common.collect.Lists;
import maquette.core.MaquetteRuntime;
import maquette.core.values.user.AuthenticatedUser;
import maquette.datashop.MaquetteDataShop;

public class DatasetStepDefinitions extends DataAssetStepDefinitions {

    public DatasetStepDefinitions(MaquetteRuntime runtime) {
        super(runtime, Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList(), null);
    }

    public void $_uploads_$_records_to_dataset_$(AuthenticatedUser bob, int recordsCount, String dataset) {
        this.runtime.getModule(MaquetteDataShop.class);
    }
}
