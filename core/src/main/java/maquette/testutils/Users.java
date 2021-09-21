package maquette.testutils;

import maquette.core.values.UID;
import maquette.core.values.user.AnonymousUser;
import maquette.core.values.user.AuthenticatedUser;

public final class Users {

    public static final String ALICE = "alice";
    public static final String BOB = "bob";
    public static final String CHARLY = "charly";

    public final AuthenticatedUser alice = AuthenticatedUser.apply(UID.apply(ALICE), "team-a", "team-b");

    public final AuthenticatedUser bob = AuthenticatedUser.apply(UID.apply(BOB), "team-a");

    public final AuthenticatedUser charly = AuthenticatedUser.apply(UID.apply(CHARLY), "team-b");

    public final AnonymousUser anonymous = AnonymousUser.apply();

    private Users() {

    }

    public static Users apply() {
        return new Users();
    }

}
