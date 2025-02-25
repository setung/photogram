INSERT INTO user (id, name, email, password, status, created_at, updated_at)
VALUES
    (1, 'name', 'tester_1@test.com', '$2a$10$6ipZUZWDYEVXa0jwgF/L2efyVMnzYs8r/s4eSoQ.gI9qFSICRDn5O', 'NORMAL', sysdate(), sysdate()),
    (2, 'name', 'update_tester@test.com', '$2a$10$6ipZUZWDYEVXa0jwgF/L2efyVMnzYs8r/s4eSoQ.gI9qFSICRDn5O', 'NORMAL', sysdate(), sysdate()),
    (3, 'name', 'delete_tester1@test.com', '$2a$10$6ipZUZWDYEVXa0jwgF/L2efyVMnzYs8r/s4eSoQ.gI9qFSICRDn5O', 'NORMAL', sysdate(), sysdate()),
    (4, 'name', 'delete_tester2@test.com', '$2a$10$6ipZUZWDYEVXa0jwgF/L2efyVMnzYs8r/s4eSoQ.gI9qFSICRDn5O', 'DELETED', sysdate(), sysdate());
