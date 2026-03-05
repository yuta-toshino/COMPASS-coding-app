package com.compass.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * 生徒一覧のレスポンスDTO
 * students配列とtotalCountを保持する
 */
public class StudentsListResponse {

    /** 生徒一覧 */
    @JsonProperty("students")
    private List<StudentResponse> students;

    /** 条件に該当する全件数 */
    @JsonProperty("totalCount")
    private int totalCount;

    public StudentsListResponse() {
    }

    public StudentsListResponse(List<StudentResponse> students, int totalCount) {
        this.students = students;
        this.totalCount = totalCount;
    }

    public List<StudentResponse> getStudents() {
        return students;
    }

    public void setStudents(List<StudentResponse> students) {
        this.students = students;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
