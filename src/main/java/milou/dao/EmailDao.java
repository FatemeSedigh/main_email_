package milou.dao;

import milou.model.Email;
import milou.model.User;
import java.sql.SQLException;
import java.util.List;

public interface EmailDao {
    Email createEmail(Email email, List<User> recipients) throws SQLException;
    List<Email> getReceivedEmails(int userId, boolean onlyUnread) throws SQLException;
    List<Email> getSentEmails(int userId) throws SQLException;
    Email getEmailByCode(String code) throws SQLException;
    void markAsRead(int emailId, int userId) throws SQLException;
    boolean canUserReadEmail(int emailId, int userId) throws SQLException;
}
