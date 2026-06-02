CREATE TABLE roles (
                       role_id BIGSERIAL PRIMARY KEY,
                       role_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role_id BIGINT,

                       CONSTRAINT fk_role
                           FOREIGN KEY(role_id)
                               REFERENCES roles(role_id)
);