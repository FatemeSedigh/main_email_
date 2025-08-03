package milou.menu;

import milou.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.Scanner;
import java.util.List;
import java.util.stream.Collectors;

public class View {
    private final SessionFactory sessionFactory;
    private final User currentUser;

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
                            "FROM EmailRecipient er " +
                                    "WHERE er.recipient.id = :userId " +
                                    "AND er.email.sender.id != :userId " + // این شرط جدید
                                    "ORDER BY er.email.sentDate DESC, er.email.sentTime DESC",
                            EmailRecipient.class)
                    .setParameter("userId", currentUser.getId())
                    .getResultList();

            displayEmails("All Emails", emails);
        }
    }

    private void showUnreadEmails() {
        try (Session session = sessionFactory.openSession()) {
            List<EmailRecipient> emails = session.createQuery(
                            "FROM EmailRecipient er " +
                                    "WHERE er.recipient.id = :userId " +
                                    "AND er.isRead = false " +
                                    "AND er.email.sender.id != :userId " + // این شرط جدید
                                    "ORDER BY er.email.sentDate DESC, er.email.sentTime DESC",
                            EmailRecipient.class)
                    .setParameter("userId", currentUser.getId())
                    .getResultList();

            displayEmails("Unread Emails", emails);
        }
    }

    private void showSentEmails() {
        try (Session session = sessionFactory.openSession()) {
            List<Email> emails = session.createQuery(
                            "FROM Email e " +
                                    "WHERE e.sender.id = :userId " +
                                    "ORDER BY e.sentDate DESC, e.sentTime DESC",
                            Email.class)
                    .setParameter("userId", currentUser.getId())
                    .getResultList();

            System.out.println("\nSent Emails:");
            for (Email email : emails) {
                String recipientList = email.getRecipients().stream()
                        .map(er -> er.getRecipient().getEmail())
                        .collect(Collectors.joining(", "));

                System.out.println("+ To: " + recipientList + " - " +
                        email.getSubject() + " (" + email.getCode() + ")");
            }
        }
    }

    private void readByCode() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Code: ");
        String code = scanner.nextLine().trim();

        try (Session session = sessionFactory.openSession()) {
            Email email = session.createQuery(
                            "FROM Email e WHERE e.code = :code", Email.class)
                    .setParameter("code", code)
                    .uniqueResult();

            if (email != null) {
                boolean canRead = email.getSender().getId() == currentUser.getId() ||
                        session.createQuery(
                                        "FROM EmailRecipient er " +
                                                "WHERE er.email.id = :emailId " +
                                                "AND er.recipient.id = :userId",
                                        EmailRecipient.class)
                                .setParameter("emailId", email.getId())
                                .setParameter("userId", currentUser.getId())
                                .uniqueResult() != null;

                if (canRead) {
                    displayFullEmail(email, session);

                    if (email.getSender().getId() != currentUser.getId()) {
                        markAsRead(email, session);
                    }
                } else {
                    System.out.println("You cannot read this email.");
                }
            } else {
                System.out.println("Email not found.");
            }
        }
    }

    private void markAsRead(Email email, Session session) {
        Transaction transaction = session.beginTransaction();
        try {
            EmailRecipient er = session.createQuery(
                            "FROM EmailRecipient er " +
                                    "WHERE er.email.id = :emailId " +
                                    "AND er.recipient.id = :userId",
                            EmailRecipient.class)
                    .setParameter("emailId", email.getId())
                    .setParameter("userId", currentUser.getId())
                    .uniqueResult();

            if (er != null && !er.isRead()) {
                er.setRead(true);
                session.merge(er);
                transaction.commit();
            }
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    private void displayEmails(String title, List<EmailRecipient> emails) {
        System.out.println("\n" + title + ":");
        if (emails.isEmpty()) {
            System.out.println("No emails found.");
        } else {
            emails.forEach(er -> {
                Email email = er.getEmail();
                System.out.println("+ " + email.getSender().getEmail() + " - " +
                        email.getSubject() + " (" + email.getCode() + ")");
            });
        }
    }

    private void displayFullEmail(Email email, Session session) {
        String recipients = email.getRecipients().stream()
                .map(er -> er.getRecipient().getEmail())
                .collect(Collectors.joining(", "));

        System.out.println("\nCode: " + email.getCode());
        System.out.println("From: " + email.getSender().getEmail());
        System.out.println("To: " + recipients);
        System.out.println("Subject: " + email.getSubject());
        System.out.println("Date: " + email.getSentDate());
        System.out.println("\n" + email.getBody());
    }
}