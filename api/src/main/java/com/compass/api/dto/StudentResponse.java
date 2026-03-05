package com.compass.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 生徒情報のレスポンスDTO
 */
public class StudentResponse {

    /** 生徒ID */
    @JsonProperty("id")
    private Integer id;

    /** 生徒名 */
    @JsonProperty("name")
    private String name;

    /** ログインID */
    @JsonProperty("loginId")
    private String loginId;

    /** クラス情報 */
    @JsonProperty("classroom")
    private ClassroomResponse classroom;

    public StudentResponse() {
    }

    public StudentResponse(Integer id, String name, String loginId, ClassroomResponse classroom) {
        this.id = id;
        this.name = name;
        this.loginId = loginId;
        this.classroom = classroom;
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

    public ClassroomResponse getClassroom() {
        return classroom;
    }

    public void setClassroom(ClassroomResponse classroom) {
        this.classroom = classroom;
    }
}
