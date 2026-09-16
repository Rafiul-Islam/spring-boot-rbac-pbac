CREATE TABLE users
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    name     VARCHAR(255)          NOT NULL,
    email    VARCHAR(255)          NOT NULL,
    password VARCHAR(255)          NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE roles
(
    id   INTEGER AUTO_INCREMENT NOT NULL,
    name VARCHAR(255)           NOT NULL,
    CONSTRAINT roles_pk PRIMARY KEY (id)
);

CREATE TABLE permissions
(
    id   INTEGER AUTO_INCREMENT NOT NULL,
    name VARCHAR(255)           NOT NULL,
    CONSTRAINT permissions_pk PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    user_id BIGINT  NOT NULL,
    role_id INTEGER NOT NULL,

    CONSTRAINT user_roles_pk PRIMARY KEY (user_id, role_id),
    CONSTRAINT user_roles_users_id_fk
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,
    CONSTRAINT user_roles_roles_id_fk
        FOREIGN KEY (role_id)
            REFERENCES roles (id)
            ON DELETE CASCADE
);

CREATE TABLE roles_permissions
(
    role_id          INTEGER NOT NULL,
    permission_id    INTEGER NOT NULL,
    isDefaultForRole BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT roles_permissions_pk PRIMARY KEY (role_id, permission_id),
    CONSTRAINT roles_permissions_roles_id_fk
        FOREIGN KEY (role_id)
            REFERENCES roles (id)
            ON DELETE CASCADE,
    CONSTRAINT roles_permissions_permissions_id_fk
        FOREIGN KEY (permission_id)
            REFERENCES permissions (id)
            ON DELETE CASCADE
);

CREATE TABLE user_permissions
(
    user_id       BIGINT  NOT NULL,
    permission_id INTEGER NOT NULL,

    CONSTRAINT user_permissions_pk PRIMARY KEY (user_id, permission_id),
    CONSTRAINT user_permissions_users_id_fk
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,
    CONSTRAINT user_permissions_permissions_id_fk
        FOREIGN KEY (permission_id)
            REFERENCES permissions (id)
            ON DELETE CASCADE
);

CREATE TABLE addresses
(
    id      BIGINT AUTO_INCREMENT NOT NULL,
    street  VARCHAR(255)          NOT NULL,
    city    VARCHAR(255)          NOT NULL,
    state   VARCHAR(255)          NOT NULL,
    zip     VARCHAR(255)          NOT NULL,
    user_id BIGINT                NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT addresses_users_id_fk
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    INDEX addresses_users_id_fk (user_id)
);

CREATE TABLE products
(
    id          BIGINT AUTO_INCREMENT  NOT NULL,
    name        VARCHAR(255)           NOT NULL,
    description LONGTEXT               NULL,
    price       DECIMAL(10, 2)         NOT NULL,
    quantity    INT UNSIGNED DEFAULT 0 NOT NULL,

    PRIMARY KEY (id)
);