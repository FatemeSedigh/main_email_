package milou.model;

import java.io.Serializable;
import java.util.Objects;

public class EmailRecipientId implements Serializable {
    private Integer email;  // تغییر از Long به Integer
    private Integer recipient;  // تغییر از Long به Integer

    // Constructors
    public EmailRecipientId() {
    }

    public EmailRecipientId(Integer email, Integer recipient) {
        this.email = email;
        this.recipient = recipient;
    }

    // Getters and Setters
    public Integer getEmail() {
        return email;
    }

    public void setEmail(Integer email) {
        this.email = email;
    }

    public Integer getRecipient() {
        return recipient;
    }

    public void setRecipient(Integer recipient) {
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