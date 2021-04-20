package test;

import com.google.common.collect.Streams;
import maquette.common.Operators;
import org.junit.Test;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.util.stream.StreamSupport;

public class GitHubTest {

   @Test
   public void test() throws IOException {
      var gh = new GitHubBuilder().withPassword("cokeSchlumpf", "8df20dcf7da191e3454384d647fc7a38b8fc5396").build();

      var login = Operators.suppressExceptions(() -> gh.getMyself().getLogin());

      var repo = gh.getRepository("rsuite/rsuite");
      System.out.println(repo.getSshUrl());

      /*
      StreamSupport
         .stream(gh.listAllPublicRepositories().spliterator(), false)
         .limit(3)
         .forEach(System.out::println);
*/
      /*
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
      }).forEach(System.out::println);*/
   }

}
