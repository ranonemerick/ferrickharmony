CREATE TABLE emails (
    email_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    email_from VARCHAR(255) NOT NULL,
    email_to VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    text TEXT,
    send_date_email TIMESTAMP NOT NULL,
    status_email VARCHAR(50) NOT NULL
);