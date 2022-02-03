package maquette.datashop.providers.databases.ports;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import maquette.core.common.Operators;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecordBuilder;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import static java.sql.Types.*;

public final class JdbcAvroHelper {

   static final int MAX_DIGITS_BIGINT = 19;

   private JdbcAvroHelper() {

   }

   @AllArgsConstructor(staticName = "apply")
   private static class Mapper {

      String fieldName;

      Operators.ExceptionalFunction<ResultSet, Object> readValue;

      public static Mapper apply(ResultSetMetaData meta, int columnIndex) {
         return Operators.suppressExceptions(() -> {
            final String columnName;

            if (meta.getColumnName(columnIndex).isEmpty()) {
               columnName = meta.getColumnLabel(columnIndex);
            } else {
               columnName = meta.getColumnName(columnIndex);
            }

            var columnType = meta.getColumnType(columnIndex);
            var precision = meta.getPrecision(columnIndex);

            var fieldName = normalizeForAvro(columnName);
            var readValue = fieldAvroValueMapper(columnType, columnIndex, precision);
            return apply(fieldName, readValue);
         });
      }

      public void setField(GenericRecordBuilder builder, ResultSet rs) {
         builder.set(fieldName, Operators.ignoreExceptionsWithDefault(() -> readValue.apply(rs), null));
      }

   }

   public static List<Mapper> createRecordMappers(ResultSet rs) {
      return Operators.suppressExceptions(() -> {
         var meta = rs.getMetaData();
         var mappings = Lists.<Mapper>newArrayList();

         for (int i = 1; i <= meta.getColumnCount(); i++) {
            mappings.add(Mapper.apply(meta, i));
         }

         return mappings;
      });
   }

   public static GenericData.Record createRecord(ResultSet rs, Schema schema, List<Mapper> mappings) {
      return Operators.suppressExceptions(() -> {
         var builder = new GenericRecordBuilder(schema);

         mappings.forEach(mapping -> {
            if (schema.getField(mapping.fieldName) != null) {
               mapping.setField(builder, rs);
            }
         });

         return builder.build();
      });
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
            builder.name(normalizeForAvro(columnName)).prop("column_name", columnName));
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

   private static ByteBuffer nullableBytes(final byte[] bts) {
      if (bts != null) {
         return ByteBuffer.wrap(bts);
      } else {
         return null;
      }
   }

   private static Operators.ExceptionalFunction<ResultSet, Object> fieldAvroValueMapper(
      int columnType,
      int columnIndex,
      int precision) {

      switch (columnType) {
         case BIGINT:
            if (precision > 0 && precision <= MAX_DIGITS_BIGINT) {
               return rs -> rs.getLong(columnIndex);
            } else {
               return rs -> rs.getString(columnIndex);
            }
         case INTEGER:
         case SMALLINT:
         case TINYINT:
            return rs -> rs.getInt(columnIndex);
         case TIMESTAMP:
         case DATE:
         case TIME:
         case TIME_WITH_TIMEZONE:
            return rs -> rs.getLong(columnIndex);
         case BOOLEAN:
            return rs -> rs.getBoolean(columnIndex);
         case BIT:
            if (precision <= 1) {
               return rs -> rs.getBoolean(columnIndex);
            } else {
               return rs -> nullableBytes(rs.getBytes(columnIndex));
            }
         case BINARY:
         case VARBINARY:
         case LONGVARBINARY:
         case ARRAY:
         case BLOB:
            return rs -> nullableBytes(rs.getBytes(columnIndex));
         case DOUBLE:
            return rs -> rs.getDouble(columnIndex);
         case FLOAT:
         case REAL:
            return rs -> rs.getFloat(columnIndex);
         default:
            return rs -> rs.getString(columnIndex);
      }
   }

   private static String getDatabaseTableName(final ResultSetMetaData meta) throws SQLException {
      if (meta.getColumnCount() > 0) {
         var result =  normalizeForAvro(meta.getTableName(1));

         if (result.trim().length() == 0) {
            result = "no_table_name";
         }

         return result;
      } else {
         return "no_table_name";
      }
   }

   private static String normalizeForAvro(final String input) {
      return input.replaceAll("[^A-Za-z0-9_]", "_");
   }

}
