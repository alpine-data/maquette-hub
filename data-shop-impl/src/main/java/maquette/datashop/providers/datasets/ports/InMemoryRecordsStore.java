package maquette.datashop.providers.datasets.ports;

import akka.Done;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import maquette.datashop.providers.datasets.records.Records;
import org.apache.avro.generic.GenericData;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@AllArgsConstructor(staticName = "apply")
public final class InMemoryRecordsStore implements RecordsStore {

    private final Map<String, Records> records;

    public static InMemoryRecordsStore apply() {
        return apply(Maps.newHashMap());
    }

    @Override
    public CompletionStage<Done> append(String key, Records records) {
        if (this.records.containsKey(key)) {
            var existing = this.records
                .get(key)
                .getRecords();
            var merged = Lists.<GenericData.Record>newArrayList();
            merged.addAll(existing);
            merged.addAll(records.getRecords());

            this.records.put(key, Records.fromRecords(merged));
        } else {
            this.records.put(key, Records.fromRecords(records.getRecords()));
        }

        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Done> clear(String key) {
        records.remove(key);
        return CompletableFuture.completedFuture(Done.getInstance());
    }

    @Override
    public CompletionStage<Records> get(String key) {
        if (records.containsKey(key)) {
            return CompletableFuture.completedFuture(records.get(key));
        } else {
            return CompletableFuture.completedFuture(Records.empty());
        }
    }

}
