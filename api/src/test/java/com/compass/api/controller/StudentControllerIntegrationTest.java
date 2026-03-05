package com.compass.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * StudentControllerの統合テスト
 * H2インメモリDBを使用してAPI全体の動作を検証する
 */
@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("正常: facilitator_id=1の生徒一覧を取得する")
    void testGetStudents_success() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalCount").value(7))
                .andExpect(jsonPath("$.students", hasSize(5))) // デフォルトlimit=5
                .andExpect(jsonPath("$.students[0].id").isNumber())
                .andExpect(jsonPath("$.students[0].name").isString())
                .andExpect(jsonPath("$.students[0].loginId").isString())
                .andExpect(jsonPath("$.students[0].classroom.id").isNumber())
                .andExpect(jsonPath("$.students[0].classroom.name").isString());
    }

    @Test
    @DisplayName("正常: facilitator_id=2の生徒一覧を取得する")
    void testGetStudents_facilitator2() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(3))
                .andExpect(jsonPath("$.students", hasSize(3)));
    }

    @Test
    @DisplayName("正常: ページネーション（page=2, limit=3）")
    void testGetStudents_pagination() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("page", "2")
                .param("limit", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(7))
                .andExpect(jsonPath("$.students", hasSize(3))); // 7件中4-6件目
    }

    @Test
    @DisplayName("正常: ソート（name降順）")
    void testGetStudents_sortByNameDesc() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("sort", "name")
                .param("order", "desc")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(7));
    }

    @Test
    @DisplayName("正常: 部分一致検索（name_like=佐）")
    void testGetStudents_nameLike() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("name_like", "佐"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(2)) // 佐藤、佐々木
                .andExpect(jsonPath("$.students", hasSize(2)));
    }

    @Test
    @DisplayName("正常: 部分一致検索（loginId_like=fizz）")
    void testGetStudents_loginIdLike() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("loginId_like", "fizz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(2)) // fizz9999, fizzbuzz#123
                .andExpect(jsonPath("$.students", hasSize(2)));
    }

    @Test
    @DisplayName("正常: レスポンス構造の確認（佐藤のデータ）")
    void testGetStudents_responseStructure() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.students[0].id").value(1))
                .andExpect(jsonPath("$.students[0].name").value("佐藤"))
                .andExpect(jsonPath("$.students[0].loginId").value("foo123"))
                .andExpect(jsonPath("$.students[0].classroom.id").value(1))
                .andExpect(jsonPath("$.students[0].classroom.name").value("クラスA"));
    }

    @Test
    @DisplayName("エラー: facilitator_idが未指定 → 400")
    void testGetStudents_missingFacilitatorId() throws Exception {
        mockMvc.perform(get("/students"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("エラー: facilitator_idが文字列 → 400")
    void testGetStudents_invalidFacilitatorId() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("エラー: 存在しないfacilitator_id → 404")
    void testGetStudents_notFound() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("エラー: 不正なsortキー → 400")
    void testGetStudents_invalidSort() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("sort", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("エラー: 不正なorder → 400")
    void testGetStudents_invalidOrder() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("order", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("エラー: pageが0以下 → 400")
    void testGetStudents_invalidPage() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("page", "0"))
                .andExpect(status().isBadRequest());
    }

    // ========== 異常系（追加） ==========

    @Test
    @DisplayName("エラー: limit=0 → 400")
    void testGetStudents_limitZero() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("limit", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("エラー: limitが負の値 → 400")
    void testGetStudents_limitNegative() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("limit", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("エラー: pageが負の値 → 400")
    void testGetStudents_pageNegative() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("page", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("エラー: pageが非整数文字列 → 400")
    void testGetStudents_pageNotInteger() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("page", "abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("エラー: limitが非整数文字列 → 400")
    void testGetStudents_limitNotInteger() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("limit", "abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("エラー: facilitator_idが負の値 → 404")
    void testGetStudents_facilitatorIdNegative() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("エラー: name_likeフィルタで該当なし → 404")
    void testGetStudents_nameLikeNoMatch() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("name_like", "該当なし"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("エラー: loginId_likeフィルタで該当なし → 404")
    void testGetStudents_loginIdLikeNoMatch() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("loginId_like", "nomatch"))
                .andExpect(status().isNotFound());
    }

    // ========== 境界値 ==========

    @Test
    @DisplayName("境界値: limit=1（最小有効値）で1件だけ取得")
    void testGetStudents_limitOne() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(7))
                .andExpect(jsonPath("$.students", hasSize(1)));
    }

    @Test
    @DisplayName("境界値: limit=全件数で1ページにちょうど収まる")
    void testGetStudents_limitExactTotal() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("limit", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(7))
                .andExpect(jsonPath("$.students", hasSize(7)));
    }

    @Test
    @DisplayName("境界値: 全件ちょうどのlimitで2ページ目 → 200だが空配列")
    void testGetStudents_pageAfterExactFit() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("page", "2")
                .param("limit", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(7))
                .andExpect(jsonPath("$.students", hasSize(0)));
    }

    @Test
    @DisplayName("境界値: 最終ページで残り1件（page=3, limit=3）")
    void testGetStudents_lastPagePartial() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("page", "3")
                .param("limit", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(7))
                .andExpect(jsonPath("$.students", hasSize(1)));
    }

    @Test
    @DisplayName("境界値: limitが全件数を超える場合は全件返却")
    void testGetStudents_limitExceedsTotal() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(7))
                .andExpect(jsonPath("$.students", hasSize(7)));
    }

    // ========== 正常系（追加） ==========

    @Test
    @DisplayName("正常: デフォルトソート（id昇順）の順序を検証")
    void testGetStudents_defaultSortOrder() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("limit", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.students[0].id").value(1))
                .andExpect(jsonPath("$.students[1].id").value(3))
                .andExpect(jsonPath("$.students[2].id").value(4))
                .andExpect(jsonPath("$.students[3].id").value(6))
                .andExpect(jsonPath("$.students[4].id").value(7))
                .andExpect(jsonPath("$.students[5].id").value(9))
                .andExpect(jsonPath("$.students[6].id").value(10));
    }

    @Test
    @DisplayName("正常: sort=loginId昇順のソート順を検証")
    void testGetStudents_sortByLoginIdAsc() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("sort", "loginId")
                .param("order", "asc")
                .param("limit", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.students[0].loginId").value("baz789"))
                .andExpect(jsonPath("$.students[1].loginId").value("fizz9999"))
                .andExpect(jsonPath("$.students[2].loginId").value("fizzbuzz#123"))
                .andExpect(jsonPath("$.students[3].loginId").value("foo123"))
                .andExpect(jsonPath("$.students[4].loginId").value("hoge0000"))
                .andExpect(jsonPath("$.students[5].loginId").value("piyo5678"))
                .andExpect(jsonPath("$.students[6].loginId").value("xxx:42"));
    }

    @Test
    @DisplayName("正常: name_likeとloginId_likeの複合フィルタ")
    void testGetStudents_combinedFilter() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("name_like", "J")
                .param("loginId_like", "fizz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.students[0].name").value("Johnson"))
                .andExpect(jsonPath("$.students[0].loginId").value("fizzbuzz#123"));
    }

    @Test
    @DisplayName("正常: 空文字のname_likeはフィルタ無効扱い")
    void testGetStudents_emptyNameLike() throws Exception {
        mockMvc.perform(get("/students")
                .param("facilitator_id", "1")
                .param("name_like", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(7));
    }
}
