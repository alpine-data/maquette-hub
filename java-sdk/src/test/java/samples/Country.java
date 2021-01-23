package samples;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor(staticName = "apply")
public class Country {

    String name;

    String capital;

    int population;

    int area;

    public static List<Country> getSample() {
        return Lists.newArrayList(
            Country.apply("China", "Peking", 1433783686, 9388211),
            Country.apply("India", "New-Dehli", 1366417754, 2973190),
            Country.apply("United States", "Washington D.C", 329064917, 9147420),
            Country.apply("Brazil", "Sao Paolo", 211000000, 8400000),
            Country.apply("Russia", "Moscow", 146000000, 164000000),
            Country.apply("Germany", "Berlin", 80000000, 348560),
            Country.apply("Switzerland", "Berne", 8591000, 39516));
    }

}
