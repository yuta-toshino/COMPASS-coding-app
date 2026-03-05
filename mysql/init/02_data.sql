-- 初期データ投入（PDF 表1のデータ）
SET NAMES utf8mb4;
USE compass;

-- 教師データ
INSERT INTO teachers (id) VALUES (1), (2);

-- 生徒データ
INSERT INTO students (id, name, login_id) VALUES
(1, '佐藤', 'foo123'),
(2, '鈴木', 'bar456'),
(3, '田中', 'baz789'),
(4, '加藤', 'hoge0000'),
(5, '太田', 'fuga1234'),
(6, '佐々木', 'piyo5678'),
(7, '小田原', 'fizz9999'),
(8, 'Smith', 'buzz777'),
(9, 'Johnson', 'fizzbuzz#123'),
(10, 'Williams', 'xxx:42');

-- クラスデータ
INSERT INTO classrooms (id, name) VALUES
(1, 'クラスA'),
(2, 'クラスB'),
(3, 'クラスC');

-- 教師-生徒-クラス 関連データ
INSERT INTO teacher_student_classroom (teacher_id, student_id, classroom_id) VALUES
(1, 1, 1),   -- 教師1 - 佐藤 - クラスA
(2, 2, 2),   -- 教師2 - 鈴木 - クラスB
(1, 3, 3),   -- 教師1 - 田中 - クラスC
(1, 4, 1),   -- 教師1 - 加藤 - クラスA
(2, 5, 2),   -- 教師2 - 太田 - クラスB
(1, 6, 3),   -- 教師1 - 佐々木 - クラスC
(1, 7, 1),   -- 教師1 - 小田原 - クラスA
(2, 8, 2),   -- 教師2 - Smith - クラスB
(1, 9, 3),   -- 教師1 - Johnson - クラスC
(1, 10, 1);  -- 教師1 - Williams - クラスA
