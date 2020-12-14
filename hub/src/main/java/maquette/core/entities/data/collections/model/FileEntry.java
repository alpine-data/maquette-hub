package maquette.core.entities.data.collections.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import lombok.*;
import maquette.core.values.ActionMetadata;
import maquette.core.values.data.binary.FileSize;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

      public static Directory apply(
         @JsonProperty(CHILDREN) Map<String, FileEntry> children) {

         return new Directory(Map.copyOf(children));
      }

      public Directory withFile(String name, RegularFile entry) {
         Map<String, FileEntry> childrenNext = Maps.newHashMap();
         childrenNext.putAll(children);

         var path = Arrays
            .stream(name.split("/"))
            .filter(s -> !s.trim().isEmpty())
            .collect(Collectors.toList());

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

            if (i < c.size() - 1) {
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

   }

   @Value
   @EqualsAndHashCode(callSuper = false)
   @AllArgsConstructor(staticName = "apply")
   @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
   public static class RegularFile extends FileEntry {

      String key;

      FileSize size;

      String message;

      ActionMetadata added;

   }

}
