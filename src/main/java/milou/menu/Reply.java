package milou.menu;

import milou.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
import java.util.UUID;

public class Reply {
    private SessionFactory sessionFactory;
    private User currentUser;

    public Reply(SessionFactory sessionFactory, User currentUser) {
        this.sessionFactory = sessionFactory;
        this.currentUser = currentUser;
    }

    public void replyToEmail() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Code: ");
        String code = scanner.nextLine().trim();

        System.out.print("Body: ");
        String body = scanner.nextLine().trim();

        try (Session session = sessionFactory.openSession()) {
            Email originalEmail = session.createQuery(
                            "FROM Email WHERE code = :code", Email.class)
                    .setParameter("code", code)
                    .uniqueResult();

            if (originalEmail == null) {
                System.out.println("Email not found.");
                return;
            }

            // Check if user can reply (is recipient)
            boolean canReply = session.createQuery(
                            "FROM EmailRecipient WHERE email.id = :emailId AND recipient.id = :userId",
                            EmailRecipient.class)
                    .setParameter("emailId", originalEmail.getId())
                    .setParameter("userId", currentUser.getId())
                    .uniqueResult() != null;

            if (!canReply) {
                System.out.println("You cannot reply to this email.");
                return;
            }

            // Generate new code
            String newCode = UUID.randomUUID().toString().substring(0, 6);

            Transaction transaction = session.beginTransaction();

            // Create reply email
            Email replyEmail = new Email(
                    currentUser,
                    newCode,
                    "[Re] " + originalEmail.getSubject(),
                    body
            );
            session.persist(replyEmail);

            // Add recipients (original sender + all recipients)
            List<User> recipients = new ArrayList<>();
            recipients.add(originalEmail.getSender());

            for (EmailRecipient er : originalEmail.getRecipients()) {
                if (!er.getRecipient().equals(currentUser)) {
                    recipients.add(er.getRecipient());
                }
            }

            // Remove duplicates
            recipients = recipients.stream().distinct().toList();

            for (User recipient : recipients) {
                EmailRecipient emailRecipient = new EmailRecipient(replyEmail, recipient);
                session.persist(emailRecipient);
            }

            transaction.commit();

            System.out.println("\nSuccessfully sent your reply to email " + code + ".");
            System.out.println("Code: " + newCode);
        }
    }
}
