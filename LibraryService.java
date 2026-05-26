import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * LibraryService encapsulates all business logic and DB operations.
 * Demonstrates: Encapsulation, Exception Handling, JDBC, Collections.
 */
public class LibraryService {

    // ═══════════════════════════════════════════════════════════════════════════
    // AUTHENTICATION
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Validates credentials and returns the matching User, or null on failure.
     */
    public User login(String email, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapUser(rs);
            }
        }
        return null;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // USER MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════════

    public boolean registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole().name());
            return ps.executeUpdate() > 0;
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) users.add(mapUser(rs));
        }
        return users;
    }

    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BOOK MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════════

    public boolean addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, genre, quantity, available) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getGenre());
            ps.setInt(4, book.getQuantity());
            ps.setInt(5, book.getQuantity()); // available = quantity initially
            return ps.executeUpdate() > 0;
        }
    }

    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY book_id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) books.add(mapBook(rs));
        }
        return books;
    }

    /**
     * Searches by title or author (case-insensitive partial match).
     */
    public List<Book> searchBooks(String keyword) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) books.add(mapBook(rs));
        }
        return books;
    }

    public boolean deleteBook(int bookId) throws SQLException {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ISSUE & RETURN
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Issues a book to a user.
     * Uses a transaction: decrements available count + inserts issue record atomically.
     */
    public boolean issueBook(int bookId, int userId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            // 1. Check availability
            String checkSql = "SELECT available FROM books WHERE book_id = ? FOR UPDATE";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, bookId);
            ResultSet rs = checkPs.executeQuery();

            if (!rs.next() || rs.getInt("available") < 1) {
                conn.rollback();
                return false;
            }

            // 2. Decrement available count
            String updateSql = "UPDATE books SET available = available - 1 WHERE book_id = ?";
            PreparedStatement updatePs = conn.prepareStatement(updateSql);
            updatePs.setInt(1, bookId);
            updatePs.executeUpdate();

            // 3. Insert issue record (due date = 14 days from now)
            String issueSql = "INSERT INTO issued_books (book_id, user_id, issue_date, due_date) "
                            + "VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY))";
            PreparedStatement issuePs = conn.prepareStatement(issueSql);
            issuePs.setInt(1, bookId);
            issuePs.setInt(2, userId);
            issuePs.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /**
     * Returns a book. Marks the issue record as returned and increments available count.
     */
    public boolean returnBook(int issueId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            // 1. Fetch the book_id from the issue record
            String fetchSql = "SELECT book_id FROM issued_books WHERE issue_id = ? AND return_date IS NULL";
            PreparedStatement fetchPs = conn.prepareStatement(fetchSql);
            fetchPs.setInt(1, issueId);
            ResultSet rs = fetchPs.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                return false; // already returned or not found
            }
            int bookId = rs.getInt("book_id");

            // 2. Mark as returned
            String returnSql = "UPDATE issued_books SET return_date = CURDATE() WHERE issue_id = ?";
            PreparedStatement returnPs = conn.prepareStatement(returnSql);
            returnPs.setInt(1, issueId);
            returnPs.executeUpdate();

            // 3. Increment available count
            String updateSql = "UPDATE books SET available = available + 1 WHERE book_id = ?";
            PreparedStatement updatePs = conn.prepareStatement(updateSql);
            updatePs.setInt(1, bookId);
            updatePs.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /**
     * Lists all currently issued (not yet returned) records.
     */
    public void printIssuedBooks() throws SQLException {
        String sql = "SELECT ib.issue_id, b.title, u.name, ib.issue_date, ib.due_date "
                   + "FROM issued_books ib "
                   + "JOIN books b ON ib.book_id = b.book_id "
                   + "JOIN users u ON ib.user_id = u.user_id "
                   + "WHERE ib.return_date IS NULL "
                   + "ORDER BY ib.due_date";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println(String.format(
                "%-10s %-30s %-20s %-12s %-12s",
                "IssueID", "Book Title", "Issued To", "Issue Date", "Due Date"
            ));
            System.out.println("-".repeat(88));

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(String.format(
                    "%-10d %-30s %-20s %-12s %-12s",
                    rs.getInt("issue_id"),
                    rs.getString("title"),
                    rs.getString("name"),
                    rs.getDate("issue_date"),
                    rs.getDate("due_date")
                ));
            }
            if (!found) System.out.println("No books are currently issued.");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ═══════════════════════════════════════════════════════════════════════════

    private Book mapBook(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt("book_id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("genre"),
            rs.getInt("quantity"),
            rs.getInt("available")
        );
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("user_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password"),
            User.Role.valueOf(rs.getString("role"))
        );
    }
}
