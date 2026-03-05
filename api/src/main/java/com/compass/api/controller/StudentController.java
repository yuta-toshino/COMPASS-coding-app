package com.compass.api.controller;

import com.compass.api.dto.StudentsListResponse;
import com.compass.api.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

/**
 * 生徒情報APIのコントローラー
 * GET /students エンドポイントを提供する
 */
@RestController
public class StudentController {

    /** 許可されるソートキー */
    private static final Set<String> VALID_SORT_KEYS = Set.of("id", "name", "loginId");

    /** 許可されるソート順 */
    private static final Set<String> VALID_ORDER_VALUES = Set.of("asc", "desc");

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * 生徒一覧を取得する
     *
     * @param facilitatorId 教師ID（必須）
     * @param page          ページ番号（デフォルト: 1）
     * @param limit         1ページあたりの表示数（デフォルト: 5）
     * @param sort          ソートキー（デフォルト: id）
     * @param order         ソート順（デフォルト: asc）
     * @param nameLike      生徒名の部分一致検索
     * @param loginIdLike   ログインIDの部分一致検索
     * @return 生徒一覧レスポンス
     */
    @GetMapping("/students")
    public ResponseEntity<StudentsListResponse> getStudents(
            @RequestParam(name = "facilitator_id") Integer facilitatorId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "limit", defaultValue = "5") int limit,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "order", defaultValue = "asc") String order,
            @RequestParam(name = "name_like", required = false) String nameLike,
            @RequestParam(name = "loginId_like", required = false) String loginIdLike) {

        // バリデーション: ページ番号は1以上
        if (page < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pageは1以上を指定してください");
        }

        // バリデーション: limitは1以上
        if (limit < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limitは1以上を指定してください");
        }

        // バリデーション: ソートキーの検証
        if (!VALID_SORT_KEYS.contains(sort)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "sortには id, name, loginId のいずれかを指定してください");
        }

        // バリデーション: ソート順の検証
        if (!VALID_ORDER_VALUES.contains(order.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "orderには asc または desc を指定してください");
        }

        // 生徒一覧を取得
        StudentsListResponse response = studentService.getStudents(
                facilitatorId, page, limit, sort, order, nameLike, loginIdLike);

        // 該当する生徒が存在しない場合は404を返す
        if (response.getTotalCount() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "リクエストに該当する生徒が存在しません");
        }

        return ResponseEntity.ok(response);
    }
}
