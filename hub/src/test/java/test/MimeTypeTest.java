package test;

import maquette.common.Operators;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MimeTypeTest {

   @Test
   public void test() throws IOException {
      Files
         .walk(Path.of("/Users/michaelwellner/Downloads"))
         .forEach(p -> {
            var ext = FilenameUtils.getExtension(p.getFileName().toString());
            System.out.println(p + " - " + Operators.ignoreExceptionsWithDefault(() -> Files.probeContentType(p), "nothing") + " - " + ext + " - " + null);
         });
   }

}
