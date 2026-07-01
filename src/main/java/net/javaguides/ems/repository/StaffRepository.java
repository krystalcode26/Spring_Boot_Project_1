package net.javaguides.ems.repository;

import net.javaguides.ems.entity.Staff;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

//Repository: Class + jdbc template
//StaffRepository CRUD - JdbcTemplate + ? placeholders (PreparedStatement)
@Repository
public class StaffRepository {
  //RowMapper-maps the result set to a Java object(manual mapping)
  /*
   * It converts each database row (ResultSet) into a Staff object,
   * similar to how JPA automatically maps rows to entities.
   * RowMapper is used only for mapping a database ResultSet into Java objects,
   * so it is typically used with SELECT queries.
   * */
  private static final RowMapper<Staff> ROW_MAPPER = (rs, rowNum) -> new Staff(
      rs.getLong("id"),
      rs.getString("first_name"),
      rs.getString("last_name"),
      rs.getString("email")
  );

  private final JdbcTemplate jdbcTemplate;

  public StaffRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Staff save(Staff staff) {
    if (staff.getId() == null) {
      return insert(staff);
    }
    update(staff);
    return staff;
  }

  public Optional<Staff> findById(Long id) {
    try {
      //queryForObject-executes a query and returns a single object
      //ROW_MAPPER-maps the result set to a Java object
      //returns an Optional of the staff

      Staff staff = jdbcTemplate.queryForObject(
          "SELECT id, first_name, last_name, email FROM staffs WHERE id = ?",
          ROW_MAPPER,
          id
      );
      //if the staff is not found, returns an empty Optional
      return Optional.ofNullable(staff);
    } catch (EmptyResultDataAccessException ex) {
      return Optional.empty();
    }
  }

  public List<Staff> findAll() {
    return jdbcTemplate.query(
        "SELECT id, first_name, last_name, email FROM staffs ORDER BY id",
        ROW_MAPPER
    );
  }

  public void deleteById(Long id) {
    jdbcTemplate.update("DELETE FROM staffs WHERE id = ?", id);
  }

//  public long count() {
//    Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM staffs", Long.class);
//    return count != null ? count : 0L;
//  }


  /*
  * An INSERT operation does not return a ResultSet; it only returns the number of affected rows
  * and optionally a generated key.
  * */
  private Staff insert(Staff staff) {
    //GeneratedKeyHolder reads PostgreSQL’s auto-generated id
    //KeyHolder-holds the generated key
    /* GeneratedKeyHolder is used to retrieve auto-generated primary keys from the database
     after an INSERT operation.
     */
    KeyHolder keyHolder = new GeneratedKeyHolder();
    //2. execute insert
    jdbcTemplate.update(connection -> {
      //3. Create PreparedStatement-prepared statement for the query,
      //Safer — values are sent separately from the SQL, prevent SQL Injection.
      //Template with ? placeholders
      PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO staffs (first_name, last_name, email) VALUES (?, ?, ?)",

          /* 5. Statement.RETURN_GENERATED_KEYS is not a Statement query.
          It is a constant (an int flag) telling prepareStatement:
          after this INSERT, give me the auto-generated id. -- Ex: generated id = 5
          It tells JDBC to return the generated ID, and Spring stores it in the KeyHolder.*/
          Statement.RETURN_GENERATED_KEYS
      );

      //4. set parameters
      /*
      INSERT INTO staffs (first_name,last_name,email) VALUES ('Tom','Lee','tom@gmail.com')
       */
      ps.setString(1, staff.getFirstName());
      ps.setString(2, staff.getLastName());
      ps.setString(3, staff.getEmail());

      //6: Return PreparedStatement - Spring executes it.
      return ps;
      //7: Pass KeyHolder
      /*  Execute INSERT, Get generated id, Store id in keyHolder */
    }, keyHolder);

    //8: Read Generated ID  Ex: key = 5
    Number key = keyHolder.getKey();
    if (key != null) {
      //9: Set Back to Java Object Entity, Before insert: Staff{id=null}, After insert: Staff{id=5}
      staff.setId(key.longValue());
    }
    return staff;
  }

  private void update(Staff staff) {
    jdbcTemplate.update(
        "UPDATE staffs SET first_name = ?, last_name = ?, email = ? WHERE id = ?",
        staff.getFirstName(),
        staff.getLastName(),
        staff.getEmail(),
        staff.getId()
    );
  }
}
