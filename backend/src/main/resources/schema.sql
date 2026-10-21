-- ===================================================================
-- SafeCircle MySQL Database Schema
-- Consent-Based Family & Friends Safety Tracker
-- ===================================================================

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(30) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    account_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS circles (
    circle_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_circles_created_by FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS circle_members (
    circle_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (circle_id, user_id),
    CONSTRAINT fk_circle_members_circle FOREIGN KEY (circle_id) REFERENCES circles(circle_id) ON DELETE CASCADE,
    CONSTRAINT fk_circle_members_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sharing_permissions (
    permission_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    viewer_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    approved_at TIMESTAMP NULL,
    revoked_at TIMESTAMP NULL,
    expires_at TIMESTAMP NULL,
    CONSTRAINT fk_permissions_owner FOREIGN KEY (owner_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_permissions_viewer FOREIGN KEY (viewer_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT uq_owner_viewer UNIQUE (owner_id, viewer_id)
);

CREATE TABLE IF NOT EXISTS location_history (
    location_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    accuracy_m DOUBLE NOT NULL,
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_locations_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Index for fast user timeline location queries
CREATE INDEX IF NOT EXISTS idx_location_user_time ON location_history (user_id, recorded_at DESC);

CREATE TABLE IF NOT EXISTS safety_checkins (
    checkin_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    destination_label VARCHAR(255) NOT NULL,
    due_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    confirmed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_checkins_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS safety_alerts (
    alert_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    alert_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL DEFAULT 'INFO',
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    details TEXT NULL,
    CONSTRAINT fk_alerts_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
