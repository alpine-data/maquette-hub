package maquette.datashop.providers.datasets.records;

import java.util.List;

import lombok.AllArgsConstructor;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecordBuilder;

import com.google.common.collect.Lists;

@AllArgsConstructor(staticName = "apply")
public final class SampleRecords {

    private static final String NAME = "name";
    private static final String CAPITAL = "capital";
    private static final String CITIZENS = "citizens";

    public Records getCountryRecords() {
        List<GenericData.Record> records = Lists.newArrayList();
        records.add(createCountryRecord("Germany", "Berlin", 80000000));
        records.add(createCountryRecord("Switzerland", "Berne", 16000000));
        records.add(createCountryRecord("Austria", "Vienna", 16000000));
        records.add(createCountryRecord("France", "Paris", 60000000));
        records.add(createCountryRecord("Italy", "Rome", 40000000));
        return Records.fromRecords(records);
    }

    public Records getCityRecords() {
        List<GenericData.Record> records = Lists.newArrayList();
        records.add(createCityRecord("Plauen", 68000));
        records.add(createCityRecord("Munich", 1400000));
        records.add(createCityRecord("Stuttgart", 600000));
        records.add(createCityRecord("Merzig", 60000));
        return Records.fromRecords(records);
    }

    private static Schema getCountrySchema() {
        return SchemaBuilder
            .record("country")
            .fields()
            .requiredString(NAME)
            .requiredString(CAPITAL)
            .requiredInt(CITIZENS)
            .endRecord();
    }

    private static Schema getCitySchema() {
        return SchemaBuilder
            .record("city")
            .fields()
            .requiredString(NAME)
            .requiredInt(CITIZENS)
            .endRecord();
    }

    private static GenericData.Record createCountryRecord(String name, String capital, int citizens) {
        return new GenericRecordBuilder(getCountrySchema())
            .set(NAME, name)
            .set(CAPITAL, capital)
            .set(CITIZENS, citizens)
            .build();
    }

    private static GenericData.Record createCityRecord(String name, int citizens) {
        return new GenericRecordBuilder(getCitySchema())
            .set(NAME, name)
            .set(CITIZENS, citizens)
            .build();
    }

}
