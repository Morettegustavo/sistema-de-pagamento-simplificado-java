CREATE TABLE transactions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    amount DECIMAL(38,2) DEFAULT NULL,
    timestamp DATETIME(6) DEFAULT NULL,
    receiver_id BIGINT DEFAULT NULL,
    sender_id BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY FK_transactions_receiver (receiver_id),
    KEY FK_transactions_sender (sender_id),
    CONSTRAINT FK_transactions_sender FOREIGN KEY (sender_id) REFERENCES users (id),
    CONSTRAINT FK_transactions_receiver FOREIGN KEY (receiver_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
