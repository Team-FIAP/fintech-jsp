CREATE TABLE T_FIN_INCOME (
    id NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    amount NUMBER(10, 2) NOT NULL,
    "DATE" DATE NOT NULL,
    description VARCHAR(255) NOT NULL,
    observation CLOB,
    origin_account_id NUMBER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  NOT NULL,

    CONSTRAINT FK_INCOME_ACCOUNT FOREIGN KEY (origin_account_id) REFERENCES T_FIN_ACCOUNT(id)
)