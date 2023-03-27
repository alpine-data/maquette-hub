package maquette.core.values.user;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.SystemAuthorization;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class SystemUser implements User {

    @Override
    public String getDisplayName() {
        return "System";
    }

    @Override
    public List<String> getRoles() {
        return Lists.newArrayList();
    }

    public String getName() {
        return "mars.system";
    }

    @Override
    public Authorization toAuthorization() {
        return SystemAuthorization.apply(getName());
    }

}
