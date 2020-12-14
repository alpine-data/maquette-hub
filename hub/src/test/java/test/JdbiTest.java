package test;

import lombok.*;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class JdbiTest {

   @Data
   @AllArgsConstructor
   @NoArgsConstructor(force = true)
   public static class User {

      int id;

      String name;

   }

   @Test
   public void test() {
      Jdbi jdbi = Jdbi.create("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");

      List<User> users = jdbi.withHandle(handle -> {
         /*
         handle.execute("CREATE TABLE users (id INTEGER PRIMARY KEY, name VARCHAR)");

         // Inline positional parameters
         handle.execute("INSERT INTO users(id, name) VALUES (?, ?)", 0, "Alice");

         // Positional parameters
         handle.createUpdate("INSERT INTO users(id, name) VALUES (?, ?)")
            .bind(0, 1) // 0-based parameter indexes
            .bind(1, "Bob")
            .execute();

         // Named parameters
         handle.createUpdate("INSERT INTO users(id, name) VALUES (:id, :name)")
            .bind("id", 2)
            .bind("name", "Clarice")
            .execute();

         // Named parameters from bean properties
         handle.createUpdate("INSERT INTO users(id, name) VALUES (:id, :name)")
            .bindBean(new User(3, "David"))
            .execute();
          */

         // Easy mapping to any type
         return handle.createQuery("SELECT * FROM users ORDER BY name")
            .map(new RowMapper<User>() {
               @Override
               public User map(ResultSet rs, StatementContext ctx) throws SQLException {
                  return new User(rs.getInt(1), rs.getString(2));
               }
            })
            .collect(Collectors.toList());
      });

      /*
      assertThat(users).containsExactly(
         new User(0, "Alice"),
         new User(1, "Bob"),
         new User(2, "Clarice"),
         new User(3, "David"));/*


       */
      System.out.println(users);
   }

}
