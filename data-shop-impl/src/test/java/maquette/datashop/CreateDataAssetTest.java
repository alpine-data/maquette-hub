package maquette.datashop;

import maquette.datashop.providers.FakeProvider;
import maquette.datashop.values.metadata.DataAssetMetadata;
import maquette.testutils.MaquetteContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateDataAssetTest {

    private MaquetteContext mq;
    private MaquetteDataShopTestContext context;

    @BeforeEach
    public void setup() {
        mq = MaquetteContext.apply();
        context = MaquetteDataShopTestContext.apply();
    }

    /**
     * Simple smoke test.
     */
    @Test
    public void smokeTest() throws ExecutionException, InterruptedException {
        context.shop
            .getServices()
            .create(mq.users.alice, FakeProvider.NAME, DataAssetMetadata.sample(), mq.authorizations.alice,
                mq.authorizations.alice, null)
            .toCompletableFuture()
            .get();

        var assets = context.shop
            .getServices()
            .list(mq.users.alice)
            .toCompletableFuture()
            .get();

        assertThat(assets).hasSize(1);
    }

    @Test
    public void testWithoutStewardAndOwner() throws ExecutionException, InterruptedException {
        context
            .shop
            .getServices()
            .create(
                mq.users.alice, FakeProvider.NAME, DataAssetMetadata.sample(), null, null, null
            )
            .toCompletableFuture()
            .get();

        var assets = context.shop
            .getServices()
            .list(mq.users.alice)
            .toCompletableFuture()
            .get();

        assertThat(assets).hasSize(1);
    }

}
