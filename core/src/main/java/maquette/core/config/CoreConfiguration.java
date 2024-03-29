package maquette.core.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import maquette.core.config.annotations.ConfigurationProperties;
import maquette.core.config.annotations.Value;

@Getter
@ConfigurationProperties
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor(staticName = "apply")
public class CoreConfiguration {

    /**
     * The name of the resource which includes the banner template shown in startup.
     */
    @Value("banner")
    String banner;

    /**
     * The port where Maquette API should listen.
     */
    @Value("port")
    int port;

    /**
     * The hostname where Maquette API should listen.
     */
    @Value("host")
    String host;

    /**
     * The HTTP header name which contains the user name, provided by the authentication provider.
     */
    @Value("user-id-header-name")
    String userIdHeaderName;

    /**
     * The HTTP header name which contains the user roles, provided by the authentication provider.
     */
    @Value("user-roles-header-name")
    String userRolesHeaderName;

    /**
     * The HTTP header name which contains the user details.
     */
    @Value("user-details-header-name")
    String userDetailsHeaderName;

    /**
     * The HTTP header name which contains the application ID
     */
    @Value("application-id-header-name")
    String applicationIdHeaderName;

    /**
     * The HTTP header name which contains the application secret
     */
    @Value("application-secret-header-name")
    String applicationSecretHeaderName;

    /**
     * OauthProxy User related login
     */
    @Value("oauth-app-name-header-name")
    String oauthAppNameHeaderName;

    @Value("oauth-workspace-header-name")
    String oauthWorkspaceHeaderName;

    @Value("application-secret-strength")
    int applicationSecretStrength;


    @Value("rootURL")
    String rootURL;

    @Value("emailServiceUsername")
    String emailServiceUsername;

    @Value("emailServicePassword")
    String emailServicePassword;

    @Value("emailServiceUrl")
    String emailServiceUrl;

    @Value("emailServiceFromEmail")
    String emailServiceFromEmail;

    @Value("emailServiceFromName")
    String emailServiceFromName;


    /**
     * The HTTP header name which may contain the id of the authentication token.
     */
    @Value("auth-token-id-header-name")
    String authTokenIdHeaderName;

    /**
     * The HTTP header name which may contain the secret for the authentication token.
     */
    @Value("auth-token-secret-header-name")
    String authTokenSecretHeaderName;

    @Value("enable-elastic-apm")
    boolean enableElasticApm;

}
