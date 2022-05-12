package maquette.core.common;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperatorsTest {

    @Test
    void dateStillValid() {
        var instantFrom = Instant.parse("2022-03-04T15:30:45.123Z");
        var instantTo = Instant.parse("2022-03-03T15:30:45.123Z");
        var duration = Duration.ofHours(2);
        assertFalse(Operators.isInstantWithinRange(instantFrom, instantTo, duration));
    }

    @Test
    void dateInvalidWithin2HoursAhead() {
        var instantFrom = Instant.parse("2022-03-01T16:30:45.123Z");
        var instantTo = Instant.parse("2022-03-01T15:30:45.123Z");
        var duration = Duration.ofHours(2);
        assertTrue(Operators.isInstantWithinRange(instantFrom, instantTo, duration));
    }

    @Test
    void dateInvalidWithin2Hours() {
        var instantFrom = Instant.parse("2022-03-03T14:15:45.123Z");
        var instantTo = Instant.parse("2022-03-03T15:30:45.123Z");
        var duration = Duration.ofHours(2);
        assertTrue(Operators.isInstantWithinRange(instantFrom, instantTo, duration));
    }

    @Test
    void dateInvalid() {
        var instantFrom = Instant.parse("2022-03-01T14:15:45.123Z");
        var instantTo = Instant.parse("2022-03-03T15:30:45.123Z");
        var duration = Duration.ofHours(2);
        assertTrue(Operators.isInstantWithinRange(instantFrom, instantTo, duration));
    }

}