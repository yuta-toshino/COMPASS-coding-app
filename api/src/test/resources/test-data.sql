-- テスト用データ（PDF表1と同じ）
INSERT INTO teachers (id) VALUES (1), (2);

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

INSERT INTO classrooms (id, name) VALUES
(1, 'クラスA'),
(2, 'クラスB'),
(3, 'クラスC');

INSERT INTO teacher_student_classroom (teacher_id, student_id, classroom_id) VALUES
(1, 1, 1),
(2, 2, 2),
(1, 3, 3),
(1, 4, 1),
(2, 5, 2),
(1, 6, 3),
(1, 7, 1),
(2, 8, 2),
(1, 9, 3),
(1, 10, 1);
