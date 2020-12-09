package maquette.adapters.jdbc;

import maquette.common.Operators;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static java.sql.Types.*;

public final class JdbcAvroHelper {

   static final int MAX_DIGITS_BIGINT = 19;

   private JdbcAvroHelper() {

   }

   public static Schema createAvroSchema(final ResultSet resultSet) {
      return Operators.suppressExceptions(() -> {
         final ResultSetMetaData meta = resultSet.getMetaData();
         final String tableName = getDatabaseTableName(meta);

         final SchemaBuilder.FieldAssembler<Schema> builder =
            SchemaBuilder
               .record(tableName)
               .namespace("ai.maquette")
               .fields();

         return createAvroFields(meta, builder).endRecord();
      });
   }

   private static SchemaBuilder.FieldAssembler<Schema> createAvroFields(
      final ResultSetMetaData meta,
      final SchemaBuilder.FieldAssembler<Schema> builder)
      throws SQLException {

      for (int i = 1; i <= meta.getColumnCount(); i++) {
         final String columnName;
         if (meta.getColumnName(i).isEmpty()) {
            columnName = meta.getColumnLabel(i);
         } else {
            columnName = meta.getColumnName(i);
         }

         int columnType = meta.getColumnType(i);
         fieldAvroType(
            columnType, meta.getPrecision(i),
            builder.name(normalizeForAvro(columnName)));
      }

      return builder;
   }

   private static void fieldAvroType(
      final int columnType,
      final int precision,
      final SchemaBuilder.FieldBuilder<Schema> fieldBuilder) {

      final SchemaBuilder.BaseTypeBuilder<
         SchemaBuilder.UnionAccumulator<SchemaBuilder.NullDefault<Schema>>>
         field = fieldBuilder.type().unionOf().nullBuilder().endNull().and();

      switch (columnType) {
         case BIGINT:
            if (precision > 0 && precision <= MAX_DIGITS_BIGINT) {
               field.longType().endUnion().nullDefault();
            } else {
               field.stringType().endUnion().nullDefault();
            }
            break;
         case INTEGER:
         case SMALLINT:
         case TINYINT:
            field.intType().endUnion().nullDefault();
            break;
         case TIMESTAMP:
         case DATE:
         case TIME:
         case TIME_WITH_TIMEZONE:
            field.longType().endUnion().nullDefault();
            break;
         case BOOLEAN:
            field.booleanType().endUnion().nullDefault();
            break;
         case BIT:
            if (precision <= 1) {
               field.booleanType().endUnion().nullDefault();
            } else {
               field.bytesType().endUnion().nullDefault();
            }
            break;
         case BINARY:
         case VARBINARY:
         case LONGVARBINARY:
         case ARRAY:
         case BLOB:
            field.bytesType().endUnion().nullDefault();
            break;
         case DOUBLE:
            field.doubleType().endUnion().nullDefault();
            break;
         case FLOAT:
         case REAL:
            field.floatType().endUnion().nullDefault();
            break;
         default:
            field.stringType().endUnion().nullDefault();
            break;
      }
   }

   private static String getDatabaseTableName(final ResultSetMetaData meta) throws SQLException {
      if (meta.getColumnCount() > 0) {
         return normalizeForAvro(meta.getTableName(1));
      } else {
         return "no_table_name";
      }
   }

   private static String normalizeForAvro(final String input) {
      return input.replaceAll("[^A-Za-z0-9_]", "_");
   }

}
