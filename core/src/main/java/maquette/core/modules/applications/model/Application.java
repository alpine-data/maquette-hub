package maquette.core.modules.applications.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import maquette.core.values.UID;

import java.util.Objects;


@With
@Value
@AllArgsConstructor(staticName = "apply")
public class Application {

    private static final String ID = "id";
    private static final String WORKSPACE_ID = "workspaceId";
    private static final String SECRET = "secret";
    private static final String NAME = "name";
    private static final String META_INFO = "metaInfo";

    @JsonProperty(ID)
    UID id;

    @JsonProperty(WORKSPACE_ID)
    UID workspaceId;

    @JsonProperty(SECRET)
    String secret;

    @JsonProperty(NAME)
    String name;

    @JsonProperty(META_INFO)
    String metaInfo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Application that = (Application) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
