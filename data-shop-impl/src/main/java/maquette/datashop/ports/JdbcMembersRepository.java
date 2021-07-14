package maquette.datashop.ports;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import maquette.core.common.Operators;
import maquette.core.common.Templates;
import maquette.core.ports.HasMembers;
import maquette.core.values.ActionMetadata;
import maquette.core.values.UID;
import maquette.core.values.authorization.Authorization;
import maquette.core.values.authorization.GrantedAuthorization;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public final class JdbcMembersRepository<T extends Enum<T>> implements HasMembers<T> {

   private final String typeName;

   private final Jdbi jdbi;

   private final ObjectMapper om;

   private final Function<T, String> mapRoleToString;

   private final Function<String, T> mapStringToRole;

   private JdbcMembersRepository(String typeName, Jdbi jdbi, ObjectMapper om, Function<T, String> mapRoleToString, Function<String, T> mapStringToRole) {
      this.typeName = typeName;
      this.jdbi = jdbi;
      this.om = om;
      this.mapRoleToString = mapRoleToString;
      this.mapStringToRole = mapStringToRole;
   }

   public static <T extends Enum<T>> JdbcMembersRepository<T> apply(String typeName, Jdbi jdbi, ObjectMapper om, Function<T, String> mapRoleToString, Function<String, T> mapStringToRole) {
      return new JdbcMembersRepository<T>(typeName, jdbi, om, mapRoleToString, mapStringToRole);
   }

   @Override
   public CompletionStage<List<GrantedAuthorization<T>>> findAllMembers(UID parent) {
      var query = Templates.renderTemplateFromResources("db/sql/members/find-members-by-parent.sql");

      var result = jdbi.withHandle(handle -> handle
         .createQuery(query)
         .bind("type", typeName)
         .bind("parent", parent.getValue())
         .map(new GrantedAuthorizationMapper()))
         .list();

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<List<GrantedAuthorization<T>>> findMembersByRole(UID parent, T role) {
      var query = Templates.renderTemplateFromResources("db/sql/members/find-members-by-parent-and-role.sql");

      var result = jdbi.withHandle(handle -> handle
         .createQuery(query)
         .bind("type", typeName)
         .bind("parent", parent.getValue())
         .bind("role", mapRoleToString.apply(role))
         .map(new GrantedAuthorizationMapper()))
         .list();

      return CompletableFuture.completedFuture(result);
   }

   @Override
   public CompletionStage<Done> insertOrUpdateMember(UID parent, GrantedAuthorization<T> member) {
      var query = Templates.renderTemplateFromResources("db/sql/members/insert-member.sql");

      jdbi.withHandle(handle -> handle
         .createUpdate(query)
         .bind("type", typeName)
         .bind("parent", parent.getValue())
         .bind("granted_by", member.getGranted().getBy())
         .bind("granted_at", member.getGranted().getAt())
         .bind("role", mapRoleToString.apply(member.getRole()))
         .bind("auth", Operators.suppressExceptions(() -> om.writeValueAsString(member.getAuthorization())))
         .execute());

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   @Override
   public CompletionStage<Done> removeMember(UID parent, Authorization member) {
      var query = Templates.renderTemplateFromResources("db/sql/members/remove-member.sql");

      jdbi.withHandle(handle -> handle
         .createUpdate(query)
         .bind("type", typeName)
         .bind("parent", parent.getValue())
         .bind("auth", Operators.suppressExceptions(() -> om.writeValueAsString(member)))
         .execute());

      return CompletableFuture.completedFuture(Done.getInstance());
   }

   private class GrantedAuthorizationMapper implements RowMapper<GrantedAuthorization<T>> {

      @Override
      public GrantedAuthorization<T> map(ResultSet rs, StatementContext ctx) throws SQLException {
         var action = ActionMetadata.apply(rs.getString("granted_by"), rs.getDate("granted_at").toInstant());
         var authorization = Operators.suppressExceptions(() -> om.readValue(rs.getString("auth"), Authorization.class));
         var role = mapStringToRole.apply(rs.getString("role"));
         return GrantedAuthorization.apply(action, authorization, role);
      }

   }

}
