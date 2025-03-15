INSERT INTO post (id, writer_id, status, contents, created_at, updated_at)
VALUES
    (1, 3,'NORMAL', 'visible post', sysdate(), sysdate()),
    (2, 3,'NORMAL', 'visible post', sysdate(), sysdate()),
    (3, 3,'NORMAL', 'visible post', sysdate(), sysdate()),
    (4, 3,'DELETED', 'deleted post', sysdate(), sysdate()),
    (5, 4,'NORMAL', 'non visible post', sysdate(), sysdate()),
    (6, 4,'NORMAL', 'non visible post', sysdate(), sysdate());
