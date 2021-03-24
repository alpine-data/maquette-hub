package maquette.asset_providers.collections.model;

import com.fasterxml.jackson.annotation.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.*;
import maquette.core.values.ActionMetadata;
import maquette.core.values.data.binary.FileSize;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "type")
@JsonSubTypes({
   @JsonSubTypes.Type(value = FileEntry.Directory.class, name = "directory"),
   @JsonSubTypes.Type(value = FileEntry.RegularFile.class, name = "file")
})
public abstract class FileEntry {

   @Value
   @EqualsAndHashCode(callSuper = false)
   @AllArgsConstructor(access = AccessLevel.PRIVATE)
   public static class Directory extends FileEntry {

      private static final String CHILDREN = "children";

      @JsonProperty(CHILDREN)
      Map<String, FileEntry> children;

      public static FileEntry.Directory apply() {
         return apply(Map.of());
      }

      @JsonCreator
      public static FileEntry.Directory apply(
         @JsonProperty(CHILDREN) Map<String, FileEntry> children) {

         return new FileEntry.Directory(Map.copyOf(children));
      }

      @JsonProperty("lastModified")
      public Optional<FileEntry.RegularFile> getLastModified() {
         return files()
            .stream()
            .map(FileEntry.NamedRegularFile::getFile)
            .max(Comparator.comparing(f -> f.getAdded().getAt()));
      }

      @JsonProperty("files")
      public int getFilesCount() {
         return files().size();
      }

      @JsonProperty("size")
      public FileSize getSize() {
         var result = FileSize.empty();

         for (var f : files()) {
            result = result.add(f.getFile().size);
         }

         return result;
      }

      @JsonIgnore
      public List<String> fileNames() {
         var result = Lists.<String>newArrayList();

         for (var entry : children.entrySet()) {
            if (entry.getValue() instanceof FileEntry.Directory) {
               var children = ((FileEntry.Directory) entry.getValue())
                  .fileNames()
                  .stream()
                  .map(file -> entry.getKey() + "/" + file)
                  .collect(Collectors.toList());

               result.addAll(children);
            } else {
               result.add(entry.getKey());
            }
         }

         result.sort(Comparator.naturalOrder());

         return result;
      }

      @JsonIgnore
      public List<FileEntry.NamedRegularFile> files() {
         var result = Lists.<FileEntry.NamedRegularFile>newArrayList();

         for (var entry : children.entrySet()) {
            if (entry.getValue() instanceof FileEntry.Directory) {
               var children = ((FileEntry.Directory) entry.getValue())
                  .files()
                  .stream()
                  .map(file -> file.withName(entry.getKey() + "/" + file.getName()))
                  .collect(Collectors.toList());

               result.addAll(children);
            } else {
               result.add(FileEntry.NamedRegularFile.apply(entry.getKey(), (FileEntry.RegularFile) entry.getValue()));
            }
         }

         result.sort(Comparator.comparing(FileEntry.NamedRegularFile::getName, Comparator.naturalOrder()));

         return result;
      }

      public Optional<FileEntry.RegularFile> getFile(String name) {
         var path = getPathFromName(name);

         if (path.isEmpty()) {
            throw new IllegalArgumentException("Invalid file name");
         } else if (path.size() == 1) {
            var elementName = path.get(0);
            var exists = children.get(elementName);

            if (exists instanceof FileEntry.RegularFile) {
               return Optional.of((FileEntry.RegularFile) exists);
            } else {
               return Optional.empty();
            }
         } else {
            var elementName = path.get(0);
            var exists = children.get(elementName);
            var remaining = String.join("/", path.subList(1, path.size()));

            if (exists != null && !(exists instanceof FileEntry.Directory)) {
               return Optional.empty();
            } else if (exists != null) { // is a Directory ...
               return ((FileEntry.Directory) exists).getFile(remaining);
            } else {
               return Optional.empty();
            }
         }
      }

