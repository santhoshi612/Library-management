public class Book {

    private int    bookId;
    private String title;
    private String author;
    private String genre;
    private int    quantity;
    private int    available;

    // ── Constructors ──────────────────────────────────────────────────────────

    public Book() {}

    /** Used when adding a new book (DB generates the ID). */
    public Book(String title, String author, String genre, int quantity) {
        this.title     = title;
        this.author    = author;
        this.genre     = genre;
        this.quantity  = quantity;
        this.available = quantity;
    }

    /** Used when reading a book row from the database. */
    public Book(int bookId, String title, String author, String genre,
                int quantity, int available) {
        this.bookId    = bookId;
        this.title     = title;
        this.author    = author;
        this.genre     = genre;
        this.quantity  = quantity;
        this.available = available;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public int    getBookId()    { return bookId;    }
    public String getTitle()     { return title;     }
    public String getAuthor()    { return author;    }
    public String getGenre()     { return genre;     }
    public int    getQuantity()  { return quantity;  }
    public int    getAvailable() { return available; }

    public void setBookId(int bookId)       { this.bookId    = bookId;    }
    public void setTitle(String title)      { this.title     = title;     }
    public void setAuthor(String author)    { this.author    = author;    }
    public void setGenre(String genre)      { this.genre     = genre;     }
    public void setQuantity(int quantity)   { this.quantity  = quantity;  }
    public void setAvailable(int available) { this.available = available; }

    // ── Display ───────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format(
            "%-6d %-30s %-20s %-15s %-10d %-10d",
            bookId, title, author, genre, quantity, available
        );
    }

    public static void printHeader() {
        System.out.println(String.format(
            "%-6s %-30s %-20s %-15s %-10s %-10s",
            "ID", "Title", "Author", "Genre", "Qty", "Available"
        ));
        System.out.println("-".repeat(96));
    }
}
