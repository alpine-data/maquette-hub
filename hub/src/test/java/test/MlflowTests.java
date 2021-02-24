package test;

import maquette.common.Operators;
import maquette.core.entities.projects.ports.MlflowPort;
import maquette.core.values.UID;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class MlflowTests {

   @Test
   public void test() {
      var out = MlflowPort
         .apply("http://localhost:55020", UID.apply("34984c15"))
         .getModels();

      /*
      new MlflowClient("http://localhost:55020")
         .getRun("a59708c475d34ea3a9287c1e777384db")
         .getData()
         .getTagsList()
         .forEach(tag -> {
            System.out.println(tag);
            System.out.println("---");
         });*/
   }

   @Test
   public void test2() {
      OkHttpClient client = new OkHttpClient.Builder()
         .readTimeout(3, TimeUnit.MINUTES)
         .build();

      var request = new Request.Builder()
         .url("http://localhost:55020/_mlflow/34984c15/get-artifact?path=model%2FMLmodel&run_uuid=a59708c475d34ea3a9287c1e777384db")
         .get()
         .build();

      try {
         var response = Operators.suppressExceptions(() -> client.newCall(request).execute());

         if (!response.isSuccessful()) {
            var body = response.body();
            var content = body != null ? Operators.suppressExceptions(body::string) : "";
            content = StringUtils.leftPad(content, 3);
            throw new RuntimeException("Received non-successful response from MLflow:\n" + content);
         } else {
            System.out.println(response.body().string());
         }
      } catch (Exception e) {
         throw new RuntimeException("Exception occurred requesting information from MLflow", e);
      }
   }

}
