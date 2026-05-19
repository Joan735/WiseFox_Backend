-- ============================================================
--  WiseFox — Stress Testing Script (Dynamic IDs)
--  Works on top of existing data — no truncate needed.
--  Generates: 500 users · 1000 ledgers · 1000 user_ledger
--             · 50.000 transactions
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;
SET UNIQUE_CHECKS      = 0;
SET autocommit         = 0;

DROP PROCEDURE IF EXISTS wisefox_stress_test;

DELIMITER $$

CREATE PROCEDURE wisefox_stress_test()
BEGIN

  -- ── Loop counters ─────────────────────────────────────────
  DECLARE i   INT    DEFAULT 1;
  DECLARE j   INT    DEFAULT 1;

  -- ── Dynamic IDs ───────────────────────────────────────────
  DECLARE uid  BIGINT DEFAULT 0;
  DECLARE lid1 BIGINT DEFAULT 0;
  DECLARE lid2 BIGINT DEFAULT 0;

  -- ══════════════════════════════════════════════════════════
  --  MAIN LOOP: 500 users
  -- ══════════════════════════════════════════════════════════
  WHILE i <= 500 DO

    -- ── 1. Insert user ────────────────────────────────────
    INSERT INTO `user` (name, surname, username, email, password, role)
    VALUES (
      CONCAT('Name',    i),
      CONCAT('Surname', i),
      CONCAT('stress_user', i),
      CONCAT('stress', i, '@wisefox.test'),
      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
      'USER'
    );

    -- Get the real ID MySQL assigned to this user
    SET uid = LAST_INSERT_ID();

    -- ── 2. Insert ledgers one by one to capture each ID ───
    INSERT INTO ledger (name, currency, description, user_id)
    VALUES (CONCAT('Personal-', i), 'EUR', CONCAT('Personal ledger for user ', i), uid);

    SET lid1 = LAST_INSERT_ID();

    INSERT INTO ledger (name, currency, description, user_id)
    VALUES (CONCAT('Savings-', i), 'EUR', CONCAT('Savings ledger for user ', i), uid);

    SET lid2 = LAST_INSERT_ID();

    -- ── 3. Register user as OWNER in user_ledger ──────────
    INSERT INTO user_ledger (user_id, ledger_id, permission)
    VALUES (uid, lid1, 'OWNER');

    INSERT INTO user_ledger (user_id, ledger_id, permission)
    VALUES (uid, lid2, 'OWNER');

    -- ── 4. Insert 50 transactions split between both ledgers
    SET j = 1;
    WHILE j <= 50 DO

      INSERT INTO `transaction` (amount, type, category, date, note, ledger_id)
      VALUES (
        ROUND(10 + (RAND() * 3000), 2),

        IF(j MOD 5 = 0, 'INCOME', 'EXPENSE'),

        CASE (j MOD 8)
          WHEN 0 THEN 'FOOD'
          WHEN 1 THEN 'TRANSPORT'
          WHEN 2 THEN 'RENT'
          WHEN 3 THEN 'ENTERTAINMENT'
          WHEN 4 THEN 'HEALTH'
          WHEN 5 THEN 'SHOPPING'
          WHEN 6 THEN 'SALARY'
          ELSE        'OTHER'
        END,

        DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 365) DAY),

        CONCAT('Stress transaction #', j, ' user ', i),

        IF(j MOD 2 = 0, lid1, lid2)
      );

      SET j = j + 1;
    END WHILE;

    SET i = i + 1;
  END WHILE;

END$$

DELIMITER ;

-- ============================================================
--  RUN
-- ============================================================
CALL wisefox_stress_test();
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
SET UNIQUE_CHECKS      = 1;
SET autocommit         = 1;

-- ============================================================
--  VERIFY
-- ============================================================
SELECT 'users'        AS table_name, COUNT(*) AS total FROM `user`        WHERE username LIKE 'stress_%'
UNION ALL
SELECT 'ledgers',                    COUNT(*)           FROM ledger         WHERE name LIKE 'Personal-%' OR name LIKE 'Savings-%'
UNION ALL
SELECT 'user_ledger',                COUNT(*)           FROM user_ledger    WHERE permission = 'OWNER'
UNION ALL
SELECT 'transactions',               COUNT(*)           FROM `transaction`  WHERE note LIKE 'Stress transaction%';

-- ============================================================
--  CLEANUP (uncomment when you want to remove stress data)
-- ============================================================
/*
SET FOREIGN_KEY_CHECKS = 0;

DELETE t FROM `transaction` t
  JOIN ledger l ON t.ledger_id = l.id
  JOIN `user` u ON l.user_id  = u.id
WHERE u.username LIKE 'stress_%';

DELETE ul FROM user_ledger ul
  JOIN `user` u ON ul.user_id = u.id
WHERE u.username LIKE 'stress_%';

DELETE FROM ledger WHERE name LIKE 'Personal-%' OR name LIKE 'Savings-%';
DELETE FROM `user` WHERE username LIKE 'stress_%';

SET FOREIGN_KEY_CHECKS = 1;
*/
