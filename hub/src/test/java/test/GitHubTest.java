package test;

import com.google.common.eventbus.EventBus;
import org.junit.Test;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;

public class GitHubTest {

   @Test
   public void test() throws IOException {
      var gh = new GitHubBuilder().withPassword("cokeSchlumpf", "8df20dcf7da191e3454384d647fc7a38b8fc5396").build();

      var result = gh.getUser("cokeSchlumpf").getRepositories();
      // result.keySet().forEach(System.out::println);

      var repo = gh.getRepository("cokeSchlumpf/mlops-titanic");

      System.out.println(repo.getGitTransportUrl());
      System.out.println(repo.getBranch("master").getSHA1());

      repo = gh.createRepository("some-test").owner("cokeSchlumpf").description("Lorem ipsum").create();

      EventBus bus = new EventBus();
      bus.post("Hello");
      bus.register("Hallo");

      repo.listCommits().toList().stream().map(commit -> {
         try {
            return commit.getCommitShortInfo().getMessage();
         } catch (IOException e) {
            return "<ERROR>";
         }
      }).forEach(System.out::println);
   }

}
