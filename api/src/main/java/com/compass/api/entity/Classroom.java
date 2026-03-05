package com.compass.api.entity;

import jakarta.persistence.*;

/**
 * クラスエンティティ
 */
@Entity
@Table(name = "classrooms")
public class Classroom {

    /** クラスID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** クラス名 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // デフォルトコンストラクタ（JPA用）
    public Classroom() {
    }

    public Classroom(Integer id, String name) {
        this.id = id;
        this.name = name;
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
}