      public FileEntry.Directory withFile(String name, FileEntry.RegularFile entry) {
         Map<String, FileEntry> childrenNext = Maps.newHashMap();
         childrenNext.putAll(children);

         var path = getPathFromName(name);

         if (path.isEmpty()) {
            throw new IllegalArgumentException("Invalid file name");
         } else if (path.size() == 1) {
            var elementName = path.get(0);
            var exists = children.get(elementName);

            if (exists != null && !(exists instanceof FileEntry.RegularFile)) {
               throw new IllegalArgumentException("Cannot replace directory with file");
            } else {
               childrenNext.put(elementName, entry);
            }
         } else {
            var elementName = path.get(0);
            var exists = children.get(elementName);
            var remaining = String.join("/", path.subList(1, path.size()));

            if (exists != null && !(exists instanceof FileEntry.Directory)) {
               throw new IllegalArgumentException("Cannot replace file with Directory");
            } else if (exists != null) { // is a Directory ...
               childrenNext.put(elementName, ((FileEntry.Directory) exists).withFile(remaining, entry));
            } else {
               childrenNext.put(elementName, (FileEntry.Directory.apply()).withFile(remaining, entry));
            }
         }

         return new FileEntry.Directory(childrenNext);
      }

      public FileEntry.Directory withoutFile(String name) {
         Map<String, FileEntry> childrenNext = Maps.newHashMap();
         childrenNext.putAll(children);

         var path = getPathFromName(name);

         if (path.isEmpty()) {
            throw new IllegalArgumentException("Invalid file name");
         } else if (path.size() == 1) {
            var elementName = path.get(0);
            var exists = children.get(elementName);

            if (exists != null && !(exists instanceof FileEntry.RegularFile)) {
               throw new IllegalArgumentException("Cannot replace directory with file");
            } else {
               return this;
            }
         } else {
            var elementName = path.get(0);
            var exists = children.get(elementName);
            var remaining = String.join("/", path.subList(1, path.size()));

            if (exists != null && !(exists instanceof FileEntry.Directory)) {
               throw new IllegalArgumentException("Invalid path!");
            } else if (exists != null) { // is a Directory ...
               var directoryNext = ((FileEntry.Directory) exists).withoutFile(remaining);
               if (directoryNext.children.isEmpty()) {
                  childrenNext.remove(elementName);
               } else {
                  childrenNext.put(elementName, directoryNext);
               }
            }
         }

         return new FileEntry.Directory(childrenNext);
      }

      private void toStringTree$internal(int indent, StringBuilder sb) {
         var c = children
            .keySet()
            .stream()
            .sorted()
            .collect(Collectors.toList());

         for (int i = 0; i < c.size(); i++) {
            sb.append(StringUtils.leftPad(" ", indent));
            var elementName = c.get(i);
            var element = children.get(elementName);

            if (i == c.size() - 1) {
               sb.append("└ ");
            } else {
               sb.append("├ ");
            }

            sb.append(elementName);

            if (i < c.size() - 1 || (element instanceof FileEntry.Directory && ((FileEntry.Directory) element).children.size() > 0)) {
               sb.append("\n");
            }

            if (element instanceof FileEntry.Directory) {
               ((FileEntry.Directory) element).toStringTree$internal(indent + 3, sb);
            }
         }
      }

      public String toString() {
         var sb = new StringBuilder();
         toStringTree$internal(0, sb);
         return sb.toString();
      }

      private List<String> getPathFromName(String name) {
         return Arrays
            .stream(name.split("/"))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .filter(s -> !s.replace('.', ' ').trim().isEmpty())
            .collect(Collectors.toList());
      }

   }

   @Value
   @With
   @EqualsAndHashCode(callSuper = false)
   @AllArgsConstructor(staticName = "apply")
   public static class RegularFile extends FileEntry {

      @SuppressWarnings("unused")
      private RegularFile() {
         this("", FileSize.empty(), FileEntry.FileType.BINARY, "", ActionMetadata.apply(""));
      }

      String key;

      FileSize size;

      FileEntry.FileType fileType;

      String message;

      ActionMetadata added;

   }

   @With
   @Value
   @AllArgsConstructor(staticName = "apply")
   public static class NamedRegularFile {

      String name;

      RegularFile file;

   }

   public enum FileType {

      TEXT("text"), IMAGE("image"), BINARY("binary");

      private final String value;

      FileType(String value) {
         this.value = value;
      }

      @JsonValue
      public String getValue() {
         return value;
      }
   }

}
