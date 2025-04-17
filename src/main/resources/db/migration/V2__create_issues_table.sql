CREATE TABLE issues (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    priority VARCHAR(10) NOT NULL,
    created_by INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE tags (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE issue_tags (
   issue_id INT NOT NULL,
   tag_id INT NOT NULL,

   PRIMARY KEY (issue_id, tag_id),
   FOREIGN KEY (issue_id) REFERENCES issues(id),
   FOREIGN KEY (tag_id) REFERENCES tags(id)
);

INSERT INTO tags (name, description)
VALUES
    ('Bug', 'Something is broken or not working as expected'),
    ('Enhancement', 'Improvement to an existing feature'),
    ('Question', 'Needs clarification, discussion, or help'),
    ('Feature', 'Request or implementation of a new feature');