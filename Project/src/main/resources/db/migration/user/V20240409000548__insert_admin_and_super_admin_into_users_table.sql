INSERT INTO users (username, email, password)
VALUES ('admin', 'admin@gmail.com', '$2a$10$f76DHgFNHRipupQQv/g62Ohz6EaHbqk//5w1bLo41jq2/Be3VqfmG');

INSERT INTO users_roles (user_id, role_id)
SELECT id, 2
FROM users
WHERE username = 'admin';

INSERT INTO users (username, email, password)
VALUES ('superadmin', 'superadmin@gmail.com', '$2a$10$H5x1JLlmVRQaf7XxZR2NBuhNrACkDVsQmz79UYN6UUbX4lfU4.Mtu');

INSERT INTO users_roles (user_id, role_id)
SELECT id, 3
FROM users
WHERE username = 'superadmin';
