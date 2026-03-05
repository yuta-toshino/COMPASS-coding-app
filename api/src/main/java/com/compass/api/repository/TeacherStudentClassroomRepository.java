package com.compass.api.repository;

import com.compass.api.entity.TeacherStudentClassroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 教師-生徒-クラス関連テーブルのリポジトリ
 * 教師IDベースで生徒情報を取得するクエリを提供する
 */
@Repository
public interface TeacherStudentClassroomRepository extends JpaRepository<TeacherStudentClassroom, Integer> {

    /**
     * 教師IDに紐づく全関連レコードを取得する
     * 生徒情報とクラス情報をJOIN FETCHで一括取得する
     *
     * @param teacherId 教師ID
     * @return 関連レコードのリスト
     */
    @Query("SELECT tsc FROM TeacherStudentClassroom tsc " +
            "JOIN FETCH tsc.student s " +
            "JOIN FETCH tsc.classroom c " +
            "WHERE tsc.teacherId = :teacherId")
    List<TeacherStudentClassroom> findByTeacherIdWithStudentAndClassroom(@Param("teacherId") Integer teacherId);
}
