package milou.menu;

import milou.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class View {
    private SessionFactory sessionFactory;
    private User currentUser;

    public View(SessionFactory sessionFactory, User currentUser) {
        this.sessionFactory = sessionFactory;
        this.currentUser = currentUser;
    }

    public void showEmails() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\n[A]ll emails, [U]nread emails, [S]ent emails, Read by [C]ode: ");
        String choice = scanner.nextLine().trim().toUpperCase();

        switch (choice) {
            case "A":
                showAllEmails();
                break;
            case "U":
                showUnreadEmails();
                break;
            case "S":
                showSentEmails();
                break;
            case "C":
                readByCode();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void showAllEmails() {
        try (Session session = sessionFactory.openSession()) {
            List<EmailRecipient> emails = session.createQuery(
                            "FROM EmailRecipient WHERE recipient.id = :userId ORDER BY email.sentDate DESC, email.sentTime DESC",
                            EmailRecipient.class)
                    .setParameter("userId", currentUser.getId())
                    .getResultList();

            displayEmails("All Emails", emails);
        }
    }

    private void showUnreadEmails() {
        try (Session session = sessionFactory.openSession()) {
            List<EmailRecipient> emails = session.createQuery(
                            "FROM EmailRecipient WHERE recipient.id = :userId AND isRead = false ORDER BY email.sentDate DESC, email.sentTime DESC",
                            EmailRecipient.class)
                    .setParameter("userId", currentUser.getId())
                    .getResultList();

            displayEmails("Unread Emails", emails);
        }
    }

    private void showSentEmails() {
        try (Session session = sessionFactory.openSession()) {
            List<Email> emails = session.createQuery(
                            "FROM Email WHERE sender.id = :userId ORDER BY sentDate DESC, sentTime DESC",
                            Email.class)
                    .setParameter("userId", currentUser.getId())
                    .getResultList();

            System.out.println("\nSent Emails:");
            for (Email email : emails) {
                List<EmailRecipient> recipients = email.getRecipients();
                String recipientList = String.join(", ",
                        recipients.stream()
                                .map(er -> er.getRecipient().getEmail())
                                .toArray(String[]::new));

                System.out.println("+ " + recipientList + " - " + email.getSubject() + " (" + email.getCode() + ")");
            }
        }
    }

    private void readByCode() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Code: ");
        String code = scanner.nextLine().trim();

        try (Session session = sessionFactory.openSession()) {
            Email email = session.createQuery(
                            "FROM Email WHERE code = :code", Email.class)
                    .setParameter("code", code)
                    .uniqueResult();

            if (email != null) {
                // Check if current user is sender or recipient
                boolean canRead = email.getSender().getId() == currentUser.getId() ||
                        session.createQuery(
                                        "FROM EmailRecipient WHERE email.id = :emailId AND recipient.id = :userId",
                                        EmailRecipient.class)
                                .setParameter("emailId", email.getId())
                                .setParameter("userId", currentUser.getId())
                                .uniqueResult() != null;

                if (canRead) {
                    displayFullEmail(email);

                    // Mark as read if recipient
                    if (email.getSender().getId() != currentUser.getId()) {
                        Transaction transaction = session.beginTransaction();
                        EmailRecipient er = session.createQuery(
                                        "FROM EmailRecipient WHERE email.id = :emailId AND recipient.id = :userId",
                                        EmailRecipient.class)
                                .setParameter("emailId", email.getId())
                                .setParameter("userId", currentUser.getId())
                                .uniqueResult();

                        if (er != null) {
                            er.setRead(true);
                            session.update(er);
                        }
                        transaction.commit();
                    }
                } else {
                    System.out.println("You cannot read this email.");
                }
            } else {
                System.out.println("Email not found.");
            }
        }
    }

    private void displayEmails(String title, List<EmailRecipient> emails) {
        System.out.println("\n" + title + ":");
        if (emails.isEmpty()) {
            System.out.println("No emails found.");
        } else {
            for (EmailRecipient er : emails) {
                Email email = er.getEmail();
                System.out.println("+ " + email.getSender().getEmail() + " - " +
                        email.getSubject() + " (" + email.getCode() + ")");
            }
        }
    }

    private void displayFullEmail(Email email) {
        List<String> recipients = new ArrayList<>();
        for (EmailRecipient er : email.getRecipients()) {
            recipients.add(er.getRecipient().getEmail());
        }

        System.out.println("\nCode: " + email.getCode());
        System.out.println("Recipient(s): " + String.join(", ", recipients));
        System.out.println("Subject: " + email.getSubject());
        System.out.println("Date: " + email.getSentDate());
        System.out.println("\n" + email.getBody());
    }
}
