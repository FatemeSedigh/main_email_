package milou.menu;

import milou.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.Scanner;
import java.util.UUID;

public class Forward {
    private SessionFactory sessionFactory;
    private User currentUser;

    public Forward(SessionFactory sessionFactory, User currentUser) {
        this.sessionFactory = sessionFactory;
        this.currentUser = currentUser;
    }

    public void forwardEmail() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Code: ");
        String code = scanner.nextLine().trim();

        System.out.print("Recipient(s) (comma separated): ");
        String recipientsInput = scanner.nextLine().trim();

        try (Session session = sessionFactory.openSession()) {
            Email originalEmail = session.createQuery(
                            "FROM Email WHERE code = :code", Email.class)
                    .setParameter("code", code)
                    .uniqueResult();

            if (originalEmail == null) {
                System.out.println("Email not found.");
                return;
            }

            // Check if user can forward (is sender or recipient)
            boolean canForward = originalEmail.getSender().equals(currentUser) ||
                    session.createQuery(
                                    "FROM EmailRecipient WHERE email.id = :emailId AND recipient.id = :userId",
                                    EmailRecipient.class)
                            .setParameter("emailId", originalEmail.getId())
                            .setParameter("userId", currentUser.getId())
                            .uniqueResult() != null;

            if (!canForward) {
                System.out.println("You cannot forward this email.");
                return;
            }

            // Generate new code
            String newCode = UUID.randomUUID().toString().substring(0, 6);

            Transaction transaction = session.beginTransaction();

            // Create forwarded email
            Email forwardedEmail = new Email(
                    currentUser,
                    newCode,
                    "[Fw] " + originalEmail.getSubject(),
                    originalEmail.getBody()
            );
            session.persist(forwardedEmail);

            // Process new recipients
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
                    EmailRecipient emailRecipient = new EmailRecipient(forwardedEmail, recipient);
                    session.persist(emailRecipient);
                } else {
                    System.out.println("User " + recipientEmail + " not found. Skipping...");
                }
            }

            transaction.commit();

            System.out.println("\nSuccessfully forwarded your email.");
            System.out.println("Code: " + newCode);
        }
    }
}