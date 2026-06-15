CREATE TABLE bookings
(
    id           UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    screening_id UUID           NOT NULL,
    customer_id  UUID           NOT NULL,
    movie_title  VARCHAR(255)   NOT NULL,
    status       VARCHAR(10)    NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_total_amount_positive CHECK (total_amount >= 0),
    CONSTRAINT chk_bookings_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED'))
);