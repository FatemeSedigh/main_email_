package milou;

import milou.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();

        Scanner scanner = new Scanner(System.in);
        Login login = new Login(sessionFactory);
        Signup signup = new Signup(sessionFactory);

        while (true) {
            System.out.print("[L]ogin, [S]ign up: ");
            String choice = scanner.nextLine().trim().toUpperCase();

            try {
                if (choice.equals("L") || choice.equals("LOGIN")) {
                    System.out.print("Email: ");
                    String email = scanner.nextLine().trim();

                    System.out.print("Password: ");
                    String password = scanner.nextLine().trim();

                    User user = login.authenticate(email, password);
                    if (user == null) {
                        System.out.println("User not found. Please check your email and password.");
                        continue;
                    }

                    System.out.println("\nWelcome back, " + user.getName() + "!");

                    // اصلاح شده: ایجاد شیء از کلاس ShowUnreadEmails و فراخوانی متد show
                    new ShowUnreadEmails(sessionFactory).show(user);

                    // اصلاح شده: ایجاد شیء از کلاس ShowMenu و فراخوانی متد show
                    new ShowMenu(sessionFactory, user).show();

                } else if (choice.equals("S") || choice.equals("SIGNUP")) {
                    System.out.print("Name: ");
                    String name = scanner.nextLine().trim();

                    System.out.print("Email: ");
                    String email = scanner.nextLine().trim();

                    System.out.print("Password: ");
                    String password = scanner.nextLine().trim();

                    try {
                        User newUser = signup.register(name, email, password);
                        System.out.println("\nYour new account is created.");
                        System.out.println("Go ahead and login!");
                    } catch (Exception e) {
                        System.out.println("\nError: " + e.getMessage());
                    }
                } else {
                    System.out.println("Invalid choice. Please enter L for Login or S for Sign up.");
                }
            } finally {
                // هیچگاه sessionFactory را اینجا نبندید چون برنامه ادامه دارد
            }
        }
    }
}