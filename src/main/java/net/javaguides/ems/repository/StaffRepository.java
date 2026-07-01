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

  public long count() {
    Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM staffs", Long.class);
    return count != null ? count : 0L;
  }

  private Staff insert(Staff staff) {
    //GeneratedKeyHolder reads PostgreSQL’s auto-generated id
    //KeyHolder-holds the generated key
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      //PreparedStatement-prepared statement for the query,
      //Safer — values are sent separately from the SQL
      //Template with ? placeholders
      PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO staffs (first_name, last_name, email) VALUES (?, ?, ?)",

          //Statement.RETURN_GENERATED_KEYS is not a Statement query.
          //It is a constant (an int flag) telling prepareStatement: 
          // after this INSERT, give me the auto-generated id.
          Statement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, staff.getFirstName());
      ps.setString(2, staff.getLastName());
      ps.setString(3, staff.getEmail());
      return ps;
    }, keyHolder);

    Number key = keyHolder.getKey();
    if (key != null) {
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
