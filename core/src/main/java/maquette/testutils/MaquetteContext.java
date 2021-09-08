package maquette.testutils;

public final class MaquetteContext {

    public final Authorizations authorizations = Authorizations.apply();

    public final Users users = Users.apply();

    private MaquetteContext() {

    }

    public static MaquetteContext apply() {
        return new MaquetteContext();
    }

}
