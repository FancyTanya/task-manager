CREATE TYPE task_status AS ENUM ('NEW', 'IN PROGRESS', 'COMPLETED', 'CANCELED');

CREATE TABLE task
(
    id          BIGINT AUTO_INCREMENT primary key,
    title       VARCHAR(50),
    description text,
    task_status task_status,
    create_date TIMESTAMP DEFAULT NOW(),
    update_date TIMESTAMP DEFAULT NOW()
);