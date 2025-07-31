package milou.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "emails")
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(name = "code", nullable = false, length = 6)
    private String code;

    @Column(name = "subject", nullable = false, length = 250)
    private String subject;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Column(name = "sent_date", nullable = false)
    private LocalDate sentDate;

    @Column(name = "sent_time", nullable = false)
    private LocalTime sentTime;

    @OneToMany(mappedBy = "email", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmailRecipient> recipients;

    // Constructors
    public Email() {
    }

    public Email(User sender, String code, String subject, String body) {
        this.sender = sender;
        this.code = code;
        this.subject = subject;
        this.body = body;
        this.sentDate = LocalDate.now();
        this.sentTime = LocalTime.now();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDate getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDate sentDate) {
        this.sentDate = sentDate;
    }

    public LocalTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(LocalTime sentTime) {
        this.sentTime = sentTime;
    }

    public List<EmailRecipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<EmailRecipient> recipients) {
        this.recipients = recipients;
    }

    @Override
    public String toString() {
        return "Email{" +
                "id=" + id +
                ", sender=" + sender.getEmail() +
                ", code='" + code + '\'' +
                ", subject='" + subject + '\'' +
                ", sentDate=" + sentDate +
                ", sentTime=" + sentTime +
                '}';
    }
}