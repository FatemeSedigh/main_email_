package milou.model;

import jakarta.persistence.*;

@Entity
@Table(name = "email_recipients")
@IdClass(EmailRecipientId.class)
public class EmailRecipient {
    @Id
    @ManyToOne
    @JoinColumn(name = "email_id", nullable = false)
    private Email email;

    @Id
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(name = "is_read", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isRead;

    // Constructors
    public EmailRecipient() {
    }

    public EmailRecipient(Email email, User recipient) {
        this.email = email;
        this.recipient = recipient;
        this.isRead = false;
    }

    // Getters and Setters
    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public String toString() {
        return "EmailRecipient{" +
                "email=" + email.getId() +
                ", recipient=" + recipient.getEmail() +
                ", isRead=" + isRead +
                '}';
    }
}