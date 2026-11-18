import java.sql.*;
import java.util.Scanner;

public class HostelManagement {

    static final String URL =
            "jdbc:mysql://localhost:3306/hostel_db";

    static final String USER = "root";

    // Yahan apna MySQL password likhna hai
    static final String PASSWORD = "Sandhya@123";

    static Scanner sc = new Scanner(System.in);

    // Database Connection
    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // (a) Insert Hostel Student Records
    static void insertStudents() {

        String sql = "INSERT INTO hostelstudent VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 1; i <= 3; i++) {

                System.out.println("\nEnter Student " + i + " Details:");

                System.out.print("Student ID: ");
                int id = sc.nextInt();
                sc.nextLine();

                System.out.print("Student Name: ");
                String name = sc.nextLine();

                System.out.print("Roll Number: ");
                String roll = sc.nextLine();

                System.out.print("Hostel Name: ");
                String hostel = sc.nextLine();

                System.out.print("Room Number: ");
                String room = sc.nextLine();

                System.out.print("Fee: ");
                double fee = sc.nextDouble();

                ps.setInt(1, id);
                ps.setString(2, name);
                ps.setString(3, roll);
                ps.setString(4, hostel);
                ps.setString(5, room);
                ps.setDouble(6, fee);

                try {
                    int rows = ps.executeUpdate();

                    if (rows > 0) {
                        System.out.println("Student inserted successfully!");
                    }

                } catch (SQLException e) {

                    if (e.getErrorCode() == 1062) {
                        System.out.println("Duplicate Student ID!");
                    } else {
                        System.out.println(e.getMessage());
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // (b) Conditional Retrieval and Sorting
    static void retrieveStudents() {

        System.out.print("Enter Hostel Name: ");
        sc.nextLine();
        String hostel = sc.nextLine();

        String sql = "SELECT * FROM hostelstudent " +
                "WHERE hostel_name = ? AND fee > 50000 " +
                "ORDER BY fee DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hostel);

            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while (rs.next()) {

                found = true;

                System.out.println("\nStudent ID: " +
                        rs.getInt("student_id"));

                System.out.println("Name: " +
                        rs.getString("student_name"));

                System.out.println("Roll Number: " +
                        rs.getString("roll_number"));

                System.out.println("Hostel: " +
                        rs.getString("hostel_name"));

                System.out.println("Room: " +
                        rs.getString("room_number"));

                System.out.println("Fee: " +
                        rs.getDouble("fee"));
            }

            if (!found) {
                System.out.println("No matching students found.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // (c) Update Hostel Fee
    static void updateFee() {

        System.out.print("Enter Student ID: ");
        int id = sc.nextInt();

        System.out.print("Enter New Fee: ");
        double fee = sc.nextDouble();

        String sql = "UPDATE hostelstudent SET fee = ? WHERE student_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, fee);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Hostel Fee Updated Successfully!");
            } else {
                System.out.println("Student Record Not Found!");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // (d) Search and Modify Hostel Details
    static void updateRoom() {

        System.out.print("Enter Student ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        String search = "SELECT * FROM hostelstudent WHERE student_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(search)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                System.out.println("Student Found!");
                System.out.println("Name: " +
                        rs.getString("student_name"));

                System.out.println("Old Room: " +
                        rs.getString("room_number"));

                System.out.print("Enter New Room Number: ");
                String room = sc.nextLine();

                String update = "UPDATE hostelstudent " +
                        "SET room_number = ? WHERE student_id = ?";

                try (PreparedStatement ps2 =
                             con.prepareStatement(update)) {

                    ps2.setString(1, room);
                    ps2.setInt(2, id);

                    ps2.executeUpdate();
                }

                System.out.println("\nUpdated Student Details:");

                String display = "SELECT * FROM hostelstudent WHERE student_id = ?";

                try (PreparedStatement ps3 =
                             con.prepareStatement(display)) {

                    ps3.setInt(1, id);

                    ResultSet rs2 = ps3.executeQuery();

                    if (rs2.next()) {

                        System.out.println("ID: " +
                                rs2.getInt("student_id"));

                        System.out.println("Name: " +
                                rs2.getString("student_name"));

                        System.out.println("Hostel: " +
                                rs2.getString("hostel_name"));

                        System.out.println("Room: " +
                                rs2.getString("room_number"));

                        System.out.println("Fee: " +
                                rs2.getDouble("fee"));
                    }
                }

            } else {
                System.out.println("Student Not Found!");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // (e) Aggregate Query and Conditional Deletion
    static void deleteStudent() {

        String sumQuery = "SELECT SUM(fee) AS total FROM hostelstudent";

        try (Connection con = getConnection();
             Statement st = con.createStatement()) {

            ResultSet rs = st.executeQuery(sumQuery);

            if (rs.next()) {
                System.out.println("Total Hostel Fee Collected: " +
                        rs.getDouble("total"));
            }

            System.out.print("Enter Student ID to Delete: ");
            int id = sc.nextInt();

            String delete = "DELETE FROM hostelstudent " +
                    "WHERE student_id = ? AND fee > 0";

            try (PreparedStatement ps = con.prepareStatement(delete)) {

                ps.setInt(1, id);

                int rows = ps.executeUpdate();

                if (rows > 0) {
                    System.out.println("Student Deleted Successfully!");
                } else {
                    System.out.println(
                            "Student Not Found or Fee is not paid!"
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Main Method
    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("Database Connected Successfully!");

            while (true) {

                System.out.println("\n===== HOSTEL MANAGEMENT SYSTEM =====");

                System.out.println("1. Insert Student Records");
                System.out.println("2. Retrieve Students by Hostel");
                System.out.println("3. Update Hostel Fee");
                System.out.println("4. Search and Update Room");
                System.out.println("5. Total Fee and Delete Student");
                System.out.println("6. Exit");

                System.out.print("Enter Your Choice: ");

                int choice = sc.nextInt();

                switch (choice) {

                    case 1:
                        insertStudents();
                        break;

                    case 2:
                        retrieveStudents();
                        break;

                    case 3:
                        updateFee();
                        break;

                    case 4:
                        updateRoom();
                        break;

                    case 5:
                        deleteStudent();
                        break;

                    case 6:
                        System.out.println("Thank You!");
                        return;

                    default:
                        System.out.println("Invalid Choice!");
                }
            }

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver Not Found!");
        }
    }
}