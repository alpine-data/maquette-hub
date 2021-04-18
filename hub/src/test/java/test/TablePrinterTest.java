package test;

import maquette.core.server.results.TablePrinter;
import org.junit.Test;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class TablePrinterTest {

   @Test
   public void test() {
      var table = Table
         .create()
         .addColumns(StringColumn.create("name"))
         .addColumns(StringColumn.create("description"))
         .addColumns(StringColumn.create("repository"));

      var row = table.appendRow();
      row.setString("name", "entry-1");
      row.setString("description", "lorem ipsum dolor");
      row.setString("repository", "foo bar");

      row = table.appendRow();
      row.setString("name", "entry-2");
      row.setString("description", "lorem ipsum dolor skdjfn askd");
      row.setString("repository", "foo barsdlk kfkasmf");

      new TablePrinter(table.rowCount(), System.out).printRelation(table);
   }

}
