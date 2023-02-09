package maquette.core.values.user;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.OauthProxyAuthorization;

import java.util.List;

/**
 * Oauth Proxy user used only temporarily when
 * we receive a request from Oauth proxy behaving
 * on it's own behalf, i.e. retrieving token for user
 * while being trusted
 */
@Value
@AllArgsConstructor(staticName = "apply")
public class OauthProxyUser implements User {

    String name;
    String workspace;

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public List<String> getRoles() {
        return Lists.newArrayList();
    }

    @Override
    public Authorization toAuthorization() {
        return OauthProxyAuthorization.apply(name);
    }

}
