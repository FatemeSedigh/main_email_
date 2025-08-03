package milou;

import milou.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class Signup {
    private SessionFactory sessionFactory;

    public Signup(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public User register(String name, String email, String password) throws Exception {
        if (!email.contains("@")) {
            email += "@milou.com";
        }

        if (password.length() < 8) {
            throw new Exception("Password must be at least 8 characters long.");
        }

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            // Check if email exists
            User existingUser = session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();

            if (existingUser != null) {
                transaction.rollback();
                throw new Exception("Email already exists. Please choose another email.");
            }

            // Create new user
            User newUser = new User(name, email, password);
            session.persist(newUser);

            transaction.commit();
            return newUser;
        }
    }
}
