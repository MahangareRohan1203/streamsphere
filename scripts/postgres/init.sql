-- Initialize User Service Schema
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,
    current_tier VARCHAR(20) DEFAULT 'FREE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    tier VARCHAR(20) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Initialize Video Service Schema
CREATE TABLE IF NOT EXISTS videos (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    original_file_name VARCHAR(255) NOT NULL,
    raw_video_url VARCHAR(500) NOT NULL,
    status VARCHAR(50) NOT NULL, -- UPLOADED, PROCESSING, COMPLETED, FAILED
    minimum_subscription_tier VARCHAR(20) DEFAULT 'FREE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS video_resolutions (
    id BIGSERIAL PRIMARY KEY,
    video_id BIGINT NOT NULL,
    resolution VARCHAR(20) NOT NULL, -- 1080p, 720p, 480p
    video_url VARCHAR(500) NOT NULL,
    CONSTRAINT fk_video FOREIGN KEY (video_id) REFERENCES videos(id) ON DELETE CASCADE
);

-- Optional: Seed an Admin User (password: password)
INSERT INTO users (username, password, email, role, current_tier) 
VALUES ('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'admin@streamsphere.com', 'ADMIN', 'GOLD')
ON CONFLICT (username) DO NOTHING;
