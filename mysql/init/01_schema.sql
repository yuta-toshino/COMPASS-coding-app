-- バックエンド職能課題 テーブル定義
-- 正規化されたテーブル設計

SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS compass CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE compass;

-- 教師テーブル
CREATE TABLE teachers (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '教師ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教師マスタ';

-- 生徒テーブル
CREATE TABLE students (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '生徒ID',
    name VARCHAR(100) NOT NULL COMMENT '生徒名',
    login_id VARCHAR(100) NOT NULL COMMENT 'ログインID',
    UNIQUE KEY uk_login_id (login_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='生徒マスタ';

-- クラステーブル
CREATE TABLE classrooms (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'クラスID',
    name VARCHAR(100) NOT NULL COMMENT 'クラス名'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='クラスマスタ';

-- 教師-生徒-クラス 関連テーブル
CREATE TABLE teacher_student_classroom (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '関連ID',
    teacher_id INT NOT NULL COMMENT '教師ID',
    student_id INT NOT NULL COMMENT '生徒ID',
    classroom_id INT NOT NULL COMMENT 'クラスID',
    CONSTRAINT fk_tsc_teacher FOREIGN KEY (teacher_id) REFERENCES teachers(id),
    CONSTRAINT fk_tsc_student FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT fk_tsc_classroom FOREIGN KEY (classroom_id) REFERENCES classrooms(id),
    UNIQUE KEY uk_teacher_student (teacher_id, student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教師-生徒-クラス関連';
