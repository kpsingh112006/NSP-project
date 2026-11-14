import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class insert {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name:");
        String name = sc.nextLine();
        System.out.println("Enter the email:");
        String email = sc.nextLine();
        System.out.println("Enter the password:");
        String pass = sc.nextLine();

        String sql = "INSERT INTO student (name, email, password) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, pass);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Inserted successfully!");
            } else {
                System.out.println("Insertion error!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}