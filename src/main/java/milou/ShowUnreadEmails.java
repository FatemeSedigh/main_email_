package milou;

import milou.model.Email;
import milou.model.EmailRecipient;
import milou.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import java.util.List;

public class ShowUnreadEmails {
    private final SessionFactory sessionFactory;

    public ShowUnreadEmails(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void show(User user) {
        try (Session session = sessionFactory.openSession()) {
            Query<EmailRecipient> query = session.createQuery(
                    "FROM EmailRecipient WHERE recipient.id = :userId AND isRead = false ORDER BY email.sentDate DESC, email.sentTime DESC",
                    EmailRecipient.class);
            query.setParameter("userId", user.getId());

            List<EmailRecipient> unreadEmails = query.getResultList();

            if (unreadEmails.isEmpty()) {
                System.out.println("No unread emails.");
            } else {
                System.out.println("\nUnread Emails:");
                System.out.println(unreadEmails.size() + " unread emails:");
                for (EmailRecipient er : unreadEmails) {
                    Email email = er.getEmail();
                    System.out.println("+ " + email.getSender().getEmail() + " - " +
                            email.getSubject() + " (" + email.getCode() + ")");
                }
            }
        }
    }
}