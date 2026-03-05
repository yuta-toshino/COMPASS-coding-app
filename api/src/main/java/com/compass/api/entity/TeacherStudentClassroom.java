package com.compass.api.entity;

import jakarta.persistence.*;

/**
 * 教師-生徒-クラス 関連エンティティ
 */
@Entity
@Table(name = "teacher_student_classroom")
public class TeacherStudentClassroom {

    /** 関連ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 教師ID */
    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId;

    /** 生徒（多対一） */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /** クラス（多対一） */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    // デフォルトコンストラクタ（JPA用）
    public TeacherStudentClassroom() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }
}
