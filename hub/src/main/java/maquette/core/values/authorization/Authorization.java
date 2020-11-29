package maquette.core.values.authorization;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import maquette.core.values.user.User;

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   property = "type")
@JsonSubTypes(
   {
      @JsonSubTypes.Type(value = RoleAuthorization.class, name = "role"),
      @JsonSubTypes.Type(value = UserAuthorization.class, name = "user"),
      @JsonSubTypes.Type(value = WildcardAuthorization.class, name = "wildcard")
   })
public interface Authorization {

    boolean authorizes(User user);

    @JsonIgnore
    String getKey();

}
