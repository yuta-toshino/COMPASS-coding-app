package com.compass.api.entity;

import jakarta.persistence.*;

/**
 * 生徒エンティティ
 */
@Entity
@Table(name = "students")
public class Student {

    /** 生徒ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 生徒名 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** ログインID */
    @Column(name = "login_id", nullable = false, unique = true, length = 100)
    private String loginId;

    // デフォルトコンストラクタ（JPA用）
    public Student() {
    }

    public Student(Integer id, String name, String loginId) {
        this.id = id;
        this.name = name;
        this.loginId = loginId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
}
