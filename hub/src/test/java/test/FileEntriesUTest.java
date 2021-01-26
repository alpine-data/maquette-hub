package test;

import maquette.core.entities.data.collections.model.FileEntry;
import maquette.core.values.ActionMetadata;
import maquette.core.values.data.binary.FileSize;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FileEntriesUTest {

   @Test
   public void test() {
      var dir = FileEntry.Directory.apply();

      var dirUpdated = dir
         .withFile(
            "file-1",
            FileEntry.RegularFile.apply("abc", FileSize.empty(), FileEntry.FileType.BINARY, "some message", ActionMetadata.apply("egon")))
         .withFile(
            "file-2",
            FileEntry.RegularFile.apply("def", FileSize.empty(), FileEntry.FileType.TEXT, "some message", ActionMetadata.apply("egon")))
         .withFile(
            "file-0",
            FileEntry.RegularFile.apply("def", FileSize.empty(), FileEntry.FileType.IMAGE, "some message", ActionMetadata.apply("egon")))
         .withFile(
            "foo/bar-0",
            FileEntry.RegularFile.apply("def", FileSize.empty(), FileEntry.FileType.TEXT, "some message", ActionMetadata.apply("egon")))
         .withFile(
            ".DS_Store",
            FileEntry.RegularFile.apply("def", FileSize.empty(), FileEntry.FileType.IMAGE, "some message", ActionMetadata.apply("egon")));



      System.out.println(dirUpdated);
      System.out.println(dirUpdated.files());

      assertThat(dirUpdated.toString())
         .contains("file-0")
         .contains("file-1")
         .contains("file-2")
         .contains("bar-0");

      dirUpdated.files().forEach(System.out::println);
   }

}
