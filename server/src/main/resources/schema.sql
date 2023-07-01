DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
     id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
     description VARCHAR(2000) NOT NULL,
     requestor_id BIGINT NOT NULL,
     created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
     CONSTRAINT pk_request PRIMARY KEY (id),
     CONSTRAINT fk_request_user
         FOREIGN KEY (requestor_id)
             REFERENCES users (id)
                ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items (
     id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
     name VARCHAR(255) NOT NULL,
     description VARCHAR(2000) NOT NULL,
     available BOOLEAN NOT NULL,
     owner_id BIGINT NOT NULL,
     request_id BIGINT,
     CONSTRAINT pk_item PRIMARY KEY (id),
     CONSTRAINT fk_item_user
         FOREIGN KEY (owner_id)
             REFERENCES users (id)
                ON DELETE CASCADE,
     CONSTRAINT fk_item_request
         FOREIGN KEY (request_id)
             REFERENCES requests (id)
                ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    text VARCHAR(2000) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id),
    CONSTRAINT fk_item_comment
        FOREIGN KEY (item_id)
            REFERENCES items (id)
                ON DELETE CASCADE,
    CONSTRAINT fk_user_comment
        FOREIGN KEY (author_id)
            REFERENCES users (id)
                ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
     id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
     start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
     end_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
     item_id BIGINT NOT NULL,
     booker_id BIGINT NOT NULL,
     status VARCHAR(10),
     CONSTRAINT pk_booking PRIMARY KEY (id),
     CONSTRAINT fk_item_booking
         FOREIGN KEY (item_id)
             REFERENCES items (id)
                ON DELETE CASCADE,
     CONSTRAINT fk_user_booking
         FOREIGN KEY (booker_id)
             REFERENCES users (id)
                ON DELETE CASCADE
);