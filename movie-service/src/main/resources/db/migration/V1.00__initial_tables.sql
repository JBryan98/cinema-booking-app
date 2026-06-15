CREATE TABLE movies
(
    id               UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    title            VARCHAR(255) NOT NULL,
    director         VARCHAR(150) NOT NULL,
    genre            VARCHAR(100) NOT NULL,
    duration_minutes INT          NOT NULL,
    rating           VARCHAR(100) NOT NULL,
    active           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_movie_genre CHECK (genre IN
                                      ('ACTION', 'COMEDY', 'DRAMA', 'HORROR', 'THRILLER', 'SCI_FI', 'ANIMATION',
                                       'ROMANCE', 'OTHER'))
);

CREATE TABLE screenings
(
    id              UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    movie_id        UUID        NOT NULL,
    room            VARCHAR(50) NOT NULL,
    show_at         TIMESTAMP   NOT NULL,
    total_seats     INT         NOT NULL,
    available_seats INT         NOT NULL,
    active          BOOLEAN     NOT NULL
)