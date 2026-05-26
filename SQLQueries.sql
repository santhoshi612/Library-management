-- ============================================================
--  Library Management System — Database Schema & Seed Data
--  MySQL 8.x
-- ============================================================

CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- ── USERS ────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS users (
    user_id  INT          AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    email    VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     ENUM('ADMIN','MEMBER') NOT NULL DEFAULT 'MEMBER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ── BOOKS ────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS books (
    book_id   INT          AUTO_INCREMENT PRIMARY KEY,
    title     VARCHAR(200) NOT NULL,
    author    VARCHAR(150) NOT NULL,
    genre     VARCHAR(80),
    quantity  INT          NOT NULL DEFAULT 1,
    available INT          NOT NULL DEFAULT 1,
    added_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CHECK (available >= 0),
    CHECK (available <= quantity)
);

-- ── ISSUED BOOKS ─────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS issued_books (
    issue_id    INT  AUTO_INCREMENT PRIMARY KEY,
    book_id     INT  NOT NULL,
    user_id     INT  NOT NULL,
    issue_date  DATE NOT NULL,
    due_date    DATE NOT NULL,
    return_date DATE,                          -- NULL means not yet returned
    CONSTRAINT fk_issue_book FOREIGN KEY (book_id)
        REFERENCES books(book_id) ON DELETE CASCADE,
    CONSTRAINT fk_issue_user FOREIGN KEY (user_id)
        REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================================
--  SEED DATA
-- ============================================================

-- Default admin  (password: admin123)
INSERT INTO users (name, email, password, role) VALUES
    ('Admin User',    'admin@library.com',  'admin123',  'ADMIN'),
    ('Alice Johnson', 'alice@example.com',  'alice123',  'MEMBER'),
    ('Bob Smith',     'bob@example.com',    'bob123',    'MEMBER');

-- Sample books
INSERT INTO books (title, author, genre, quantity, available) VALUES
    ('The Pragmatic Programmer',        'Andrew Hunt',         'Technology',    3, 3),
    ('Clean Code',                      'Robert C. Martin',    'Technology',    2, 2),
    ('Design Patterns',                 'Gang of Four',        'Technology',    2, 2),
    ('To Kill a Mockingbird',           'Harper Lee',          'Fiction',       4, 4),
    ('1984',                            'George Orwell',       'Dystopian',     3, 3),
    ('The Alchemist',                   'Paulo Coelho',        'Philosophy',    5, 5),
    ('Introduction to Algorithms',      'Cormen et al.',       'Computer Sci',  2, 2),
    ('Sapiens',                         'Yuval Noah Harari',   'History',       3, 3),
    ('Atomic Habits',                   'James Clear',         'Self-Help',     4, 4),
    ('The Great Gatsby',                'F. Scott Fitzgerald', 'Classic',       3, 3);

-- ============================================================
--  USEFUL QUERIES (for reference / testing)
-- ============================================================

-- View all books with availability
-- SELECT * FROM books ORDER BY title;

-- View all currently issued books with names
-- SELECT ib.issue_id, b.title, u.name, ib.issue_date, ib.due_date
-- FROM issued_books ib
-- JOIN books b ON ib.book_id = b.book_id
-- JOIN users u ON ib.user_id = u.user_id
-- WHERE ib.return_date IS NULL;

-- Overdue books
-- SELECT ib.issue_id, b.title, u.name, ib.due_date,
--        DATEDIFF(CURDATE(), ib.due_date) AS days_overdue
-- FROM issued_books ib
-- JOIN books b ON ib.book_id = b.book_id
-- JOIN users u ON ib.user_id = u.user_id
-- WHERE ib.return_date IS NULL AND ib.due_date < CURDATE()
-- ORDER BY days_overdue DESC;
