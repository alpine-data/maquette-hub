package maquette.core.ports.email;

import akka.Done;
import lombok.AllArgsConstructor;
import maquette.core.modules.ports.UsersRepository;
import maquette.core.modules.users.model.UserProfile;
import net.minidev.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public class EmailClientImpl implements  EmailClient{


    @Override
    public CompletionStage<Done> sendEmail(UserProfile userProfile, String toAddress, String emailBody, String emailSubject, boolean html) {
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
            = new UsernamePasswordCredentials("e9d7fbfa30bf42a6a6a08fc0a3f24bcc", "26e101927a6A43Fc8Ea8ad90664D007e");
        provider.setCredentials(AuthScope.ANY, credentials);

        StringEntity requestEntity = new StringEntity(
            "{\n" +
                "    \"messages\": [{\n" +
                "        \"subject\": \""+emailSubject+"\",\n" +
                "        \"textPart\": \""+emailBody+"\",\n" +
                "        \"to\": [{\n" +
                "            \"email\": \""+userProfile.getEmail()+"\",\n" +
                "            \"name\": \""+userProfile.getName()+"\"\n" +
                "        }],\n" +
                "        \"from\": {\n" +
                "            \"email\": \"no-reply@zurich.com\",\n" +
                "            \"name\": \"Mars No Reply\"\n" +
                "        }\n" +
                "    }]\n" +
                "}",
            ContentType.APPLICATION_JSON);
        HttpClient httpclient = HttpClientBuilder.create()
                                                 .setDefaultCredentialsProvider(provider)
                                                 .build();
        HttpPost httppost = new HttpPost("https://capi.zurich.com/z/c4e/s/mailjet/v1/messages/send");
        httppost.setEntity(requestEntity);
        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            try (InputStream instream = entity.getContent()) {
                String response1 = new BasicResponseHandler().handleResponse(response);
                System.out.println(response1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return CompletableFuture.completedFuture(Done.getInstance());
    }

}
