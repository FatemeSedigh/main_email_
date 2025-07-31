package milou.model;

import java.io.Serializable;
import java.util.Objects;

public class EmailRecipientId implements Serializable {
    private Long email;
    private Long recipient;

    // Constructors
    public EmailRecipientId() {
    }

    public EmailRecipientId(Long email, Long recipient) {
        this.email = email;
        this.recipient = recipient;
    }

    // Getters and Setters
    public Long getEmail() {
        return email;
    }

    public void setEmail(Long email) {
        this.email = email;
    }

    public Long getRecipient() {
        return recipient;
    }

    public void setRecipient(Long recipient) {
        this.recipient = recipient;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailRecipientId that = (EmailRecipientId) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(recipient, that.recipient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, recipient);
    }
}