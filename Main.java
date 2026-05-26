import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the Library Management System.
 * Demonstrates: OOP, Collections, Exception Handling, Inheritance (Role-based menus).
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final LibraryService service = new LibraryService();
    private static User loggedInUser = null;

    // ═══════════════════════════════════════════════════════════════════════════
    // ENTRY POINT
    // ═══════════════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        printBanner();
        try {
            while (true) {
                if (loggedInUser == null) {
                    showAuthMenu();
                } else if (loggedInUser.isAdmin()) {
                    showAdminMenu();
                } else {
                    showMemberMenu();
                }
            }
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection();
            scanner.close();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // AUTH MENU
    // ═══════════════════════════════════════════════════════════════════════════

    private static void showAuthMenu() throws SQLException {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║    LIBRARY MANAGEMENT SYSTEM ║");
        System.out.println("╠══════════════════════════════╣");
        System.out.println("║  1. Login                    ║");
        System.out.println("║  2. Register as Member       ║");
        System.out.println("║  0. Exit                     ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.print("Choose: ");

        int choice = readInt();
        switch (choice) {
            case 1  -> login();
            case 2  -> registerMember();
            case 0  -> { System.out.println("Goodbye!"); System.exit(0); }
            default -> System.out.println("Invalid option.");
        }
    }

    private static void login() throws SQLException {
        System.out.print("Email    : ");
        String email = scanner.nextLine().trim();
        System.out.print("Password : ");
        String password = scanner.nextLine().trim();

        User user = service.login(email, password);
        if (user != null) {
            loggedInUser = user;
            System.out.println("\n✔ Welcome, " + user.getName() + "! [" + user.getRole() + "]");
        } else {
            System.out.println("✘ Invalid credentials. Please try again.");
        }
    }

    private static void registerMember() throws SQLException {
        System.out.print("Name     : ");
        String name = scanner.nextLine().trim();
        System.out.print("Email    : ");
        String email = scanner.nextLine().trim();
        System.out.print("Password : ");
        String password = scanner.nextLine().trim();

        User newUser = new User(name, email, password, User.Role.MEMBER);
        boolean success = service.registerUser(newUser);
        System.out.println(success ? "✔ Registered successfully! Please login." : "✘ Registration failed.");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ADMIN MENU
    // ═══════════════════════════════════════════════════════════════════════════

    private static void showAdminMenu() throws SQLException {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║       ADMIN DASHBOARD        ║");
        System.out.println("╠══════════════════════════════╣");
        System.out.println("║  --- Book Operations ---     ║");
        System.out.println("║  1. Add Book                 ║");
        System.out.println("║  2. View All Books           ║");
        System.out.println("║  3. Search Books             ║");
        System.out.println("║  4. Delete Book              ║");
        System.out.println("║  --- Issue Operations ---    ║");
        System.out.println("║  5. Issue Book               ║");
        System.out.println("║  6. Return Book              ║");
        System.out.println("║  7. View Issued Books        ║");
        System.out.println("║  --- User Operations ---     ║");
        System.out.println("║  8. View All Users           ║");
        System.out.println("║  9. Add User (Admin/Member)  ║");
        System.out.println("║  10. Delete User             ║");
        System.out.println("║  0. Logout                   ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.print("Choose: ");

        int choice = readInt();
        switch (choice) {
            case 1  -> addBook();
            case 2  -> viewAllBooks();
            case 3  -> searchBooks();
            case 4  -> deleteBook();
            case 5  -> issueBook();
            case 6  -> returnBook();
            case 7  -> service.printIssuedBooks();
            case 8  -> viewAllUsers();
            case 9  -> addUser();
            case 10 -> deleteUser();
            case 0  -> logout();
            default -> System.out.println("Invalid option.");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // MEMBER MENU
    // ═══════════════════════════════════════════════════════════════════════════

    private static void showMemberMenu() throws SQLException {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║       MEMBER DASHBOARD       ║");
        System.out.println("╠══════════════════════════════╣");
        System.out.println("║  1. View All Books           ║");
        System.out.println("║  2. Search Books             ║");
        System.out.println("║  3. Issue a Book             ║");
        System.out.println("║  4. Return a Book            ║");
        System.out.println("║  0. Logout                   ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.print("Choose: ");

        int choice = readInt();
        switch (choice) {
            case 1  -> viewAllBooks();
            case 2  -> searchBooks();
            case 3  -> issueBook();
            case 4  -> returnBook();
            case 0  -> logout();
            default -> System.out.println("Invalid option.");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BOOK OPERATIONS
    // ═══════════════════════════════════════════════════════════════════════════

    private static void addBook() throws SQLException {
        System.out.print("Title    : ");
        String title = scanner.nextLine().trim();
        System.out.print("Author   : ");
        String author = scanner.nextLine().trim();
        System.out.print("Genre    : ");
        String genre = scanner.nextLine().trim();
        System.out.print("Quantity : ");
        int qty = readInt();

        Book book = new Book(title, author, genre, qty);
        boolean success = service.addBook(book);
        System.out.println(success ? "✔ Book added successfully." : "✘ Failed to add book.");
    }

    private static void viewAllBooks() throws SQLException {
        List<Book> books = service.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books in the library.");
            return;
        }
        System.out.println("\n--- All Books ---");
        Book.printHeader();
        books.forEach(System.out::println);
    }

    private static void searchBooks() throws SQLException {
        System.out.print("Enter title or author keyword: ");
        String keyword = scanner.nextLine().trim();

        List<Book> results = service.searchBooks(keyword);
        if (results.isEmpty()) {
            System.out.println("No books found for: " + keyword);
        } else {
            System.out.println("\n--- Search Results ---");
            Book.printHeader();
            results.forEach(System.out::println);
        }
    }

    private static void deleteBook() throws SQLException {
        System.out.print("Enter Book ID to delete: ");
        int bookId = readInt();
        boolean success = service.deleteBook(bookId);
        System.out.println(success ? "✔ Book deleted." : "✘ Book not found.");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ISSUE / RETURN OPERATIONS
    // ═══════════════════════════════════════════════════════════════════════════

    private static void issueBook() throws SQLException {
        System.out.print("Enter Book ID to issue: ");
        int bookId = readInt();

        // Members issue to themselves; admins can specify any user
        int userId = loggedInUser.getUserId();
        if (loggedInUser.isAdmin()) {
            System.out.print("Enter User ID to issue to: ");
            userId = readInt();
        }

        boolean success = service.issueBook(bookId, userId);
        System.out.println(success
            ? "✔ Book issued successfully. Due in 14 days."
            : "✘ Book not available or invalid ID.");
    }

    private static void returnBook() throws SQLException {
        System.out.print("Enter Issue ID to return: ");
        int issueId = readInt();
        boolean success = service.returnBook(issueId);
        System.out.println(success ? "✔ Book returned successfully." : "✘ Invalid Issue ID or already returned.");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // USER OPERATIONS (ADMIN ONLY)
    // ═══════════════════════════════════════════════════════════════════════════

    private static void viewAllUsers() throws SQLException {
        List<User> users = service.getAllUsers();
        System.out.println("\n--- All Users ---");
        User.printHeader();
        users.forEach(System.out::println);
    }

    private static void addUser() throws SQLException {
        System.out.print("Name     : ");
        String name = scanner.nextLine().trim();
        System.out.print("Email    : ");
        String email = scanner.nextLine().trim();
        System.out.print("Password : ");
        String password = scanner.nextLine().trim();
        System.out.print("Role (ADMIN/MEMBER): ");
        String roleStr = scanner.nextLine().trim().toUpperCase();

        User.Role role;
        try {
            role = User.Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            System.out.println("✘ Invalid role. Use ADMIN or MEMBER.");
            return;
        }

        User user = new User(name, email, password, role);
        boolean success = service.registerUser(user);
        System.out.println(success ? "✔ User added." : "✘ Failed to add user.");
    }

    private static void deleteUser() throws SQLException {
        System.out.print("Enter User ID to delete: ");
        int userId = readInt();
        boolean success = service.deleteUser(userId);
        System.out.println(success ? "✔ User deleted." : "✘ User not found.");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════════════

    private static void logout() {
        System.out.println("Logged out. Goodbye, " + loggedInUser.getName() + "!");
        loggedInUser = null;
    }

    /** Reads an integer, consuming any trailing newline. Returns -1 on bad input. */
    private static int readInt() {
        try {
            int value = Integer.parseInt(scanner.nextLine().trim());
            return value;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void printBanner() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║       LIBRARY MANAGEMENT SYSTEM v1.0     ║");
        System.out.println("║    Java | JDBC | MySQL  —  Console App   ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }
}
