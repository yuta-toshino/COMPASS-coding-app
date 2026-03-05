package com.compass.api.service;

import com.compass.api.dto.ClassroomResponse;
import com.compass.api.dto.StudentResponse;
import com.compass.api.dto.StudentsListResponse;
import com.compass.api.entity.TeacherStudentClassroom;
import com.compass.api.repository.TeacherStudentClassroomRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 生徒情報を取得するサービス層
 * ページネーション、ソート、部分一致検索のロジックを担当する
 */
@Service
public class StudentService {

    private final TeacherStudentClassroomRepository repository;

    public StudentService(TeacherStudentClassroomRepository repository) {
        this.repository = repository;
    }

    /**
     * 教師IDに紐づく生徒一覧を取得する
     *
     * @param facilitatorId 教師ID（必須）
     * @param page          ページ番号（デフォルト: 1）
     * @param limit         1ページあたりの表示数（デフォルト: 5）
     * @param sort          ソートキー（id, name, loginId）
     * @param order         ソート順（asc, desc）
     * @param nameLike      生徒名の部分一致検索
     * @param loginIdLike   ログインIDの部分一致検索
     * @return 生徒一覧レスポンス
     */
    public StudentsListResponse getStudents(
            Integer facilitatorId,
            int page,
            int limit,
            String sort,
            String order,
            String nameLike,
            String loginIdLike) {

        // 教師IDに紐づく関連データを全件取得
        List<TeacherStudentClassroom> records = repository.findByTeacherIdWithStudentAndClassroom(facilitatorId);

        // DTOに変換
        List<StudentResponse> studentResponses = records.stream()
                .map(tsc -> new StudentResponse(
                        tsc.getStudent().getId(),
                        tsc.getStudent().getName(),
                        tsc.getStudent().getLoginId(),
                        new ClassroomResponse(
                                tsc.getClassroom().getId(),
                                tsc.getClassroom().getName())))
                .collect(Collectors.toList());

        // 部分一致検索（name_like）
        if (nameLike != null && !nameLike.isEmpty()) {
            studentResponses = studentResponses.stream()
                    .filter(s -> s.getName().contains(nameLike))
                    .collect(Collectors.toList());
        }

        // 部分一致検索（loginId_like）
        if (loginIdLike != null && !loginIdLike.isEmpty()) {
            studentResponses = studentResponses.stream()
                    .filter(s -> s.getLoginId().contains(loginIdLike))
                    .collect(Collectors.toList());
        }

        // ソート
        Comparator<StudentResponse> comparator = getComparator(sort);
        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }
        studentResponses.sort(comparator);

        // 全件数（ページネーション前）
        int totalCount = studentResponses.size();

        // ページネーション
        int fromIndex = (page - 1) * limit;
        int toIndex = Math.min(fromIndex + limit, totalCount);

        if (fromIndex >= totalCount) {
            // ページ番号が範囲外の場合は空リストを返す
            return new StudentsListResponse(List.of(), totalCount);
        }

        List<StudentResponse> pagedStudents = studentResponses.subList(fromIndex, toIndex);

        return new StudentsListResponse(pagedStudents, totalCount);
    }

    /**
     * ソートキーに対応するComparatorを返す
     *
     * @param sort ソートキー
     * @return Comparator
     */
    private Comparator<StudentResponse> getComparator(String sort) {
        return switch (sort) {
            case "name" -> Comparator.comparing(StudentResponse::getName);
            case "loginId" -> Comparator.comparing(StudentResponse::getLoginId);
            default -> Comparator.comparing(StudentResponse::getId);
        };
    }
}
