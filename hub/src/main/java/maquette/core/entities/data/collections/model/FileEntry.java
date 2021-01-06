package maquette.core.entities.data.collections.model;

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

      public static Directory apply() {
         return apply(Map.of());
      }

      @JsonCreator
      public static Directory apply(
         @JsonProperty(CHILDREN) Map<String, FileEntry> children) {

         return new Directory(Map.copyOf(children));
      }

      @JsonProperty("lastModified")
      public Optional<RegularFile> getLastModified() {
         return files()
            .stream()
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
            result = result.add(f.size);
         }

         return result;
      }

      @JsonIgnore
      public List<RegularFile> files() {
         var result = Lists.<RegularFile>newArrayList();

         for (var entry : children.entrySet()) {
            if (entry.getValue() instanceof Directory) {
               var children = ((Directory) entry.getValue())
                  .files()
                  .stream()
                  .map(file -> file.withKey(entry.getKey() + "/" + file.getKey()))
                  .collect(Collectors.toList());

               result.addAll(children);
            } else {
               result.add((RegularFile) entry.getValue());
            }
         }

         return result;
      }

      public Optional<RegularFile> getFile(String name) {
         var path = getPathFromName(name);

         if (path.isEmpty()) {
            throw new IllegalArgumentException("Invalid file name");
         } else if (path.size() == 1) {
            var elementName = path.get(0);
            var exists = children.get(elementName);

            if (exists instanceof RegularFile) {
               return Optional.of((RegularFile) exists);
            } else {
               return Optional.empty();
            }
         } else {
            var elementName = path.get(0);
            var exists = children.get(elementName);
            var remaining = String.join("/", path.subList(1, path.size()));

            if (exists != null && !(exists instanceof Directory)) {
               return Optional.empty();
            } else if (exists != null) { // is a Directory ...
               return getFile(remaining);
            } else {
               return Optional.empty();
            }
         }
      }

      public Directory withFile(String name, RegularFile entry) {
         Map<String, FileEntry> childrenNext = Maps.newHashMap();
         childrenNext.putAll(children);

         var path = getPathFromName(name);

         if (path.isEmpty()) {
            throw new IllegalArgumentException("Invalid file name");
         } else if (path.size() == 1) {
            var elementName = path.get(0);
            var exists = children.get(elementName);

            if (exists != null && !(exists instanceof RegularFile)) {
               throw new IllegalArgumentException("Cannot replace directory with file");
            } else {
               childrenNext.put(elementName, entry);
            }
         } else {
            var elementName = path.get(0);
            var exists = children.get(elementName);
            var remaining = String.join("/", path.subList(1, path.size()));

            if (exists != null && !(exists instanceof Directory)) {
               throw new IllegalArgumentException("Cannot replace file with Directory");
            } else if (exists != null) { // is a Directory ...
               childrenNext.put(elementName, ((Directory) exists).withFile(remaining, entry));
            } else {
               childrenNext.put(elementName, (Directory.apply()).withFile(remaining, entry));
            }
         }

         return new Directory(childrenNext);
      }

      public Directory withoutFile(String name) {
         Map<String, FileEntry> childrenNext = Maps.newHashMap();
         childrenNext.putAll(children);

         var path = getPathFromName(name);

         if (path.isEmpty()) {
            throw new IllegalArgumentException("Invalid file name");
         } else if (path.size() == 1) {
            var elementName = path.get(0);
            var exists = children.get(elementName);

            if (exists != null && !(exists instanceof RegularFile)) {
               throw new IllegalArgumentException("Cannot replace directory with file");
            } else {
               return this;
            }
         } else {
            var elementName = path.get(0);
            var exists = children.get(elementName);
            var remaining = String.join("/", path.subList(1, path.size()));

            if (exists != null && !(exists instanceof Directory)) {
               throw new IllegalArgumentException("Invalid path!");
            } else if (exists != null) { // is a Directory ...
               var directoryNext = ((Directory) exists).withoutFile(remaining);
               if (directoryNext.children.isEmpty()) {
                  childrenNext.remove(elementName);
               } else {
                  childrenNext.put(elementName, directoryNext);
               }
            }
         }

         return new Directory(childrenNext);
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

            if (i < c.size() - 1 || (element instanceof Directory && ((Directory) element).children.size() > 0)) {
               sb.append("\n");
            }

            if (element instanceof Directory) {
               ((Directory) element).toStringTree$internal(indent + 3, sb);
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
         this("", FileSize.empty(), FileType.BINARY, "", ActionMetadata.apply(""));
      }

      String key;

      FileSize size;

      FileType fileType;

      String message;

      ActionMetadata added;

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
