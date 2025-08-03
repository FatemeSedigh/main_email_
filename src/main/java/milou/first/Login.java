package milou.first;

import milou.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public class Login {
    private SessionFactory sessionFactory;

    public Login(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public User authenticate(String email, String password) {
        // اضافه کردن @milou.com اگر وجود نداشت
        if (!email.contains("@")) {
            email += "@milou.com";
        }

        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE email = :email AND password = :password", User.class);
            query.setParameter("email", email);
            query.setParameter("password", password);

            return query.uniqueResult();
        }
    }
}


