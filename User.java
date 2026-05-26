public class User {

    public enum Role { ADMIN, MEMBER }

    private int    userId;
    private String name;
    private String email;
    private String password;   // stored as plain text for simplicity; hash in production
    private Role   role;

    // ── Constructors ──────────────────────────────────────────────────────────

    public User() {}

    /** Used when registering a new member (DB generates the ID). */
    public User(String name, String email, String password, Role role) {
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    /** Used when reading a user row from the database. */
    public User(int userId, String name, String email, String password, Role role) {
        this.userId   = userId;
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public int    getUserId()  { return userId;   }
    public String getName()    { return name;     }
    public String getEmail()   { return email;    }
    public String getPassword(){ return password; }
    public Role   getRole()    { return role;     }

    public void setUserId(int userId)      { this.userId   = userId;   }
    public void setName(String name)       { this.name     = name;     }
    public void setEmail(String email)     { this.email    = email;    }
    public void setPassword(String pw)     { this.password = pw;       }
    public void setRole(Role role)         { this.role     = role;     }

    public boolean isAdmin() { return role == Role.ADMIN; }

    // ── Display ───────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format(
            "%-6d %-20s %-30s %-10s",
            userId, name, email, role
        );
    }

    public static void printHeader() {
        System.out.println(String.format(
            "%-6s %-20s %-30s %-10s",
            "ID", "Name", "Email", "Role"
        ));
        System.out.println("-".repeat(70));
    }
}
