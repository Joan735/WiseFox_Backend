-- ============================================================
--  WiseFox – Dummy Data
--  Flyway migration: V1__dummy_data.sql
--
--  10 Users | 22 Ledgers | 33 UserLedgers | 100 Transactions
--  All currencies with EUR
--  Dates: march-april 2025
--
--  Everyone password: "password123"
--  BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh32
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- USERS
-- ============================================================
INSERT INTO `user` (id, name, surname, username, email, password, role, pfp) VALUES
(1,  'Alice',  'Martin',   'alice_m',  'alice@wisefox.com',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh32', 'USER',    NULL),
(2,  'Bob',    'Johnson',  'bob_j',    'bob@wisefox.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh32', 'USER',    NULL),
(3,  'Carlos', 'Garcia',   'carlos_g', 'carlos@wisefox.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh32', 'PREMIUM', NULL),
(4,  'Diana',  'Smith',    'diana_s',  'diana@wisefox.com',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh32', 'USER',    NULL),
(5,  'Ethan',  'Brown',    'ethan_b',  'ethan@wisefox.com',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh32', 'USER',    NULL),
(6,  'Fiona',  'Wilson',   'fiona_w',  'fiona@wisefox.com',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh32', 'PREMIUM', NULL),
(7,  'George', 'Taylor',   'george_t', 'george@wisefox.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh32', 'USER',    NULL),
(8,  'Hannah', 'Anderson', 'hannah_a', 'hannah@wisefox.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh32', 'USER',    NULL),
(9,  'Ivan',   'Thomas',   'ivan_t',   'ivan@wisefox.com',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh32', 'USER',    NULL),
(10, 'Julia',  'White',    'julia_w',  'julia@wisefox.com',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh32', 'PREMIUM', NULL);

-- ============================================================
-- LEDGERS  (todos EUR)
-- Alice (1):  1,2 solo  | 3  shared owner
-- Bob (2):    4   solo  | 5  shared owner
-- Carlos (3): 6,7 solo  | 8  shared owner
-- Diana (4):  9   solo  | 10 shared owner
-- Ethan (5):  11,12 solo | member en 10
-- Fiona (6):  13  solo  | 14 shared owner
-- George (7): 15,16 solo | 17 shared owner
-- Hannah (8): 18  solo  | member en 14, 17
-- Ivan (9):   19,20 solo | 21 shared owner
-- Julia (10): 22  solo  | member en 17, 21
-- ============================================================
INSERT INTO ledger (id, name, currency, description, user_id) VALUES
( 1, 'Alice Personal',         'EUR', 'Gastos personales de Alice',       1),
( 2, 'Alice Savings',          'EUR', 'Ahorro mensual de Alice',          1),
( 3, 'Alice & Bob Household',  'EUR', 'Gastos del hogar compartido',      1),
( 4, 'Bob Personal',           'EUR', 'Gastos personales de Bob',         2),
( 5, 'Bob & Carlos Trip',      'EUR', 'Viaje de verano compartido',       2),
( 6, 'Carlos Personal',        'EUR', 'Gastos personales de Carlos',      3),
( 7, 'Carlos Business',        'EUR', 'Gastos de negocio de Carlos',      3),
( 8, 'Carlos & Diana Family',  'EUR', 'Gastos familiares compartidos',    3),
( 9, 'Diana Personal',         'EUR', 'Gastos personales de Diana',       4),
(10, 'Diana & Ethan Gym',      'EUR', 'Cuota y gastos del gimnasio',      4),
(11, 'Ethan Personal',         'EUR', 'Gastos personales de Ethan',       5),
(12, 'Ethan Side Projects',    'EUR', 'Ingresos y gastos de proyectos',   5),
(13, 'Fiona Personal',         'EUR', 'Gastos personales de Fiona',       6),
(14, 'Fiona & George Rent',    'EUR', 'Alquiler y suministros del piso',  6),
(15, 'George Personal',        'EUR', 'Gastos personales de George',      7),
(16, 'George Investments',     'EUR', 'Seguimiento de inversiones',       7),
(17, 'George & Hannah Travel', 'EUR', 'Fondo de viajes compartido',       7),
(18, 'Hannah Personal',        'EUR', 'Gastos personales de Hannah',      8),
(19, 'Ivan Personal',          'EUR', 'Gastos personales de Ivan',        9),
(20, 'Ivan Freelance',         'EUR', 'Ingresos y gastos freelance',      9),
(21, 'Ivan & Julia Events',    'EUR', 'Eventos y ocio compartido',        9),
(22, 'Julia Personal',         'EUR', 'Gastos personales de Julia',      10);

-- ============================================================
-- USER_LEDGER
-- ============================================================
INSERT INTO user_ledger (id, user_id, ledger_id, permission) VALUES
(1,  1,  1, 'OWNER'),
(2,  1,  2, 'OWNER'),
(3,  1,  3, 'OWNER'),
(4,  2,  3, 'MEMBER'),
(5,  2,  4, 'OWNER'),
(6,  2,  5, 'OWNER'),
(7,  3,  5, 'MEMBER'),
(8,  4,  5, 'MEMBER'),
(9,  3,  6, 'OWNER'),
(10, 3,  7, 'OWNER'),
(11, 3,  8, 'OWNER'),
(12, 4,  8, 'MEMBER'),
(13, 4,  9, 'OWNER'),
(14, 4, 10, 'OWNER'),
(15, 5, 10, 'MEMBER'),
(16, 5, 11, 'OWNER'),
(17, 5, 12, 'OWNER'),
(18, 6, 13, 'OWNER'),
(19, 6, 14, 'OWNER'),
(20, 7, 14, 'MEMBER'),
(21, 8, 14, 'MEMBER'),
(22, 7, 15, 'OWNER'),
(23, 7, 16, 'OWNER'),
(24, 7, 17, 'OWNER'),
(25, 8, 17, 'MEMBER'),
(26,10, 17, 'MEMBER'),
(27, 8, 18, 'OWNER'),
(28, 9, 19, 'OWNER'),
(29, 9, 20, 'OWNER'),
(30, 9, 21, 'OWNER'),
(31,10, 21, 'MEMBER'),
(32, 2, 21, 'MEMBER'),
(33,10, 22, 'OWNER');

-- ============================================================
-- TRANSACTIONS (100 registros)
-- ============================================================

-- Ledger 1: Alice Personal
INSERT INTO `transaction` (id, amount, type, category, date, note, ledger_id) VALUES
(1,  1800.00, 'INCOME',  'SALARY',        '2025-03-01', 'Nomina marzo',             1),
(2,   320.00, 'EXPENSE', 'RENT',          '2025-03-05', 'Alquiler habitacion',      1),
(3,    85.50, 'EXPENSE', 'FOOD',          '2025-03-10', 'Compra supermercado',      1),
(4,    42.00, 'EXPENSE', 'TRANSPORT',     '2025-03-15', 'Abono transporte',         1),
(5,  1800.00, 'INCOME',  'SALARY',        '2025-04-01', 'Nomina abril',             1),
(6,   320.00, 'EXPENSE', 'RENT',          '2025-04-05', 'Alquiler habitacion',      1),
-- Ledger 2: Alice Savings
(7,   300.00, 'INCOME',  'OTHER',         '2025-03-01', 'Transferencia ahorro',     2),
(8,   300.00, 'INCOME',  'OTHER',         '2025-04-01', 'Transferencia ahorro',     2),
(9,   150.00, 'EXPENSE', 'OTHER',         '2025-04-20', 'Compra fondo indexado',    2),
-- Ledger 3: Alice & Bob Household
(10,  600.00, 'EXPENSE', 'RENT',          '2025-03-01', 'Alquiler piso marzo',      3),
(11,  180.00, 'EXPENSE', 'FOOD',          '2025-03-08', 'Compra semanal',           3),
(12,   65.00, 'EXPENSE', 'OTHER',         '2025-03-20', 'Factura luz',              3),
(13,  600.00, 'EXPENSE', 'RENT',          '2025-04-01', 'Alquiler piso abril',      3),
(14,  175.00, 'EXPENSE', 'FOOD',          '2025-04-09', 'Compra semanal',           3),
-- Ledger 4: Bob Personal
(15, 2200.00, 'INCOME',  'SALARY',        '2025-03-01', 'Nomina marzo',             4),
(16,  120.00, 'EXPENSE', 'ENTERTAINMENT', '2025-03-14', 'Entradas concierto',       4),
(17,   55.00, 'EXPENSE', 'HEALTH',        '2025-03-22', 'Farmacia',                 4),
(18, 2200.00, 'INCOME',  'SALARY',        '2025-04-01', 'Nomina abril',             4),
(19,   90.00, 'EXPENSE', 'SHOPPING',      '2025-04-17', 'Ropa nueva',               4),
-- Ledger 5: Bob & Carlos Trip
(20,  350.00, 'EXPENSE', 'TRANSPORT',     '2025-03-10', 'Vuelos ida',               5),
(21,  280.00, 'EXPENSE', 'ENTERTAINMENT', '2025-03-11', 'Hotel 2 noches',           5),
(22,  145.00, 'EXPENSE', 'FOOD',          '2025-03-12', 'Restaurantes',             5),
(23,  350.00, 'EXPENSE', 'TRANSPORT',     '2025-04-15', 'Vuelos vuelta',            5),
(24,   60.00, 'EXPENSE', 'OTHER',         '2025-04-16', 'Seguro de viaje',          5),
-- Ledger 6: Carlos Personal
(25, 3500.00, 'INCOME',  'SALARY',        '2025-03-01', 'Salario marzo',            6),
(26,  950.00, 'EXPENSE', 'RENT',          '2025-03-01', 'Hipoteca',                 6),
(27,  200.00, 'EXPENSE', 'FOOD',          '2025-03-07', 'Supermercado',             6),
(28, 3500.00, 'INCOME',  'SALARY',        '2025-04-01', 'Salario abril',            6),
(29,  110.00, 'EXPENSE', 'HEALTH',        '2025-04-10', 'Medico privado',           6),
-- Ledger 7: Carlos Business
(30, 1200.00, 'INCOME',  'OTHER',         '2025-03-15', 'Factura cliente A',        7),
(31,  430.00, 'EXPENSE', 'OTHER',         '2025-03-20', 'Licencias software',       7),
(32,  800.00, 'INCOME',  'OTHER',         '2025-04-05', 'Factura cliente B',        7),
(33,   95.00, 'EXPENSE', 'TRANSPORT',     '2025-04-12', 'Viaje de negocios',        7),
-- Ledger 8: Carlos & Diana Family
(34,  400.00, 'EXPENSE', 'FOOD',          '2025-03-03', 'Compra familiar marzo',    8),
(35,  220.00, 'EXPENSE', 'HEALTH',        '2025-03-18', 'Farmacia familia',         8),
(36,  400.00, 'EXPENSE', 'FOOD',          '2025-04-03', 'Compra familiar abril',    8),
(37,   75.00, 'EXPENSE', 'ENTERTAINMENT', '2025-04-21', 'Cine en familia',          8),
-- Ledger 9: Diana Personal
(38, 2800.00, 'INCOME',  'SALARY',        '2025-03-01', 'Salario marzo',            9),
(39,  800.00, 'EXPENSE', 'RENT',          '2025-03-01', 'Alquiler piso',            9),
(40,  160.00, 'EXPENSE', 'SHOPPING',      '2025-03-25', 'Ropa',                     9),
(41, 2800.00, 'INCOME',  'SALARY',        '2025-04-01', 'Salario abril',            9),
(42,   45.00, 'EXPENSE', 'TRANSPORT',     '2025-04-08', 'Abono mensual',            9),
-- Ledger 10: Diana & Ethan Gym
(43,   80.00, 'EXPENSE', 'HEALTH',        '2025-03-01', 'Cuota gimnasio marzo',    10),
(44,   35.00, 'EXPENSE', 'SHOPPING',      '2025-03-15', 'Ropa deportiva',          10),
(45,   80.00, 'EXPENSE', 'HEALTH',        '2025-04-01', 'Cuota gimnasio abril',    10),
(46,   55.00, 'EXPENSE', 'FOOD',          '2025-04-10', 'Suplementos',             10),
-- Ledger 11: Ethan Personal
(47, 1950.00, 'INCOME',  'SALARY',        '2025-03-01', 'Nomina marzo',            11),
(48,  550.00, 'EXPENSE', 'RENT',          '2025-03-01', 'Alquiler',                11),
(49,   70.00, 'EXPENSE', 'FOOD',          '2025-03-12', 'Supermercado',            11),
(50, 1950.00, 'INCOME',  'SALARY',        '2025-04-01', 'Nomina abril',            11),
(51,   30.00, 'EXPENSE', 'ENTERTAINMENT', '2025-04-19', 'Netflix + Spotify',       11),
-- Ledger 12: Ethan Side Projects
(52,  450.00, 'INCOME',  'OTHER',         '2025-03-20', 'Proyecto web freelance',  12),
(53,   60.00, 'EXPENSE', 'OTHER',         '2025-03-22', 'Dominio y hosting',       12),
(54,  700.00, 'INCOME',  'OTHER',         '2025-04-18', 'Contrato app movil',      12),
(55,   40.00, 'EXPENSE', 'OTHER',         '2025-04-20', 'Herramientas dev',        12),
-- Ledger 13: Fiona Personal
(56, 3100.00, 'INCOME',  'SALARY',        '2025-03-01', 'Salario marzo',           13),
(57,  130.00, 'EXPENSE', 'SHOPPING',      '2025-03-09', 'Ropa nueva',              13),
(58,   90.00, 'EXPENSE', 'HEALTH',        '2025-03-28', 'Dentista',                13),
(59, 3100.00, 'INCOME',  'SALARY',        '2025-04-01', 'Salario abril',           13),
(60,  200.00, 'EXPENSE', 'ENTERTAINMENT', '2025-04-13', 'Teatro y cena',           13),
-- Ledger 14: Fiona & George Rent
(61,  750.00, 'EXPENSE', 'RENT',          '2025-03-01', 'Alquiler piso marzo',     14),
(62,   95.00, 'EXPENSE', 'OTHER',         '2025-03-10', 'Internet y gas',          14),
(63,  750.00, 'EXPENSE', 'RENT',          '2025-04-01', 'Alquiler piso abril',     14),
(64,  100.00, 'EXPENSE', 'OTHER',         '2025-04-11', 'Internet y luz',          14),
-- Ledger 15: George Personal
(65, 2600.00, 'INCOME',  'SALARY',        '2025-03-01', 'Nomina marzo',            15),
(66,   75.00, 'EXPENSE', 'FOOD',          '2025-03-06', 'Compra semanal',          15),
(67,  110.00, 'EXPENSE', 'TRANSPORT',     '2025-03-20', 'Gasolina coche',          15),
(68, 2600.00, 'INCOME',  'SALARY',        '2025-04-01', 'Nomina abril',            15),
(69,   45.00, 'EXPENSE', 'HEALTH',        '2025-04-16', 'Consulta medico',         15),
-- Ledger 16: George Investments
(70,  500.00, 'EXPENSE', 'OTHER',         '2025-03-05', 'Compra ETF',              16),
(71,  200.00, 'INCOME',  'OTHER',         '2025-04-02', 'Dividendos',              16),
(72,  500.00, 'EXPENSE', 'OTHER',         '2025-04-05', 'Compra ETF',              16),
-- Ledger 17: George & Hannah Travel
(73,  200.00, 'INCOME',  'OTHER',         '2025-03-01', 'Aportacion George',       17),
(74,  200.00, 'INCOME',  'OTHER',         '2025-03-01', 'Aportacion Hannah',       17),
(75,  380.00, 'EXPENSE', 'TRANSPORT',     '2025-04-10', 'Vuelos verano',           17),
(76,   55.00, 'EXPENSE', 'OTHER',         '2025-04-10', 'Seguro viaje',            17),
-- Ledger 18: Hannah Personal
(77, 2100.00, 'INCOME',  'SALARY',        '2025-03-01', 'Nomina marzo',            18),
(78,  600.00, 'EXPENSE', 'RENT',          '2025-03-01', 'Alquiler habitacion',     18),
(79,   95.00, 'EXPENSE', 'FOOD',          '2025-03-11', 'Supermercado',            18),
(80, 2100.00, 'INCOME',  'SALARY',        '2025-04-01', 'Nomina abril',            18),
(81,   40.00, 'EXPENSE', 'ENTERTAINMENT', '2025-04-22', 'Libros',                  18),
-- Ledger 19: Ivan Personal
(82, 2400.00, 'INCOME',  'SALARY',        '2025-03-01', 'Nomina marzo',            19),
(83,  700.00, 'EXPENSE', 'RENT',          '2025-03-01', 'Alquiler',                19),
(84,  120.00, 'EXPENSE', 'FOOD',          '2025-03-09', 'Supermercado',            19),
(85, 2400.00, 'INCOME',  'SALARY',        '2025-04-01', 'Nomina abril',            19),
(86,   60.00, 'EXPENSE', 'TRANSPORT',     '2025-04-07', 'Abono transporte',        19),
-- Ledger 20: Ivan Freelance
(87,  900.00, 'INCOME',  'OTHER',         '2025-03-18', 'Proyecto diseno web',     20),
(88,   80.00, 'EXPENSE', 'OTHER',         '2025-03-19', 'Adobe Creative Cloud',    20),
(89, 1200.00, 'INCOME',  'OTHER',         '2025-04-22', 'Proyecto backend',        20),
(90,   50.00, 'EXPENSE', 'OTHER',         '2025-04-23', 'Servidor VPS',            20),
-- Ledger 21: Ivan & Julia Events
(91,  150.00, 'INCOME',  'OTHER',         '2025-03-05', 'Aportacion Ivan',         21),
(92,  150.00, 'INCOME',  'OTHER',         '2025-03-05', 'Aportacion Julia',        21),
(93,  220.00, 'EXPENSE', 'ENTERTAINMENT', '2025-03-20', 'Festival musica',         21),
(94,   85.00, 'EXPENSE', 'FOOD',          '2025-03-20', 'Cena post-festival',      21),
(95,  180.00, 'EXPENSE', 'ENTERTAINMENT', '2025-04-25', 'Entradas evento',         21),
-- Ledger 22: Julia Personal
(96,  2300.00, 'INCOME',  'SALARY',       '2025-03-01', 'Nomina marzo',            22),
(97,   650.00, 'EXPENSE', 'RENT',         '2025-03-01', 'Alquiler piso',           22),
(98,   110.00, 'EXPENSE', 'FOOD',         '2025-03-14', 'Supermercado',            22),
(99,  2300.00, 'INCOME',  'SALARY',       '2025-04-01', 'Nomina abril',            22),
(100,  140.00, 'EXPENSE', 'SHOPPING',     '2025-04-18', 'Compra online',           22);

SET FOREIGN_KEY_CHECKS = 1;