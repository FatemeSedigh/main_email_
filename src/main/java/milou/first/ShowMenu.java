package milou.first;

import milou.menu.Forward;
import milou.menu.Reply;
import milou.menu.Send;
import milou.menu.View;
import milou.model.User;
import java.util.Scanner;
import org.hibernate.SessionFactory;

public class ShowMenu {
    private final SessionFactory sessionFactory;
    private final User currentUser;

    public ShowMenu(SessionFactory sessionFactory, User currentUser) {
        this.sessionFactory = sessionFactory;
        this.currentUser = currentUser;
    }

    public void show() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("\n[S]end, [V]iew, [R]eply, [F]orward: ");
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "S":
                    new Send(sessionFactory, currentUser).sendEmail();
                    break;
                case "V":
                    new View(sessionFactory, currentUser).showEmails();
                    break;
                case "R":
                    new Reply(sessionFactory, currentUser).replyToEmail();
                    break;
                case "F":
                    new Forward(sessionFactory, currentUser).forwardEmail();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}