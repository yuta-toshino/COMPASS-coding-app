package com.compass.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * クラス情報のレスポンスDTO
 */
public class ClassroomResponse {

    /** クラスID */
    @JsonProperty("id")
    private Integer id;

    /** クラス名 */
    @JsonProperty("name")
    private String name;

    public ClassroomResponse() {
    }

    public ClassroomResponse(Integer id, String name) {
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
