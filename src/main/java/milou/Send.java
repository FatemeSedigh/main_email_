package milou;

import milou.model.Email;
import milou.model.EmailRecipient;
import milou.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.Scanner;
import java.util.UUID;

public class Send {
    private SessionFactory sessionFactory;
    private User currentUser;

    public Send(SessionFactory sessionFactory, User currentUser) {
        this.sessionFactory = sessionFactory;
        this.currentUser = currentUser;
    }

    public void sendEmail() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Recipient(s) (comma separated): ");
        String recipientsInput = scanner.nextLine().trim();

        System.out.print("Subject: ");
        String subject = scanner.nextLine().trim();

        System.out.print("Body: ");
        String body = scanner.nextLine().trim();

        // Generate unique 6-character code
        String code = UUID.randomUUID().toString().substring(0, 6);

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            // Create new email
            Email email = new Email(currentUser, code, subject, body);
            session.persist(email);

            // Process recipients
            String[] recipientEmails = recipientsInput.split(",");
            for (String recipientEmail : recipientEmails) {
                recipientEmail = recipientEmail.trim();
                if (!recipientEmail.contains("@")) {
                    recipientEmail += "@milou.com";
                }

                // Find recipient user
                User recipient = session.createQuery("FROM User WHERE email = :email", User.class)
                        .setParameter("email", recipientEmail)
                        .uniqueResult();

                if (recipient != null) {
                    EmailRecipient emailRecipient = new EmailRecipient(email, recipient);
                    session.persist(emailRecipient);
                } else {
                    System.out.println("User " + recipientEmail + " not found. Skipping...");
                }
            }

            transaction.commit();

            System.out.println("\nSuccessfully sent your email.");
            System.out.println("Code: " + code);
        }
    }
}