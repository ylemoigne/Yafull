CREATE TABLE "user"
(
    id              UUID              NOT NULL DEFAULT gen_random_uuid(),
    login           CHARACTER VARYING NOT NULL,
    fullname        CHARACTER VARYING NOT NULL,
    roles           CHARACTER VARYING[] NOT NULL,
    hashed_password CHARACTER VARYING NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (login)
);

CREATE TABLE note
(
    id         UUID      NOT NULL DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    author_id  UUID      NOT NULL,
    title      CHARACTER VARYING,
    content    CHARACTER VARYING,
    PRIMARY KEY (id),
    FOREIGN KEY (author_id) REFERENCES "user" (id) ON DELETE CASCADE
);
