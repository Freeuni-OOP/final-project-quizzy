CREATE DATABASE IF NOT EXISTS quizzy;
USE quizzy;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE
);

CREATE TABLE quizzes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    creator_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    random_questions BOOLEAN DEFAULT FALSE,
    one_page BOOLEAN DEFAULT TRUE,
    immediate_correction BOOLEAN DEFAULT FALSE,
    practice_mode BOOLEAN DEFAULT FALSE,

    FOREIGN KEY (creator_id) REFERENCES users(id)
);

CREATE TABLE questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id INT NOT NULL,
    question_type VARCHAR(30) NOT NULL,
    prompt TEXT NOT NULL,
    image_url TEXT,
    question_order INT DEFAULT 0,

    FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);

CREATE TABLE answers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    answer_text TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT TRUE,

    FOREIGN KEY (question_id) REFERENCES questions(id)
);

CREATE TABLE quiz_attempts (
           id INT AUTO_INCREMENT PRIMARY KEY,
           user_id INT NOT NULL,
           quiz_id INT NOT NULL,
           score INT NOT NULL,
           max_score INT NOT NULL,
           time_taken_seconds BIGINT NOT NULL,
           completed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

           FOREIGN KEY (user_id) REFERENCES users(id),
           FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);
CREATE TABLE friendships (
    id INT AUTO_INCREMENT PRIMARY KEY,
    requester_id INT NOT NULL,
    receiver_id INT NOT NULL,
    status VARCHAR(20) NOT NULL,

    FOREIGN KEY (requester_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id)
);

CREATE TABLE messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    message_type VARCHAR(30) NOT NULL,
    quiz_id INT,
    body TEXT,

    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id),
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id)
);

-- Achievement names are stored as enum constants (VARCHAR).
-- The original 'achievements' lookup table has been removed.
-- Deduplication is enforced by the UNIQUE constraint.

CREATE TABLE user_achievements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    achievement_name VARCHAR(50) NOT NULL,
    earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY unique_user_achievement (user_id, achievement_name)
);

CREATE TABLE announcements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    creator_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (creator_id) REFERENCES users(id)
);

CREATE TABLE reported_quizzes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id INT NOT NULL,
    reporter_id INT NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    reviewed_by INT NULL,

    FOREIGN KEY (quiz_id) REFERENCES quizzes(id),
    FOREIGN KEY (reporter_id) REFERENCES users(id),
    FOREIGN KEY (reviewed_by) REFERENCES users(id)
);
