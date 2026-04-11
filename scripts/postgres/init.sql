-- Initialize User Service Schema
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Initialize Video Service Schema
CREATE TABLE IF NOT EXISTS videos (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    original_file_name VARCHAR(255) NOT NULL,
    raw_video_url VARCHAR(500) NOT NULL,
    status VARCHAR(50) NOT NULL, -- UPLOADED, PROCESSING, COMPLETED, FAILED
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
INSERT INTO users (username, password, email, role) 
VALUES ('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'admin@streamsphere.com', 'ADMIN')
ON CONFLICT (username) DO NOTHING;
