INSERT INTO usuarios (nombre, email, enabled, password, sector_id) VALUES ("Admin", "admin@gmail.com", true, "$2a$10$n4jsMHzqMqVhWPg9etKHG.2vfqr8RcVW3uS/2ztVTz6Bu4rhhKwnS", null);

INSERT INTO roles (role) VALUES ("ADMIN");
INSERT INTO roles (role) VALUES ("USER");

INSERT INTO user_role(user_id, role_id) VALUES (1, 1);



