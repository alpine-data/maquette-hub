package test;

import org.junit.Test;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TableTest {

    @Test
    public void test() {
        var table = Table
                .create("hallo")
                .addColumns(StringColumn.create("firstname"))
                .addColumns(StringColumn.create("lastname"))
                .addColumns(DateTimeColumn.create("created"));

        var row = table.appendRow();
        row.setString("firstname", "Micky");
        row.setString("lastname", "Mouse");
        row.setDateTime("created", LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

        System.out.println(table.write().toString("csv"));

        System.out.println("----");
        System.out.println(table.printAll());
        /*
        Table.defaultWriterRegistry.registerExtension("ascii", new DataWriter<WriteOptions>() {
            @Override
            public void write(Table table, Destination dest) throws IOException {
                table.
            }

            @Override
            public void write(Table table, WriteOptions options) throws IOException {
                write(table, options.destination());
            }
        });

        table.write().toWriter(new StringWriter(), "csv");
         */
    }

}
