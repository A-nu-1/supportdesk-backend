-- =========================================================
-- SupportDesk demo data
-- =========================================================

-- ---------------------------------------------------------
-- Demo users
-- ---------------------------------------------------------

INSERT INTO users (
    email,
    password_hash,
    first_name,
    last_name,
    role,
    active
)
VALUES
(
    'anu@example.com',
    '$2a$10$zBKoi59BjEArzV/k4FFaeO/vLaizKMOell7LTfEtQx0KDLz.2OC0i',
    'Anu',
    'Admin',
    'ADMIN',
    TRUE
),
(
    'agent@example.com',
    '$2a$10$mDi9Ic3B5TY2lM9V9jBs3O7t2HT9orU3pgreGkJOo.emzlGLwTd7q',
    'Sam',
    'Support',
    'SUPPORT_AGENT',
    TRUE
),
(
    'employee@example.com',
    '$2a$10$L9mPeaKCx4p7hug6vs2goedic3.CUoiWJBZ9l0rLef6POFD.2yUXG',
    'Priya',
    'Employee',
    'EMPLOYEE',
    TRUE
);


-- ---------------------------------------------------------
-- Ticket categories
-- ---------------------------------------------------------

INSERT INTO categories (name, active)
VALUES
    ('Laptop', TRUE),
    ('Software', TRUE),
    ('Network', TRUE),
    ('Access', TRUE),
    ('Other', TRUE),
    ('Printer', FALSE);


-- ---------------------------------------------------------
-- Demo tickets
-- ---------------------------------------------------------

-- Priya has an open laptop ticket waiting to be claimed.
INSERT INTO tickets (
    title,
    description,
    category_id,
    created_by,
    assigned_to,
    priority,
    status
)
VALUES (
    'Laptop will not start',
    'My laptop does not power on even when connected to the charger.',
    (SELECT id FROM categories WHERE name = 'Laptop'),
    (SELECT id FROM users WHERE email = 'employee@example.com'),
    NULL,
    'HIGH',
    'OPEN'
);


-- Priya has a software ticket currently handled by Sam.
INSERT INTO tickets (
    title,
    description,
    category_id,
    created_by,
    assigned_to,
    priority,
    status
)
VALUES (
    'Outlook crashes after startup',
    'Outlook closes a few seconds after opening. Restarting the application has not resolved the issue.',
    (SELECT id FROM categories WHERE name = 'Software'),
    (SELECT id FROM users WHERE email = 'employee@example.com'),
    (SELECT id FROM users WHERE email = 'agent@example.com'),
    'MEDIUM',
    'IN_PROGRESS'
);


-- Priya has a resolved network ticket handled by Sam.
INSERT INTO tickets (
    title,
    description,
    category_id,
    created_by,
    assigned_to,
    priority,
    status
)
VALUES (
    'Unable to connect to office Wi-Fi',
    'The laptop could not connect to the office wireless network after a password update.',
    (SELECT id FROM categories WHERE name = 'Network'),
    (SELECT id FROM users WHERE email = 'employee@example.com'),
    (SELECT id FROM users WHERE email = 'agent@example.com'),
    'HIGH',
    'RESOLVED'
);


-- Another ticket demonstrates the waiting-for-user workflow.
INSERT INTO tickets (
    title,
    description,
    category_id,
    created_by,
    assigned_to,
    priority,
    status
)
VALUES (
    'VPN access required',
    'VPN access is required to work remotely with internal applications.',
    (SELECT id FROM categories WHERE name = 'Access'),
    (SELECT id FROM users WHERE email = 'employee@example.com'),
    (SELECT id FROM users WHERE email = 'agent@example.com'),
    'MEDIUM',
    'WAITING_FOR_USER'
);


-- ---------------------------------------------------------
-- Demo comments
-- ---------------------------------------------------------

-- Comments on the Outlook ticket.
INSERT INTO comments (
    ticket_id,
    user_id,
    message
)
VALUES (
    (SELECT id FROM tickets WHERE title = 'Outlook crashes after startup'),
    (SELECT id FROM users WHERE email = 'agent@example.com'),
    'I am checking the Outlook installation and application logs.'
);

INSERT INTO comments (
    ticket_id,
    user_id,
    message
)
VALUES (
    (SELECT id FROM tickets WHERE title = 'Outlook crashes after startup'),
    (SELECT id FROM users WHERE email = 'employee@example.com'),
    'Thank you. The issue still occurs after restarting the laptop.'
);


-- Comments on the resolved network ticket.
INSERT INTO comments (
    ticket_id,
    user_id,
    message
)
VALUES (
    (SELECT id FROM tickets WHERE title = 'Unable to connect to office Wi-Fi'),
    (SELECT id FROM users WHERE email = 'agent@example.com'),
    'The saved Wi-Fi profile was outdated. I removed it and configured the connection again.'
);

INSERT INTO comments (
    ticket_id,
    user_id,
    message
)
VALUES (
    (SELECT id FROM tickets WHERE title = 'Unable to connect to office Wi-Fi'),
    (SELECT id FROM users WHERE email = 'employee@example.com'),
    'The connection is working now. Thank you.'
);


-- Comment on the waiting-for-user ticket.
INSERT INTO comments (
    ticket_id,
    user_id,
    message
)
VALUES (
    (SELECT id FROM tickets WHERE title = 'VPN access required'),
    (SELECT id FROM users WHERE email = 'agent@example.com'),
    'Please confirm whether VPN access is required for Windows only or for a mobile device as well.'
);