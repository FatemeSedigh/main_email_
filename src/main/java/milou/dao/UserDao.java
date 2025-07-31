package milou.dao;

import milou.model.User;

import java.sql.SQLException;

public interface UserDao {
    User createUser(User user) throws SQLException;
    User getUserByEmail(String email) throws SQLException;
    boolean emailExists(String email) throws SQLException;
}