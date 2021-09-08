package maquette.datashop.providers;

import lombok.AllArgsConstructor;

/**
 * A fake data asset provider implementation w/o any function.
 */
@AllArgsConstructor(staticName = "apply")
public final class FakeProvider implements DataAssetProvider {

    public static final String NAME = "fake";

    @Override
    public String getType() {
        return NAME;
    }

}
