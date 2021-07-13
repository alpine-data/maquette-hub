package maquette.core.values.apidocs;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Url {

   String protocol;

   List<String> host;

   String port;

   List<String> path;

   public static Url apply() {
      return new Url("http", Lists.newArrayList(), null, Lists.newArrayList());
   }

   public Url withHost(String host) {
      this.host.add(host);
      return this;
   }

   public Url withPort(int port) {
      return new Url(protocol, host, String.valueOf(port), path);
   }

   public Url withPath(String path) {
      this.path.addAll(Arrays.stream(path.split("/")).collect(Collectors.toList()));
      return this;
   }

   public Url withPath(String first, String second, String ...remaining) {
      this.path.add(first);
      this.path.add(second);
      this.path.addAll(Arrays.stream(remaining).collect(Collectors.toList()));
      return this;
   }

   public String getRaw() {
      String host = String.join(".", this.host);
      String path = String.join("/", this.path);

      return protocol + "://" + host + "/" + path;
   }

}
