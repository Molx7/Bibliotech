CREATE DATABASE IF NOT EXISTS bibliotech
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE bibliotech;

CREATE TABLE IF NOT EXISTS livros (
    id BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(150) NOT NULL,
    autor VARCHAR(255) NOT NULL,
    isbn VARCHAR(255) NOT NULL,
    exemplares INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_livros_isbn UNIQUE (isbn),
    CONSTRAINT chk_livros_exemplares CHECK (exemplares >= 0)
);
