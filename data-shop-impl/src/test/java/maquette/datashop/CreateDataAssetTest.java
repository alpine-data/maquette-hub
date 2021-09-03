package maquette.datashop;

import maquette.datashop.values.metadata.DataAssetMetadata;
import maquette.datashop.providers.FakeProvider;
import maquette.testutils.MaquetteContext;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateDataAssetTest {

   private MaquetteContext mq;
   private MaquetteDataShopTestContext context;

   @Before
   public void setup() {
      mq = MaquetteContext.apply();
      context = MaquetteDataShopTestContext.apply();
   }

   /**
    * TODO mw: Write meaningful tests ...
    * @throws ExecutionException
    * @throws InterruptedException
    */

   @Test
   public void smokeTest() throws ExecutionException, InterruptedException {
      context.shop
         .getServices()
         .create(mq.users.alice, FakeProvider.NAME, DataAssetMetadata.sample(), mq.authorizations.alice, mq.authorizations.alice, null)
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