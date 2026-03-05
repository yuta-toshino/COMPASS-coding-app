package com.compass.api.service;

import com.compass.api.dto.ClassroomResponse;
import com.compass.api.dto.StudentResponse;
import com.compass.api.dto.StudentsListResponse;
import com.compass.api.entity.Classroom;
import com.compass.api.entity.Student;
import com.compass.api.entity.TeacherStudentClassroom;
import com.compass.api.repository.TeacherStudentClassroomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * StudentServiceのユニットテスト
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private TeacherStudentClassroomRepository repository;

    @InjectMocks
    private StudentService studentService;

    private List<TeacherStudentClassroom> testData;

    @BeforeEach
    void setUp() {
        // テストデータの作成
        testData = List.of(
                createRecord(1, "佐藤", "foo123", 1, "クラスA"),
                createRecord(3, "田中", "baz789", 3, "クラスC"),
                createRecord(4, "加藤", "hoge0000", 1, "クラスA"),
                createRecord(6, "佐々木", "piyo5678", 3, "クラスC"),
                createRecord(7, "小田原", "fizz9999", 1, "クラスA"),
                createRecord(9, "Johnson", "fizzbuzz#123", 3, "クラスC"),
                createRecord(10, "Williams", "xxx:42", 1, "クラスA"));
    }

    /**
     * テスト用の関連レコードを作成するヘルパーメソッド
     */
    private TeacherStudentClassroom createRecord(int studentId, String name, String loginId,
            int classroomId, String classroomName) {
        Student student = new Student(studentId, name, loginId);
        Classroom classroom = new Classroom(classroomId, classroomName);

        TeacherStudentClassroom tsc = new TeacherStudentClassroom();
        tsc.setTeacherId(1);
        tsc.setStudent(student);
        tsc.setClassroom(classroom);
        return tsc;
    }

    @Test
    @DisplayName("基本取得: 教師ID=1の生徒一覧をデフォルト設定で取得する")
    void testGetStudents_default() {
        when(repository.findByTeacherIdWithStudentAndClassroom(1)).thenReturn(testData);

        StudentsListResponse response = studentService.getStudents(1, 1, 5, "id", "asc", null, null);

        assertEquals(7, response.getTotalCount());
        assertEquals(5, response.getStudents().size());
        // ID昇順で最初の5件
        assertEquals(1, response.getStudents().get(0).getId());
        assertEquals("佐藤", response.getStudents().get(0).getName());
    }

    @Test
    @DisplayName("ページネーション: 2ページ目を取得する")
    void testGetStudents_pagination() {
        when(repository.findByTeacherIdWithStudentAndClassroom(1)).thenReturn(testData);

        StudentsListResponse response = studentService.getStudents(1, 2, 5, "id", "asc", null, null);

        assertEquals(7, response.getTotalCount());
        assertEquals(2, response.getStudents().size()); // 残り2件
    }

    @Test
    @DisplayName("ページネーション: limit=3の場合")
    void testGetStudents_customLimit() {
        when(repository.findByTeacherIdWithStudentAndClassroom(1)).thenReturn(testData);

        StudentsListResponse response = studentService.getStudents(1, 1, 3, "id", "asc", null, null);

        assertEquals(7, response.getTotalCount());
        assertEquals(3, response.getStudents().size());
    }

    @Test
    @DisplayName("ソート: name降順で取得する")
    void testGetStudents_sortByNameDesc() {
        when(repository.findByTeacherIdWithStudentAndClassroom(1)).thenReturn(testData);

        StudentsListResponse response = studentService.getStudents(1, 1, 10, "name", "desc", null, null);

        // 日本語ソートの確認（名前降順）
        List<StudentResponse> students = response.getStudents();
        for (int i = 0; i < students.size() - 1; i++) {
            assertTrue(students.get(i).getName().compareTo(students.get(i + 1).getName()) >= 0);
        }
    }

    @Test
    @DisplayName("ソート: loginId昇順で取得する")
    void testGetStudents_sortByLoginIdAsc() {
        when(repository.findByTeacherIdWithStudentAndClassroom(1)).thenReturn(testData);

        StudentsListResponse response = studentService.getStudents(1, 1, 10, "loginId", "asc", null, null);

        List<StudentResponse> students = response.getStudents();
        for (int i = 0; i < students.size() - 1; i++) {
            assertTrue(students.get(i).getLoginId().compareTo(students.get(i + 1).getLoginId()) <= 0);
        }
    }

    @Test
    @DisplayName("部分一致検索: name_like=佐 で検索する")
    void testGetStudents_nameLike() {
        when(repository.findByTeacherIdWithStudentAndClassroom(1)).thenReturn(testData);

        StudentsListResponse response = studentService.getStudents(1, 1, 10, "id", "asc", "佐", null);

        assertEquals(2, response.getTotalCount()); // 佐藤、佐々木
        assertTrue(response.getStudents().stream().allMatch(s -> s.getName().contains("佐")));
    }

    @Test
    @DisplayName("部分一致検索: loginId_like=fizz で検索する")
    void testGetStudents_loginIdLike() {
        when(repository.findByTeacherIdWithStudentAndClassroom(1)).thenReturn(testData);

        StudentsListResponse response = studentService.getStudents(1, 1, 10, "id", "asc", null, "fizz");

        assertEquals(2, response.getTotalCount()); // fizz9999, fizzbuzz#123
        assertTrue(response.getStudents().stream().allMatch(s -> s.getLoginId().contains("fizz")));
    }

    @Test
    @DisplayName("該当なし: 存在しない教師IDの場合は空リストを返す")
    void testGetStudents_noResults() {
        when(repository.findByTeacherIdWithStudentAndClassroom(999)).thenReturn(List.of());

        StudentsListResponse response = studentService.getStudents(999, 1, 5, "id", "asc", null, null);

        assertEquals(0, response.getTotalCount());
        assertTrue(response.getStudents().isEmpty());
    }

    @Test
    @DisplayName("ページ範囲外: 存在しないページ番号の場合は空リストを返す")
    void testGetStudents_pageOutOfRange() {
        when(repository.findByTeacherIdWithStudentAndClassroom(1)).thenReturn(testData);

        StudentsListResponse response = studentService.getStudents(1, 100, 5, "id", "asc", null, null);

        assertEquals(7, response.getTotalCount());
        assertTrue(response.getStudents().isEmpty());
    }

    // ========== 境界値（追加） ==========

    @Test
    @DisplayName("境界値: limit=1で1件ずつ取得する")
    void testGetStudents_limitOne() {
        when(repository.findByTeacherIdWithStudentAndClassroom(1)).thenReturn(testData);

        StudentsListResponse response = studentService.getStudents(1, 1, 1, "id", "asc", null, null);

        assertEquals(7, response.getTotalCount());
        assertEquals(1, response.getStudents().size());
        assertEquals(1, response.getStudents().get(0).getId());
    }

    @Test
    @DisplayName("境界値: limit=全件数でちょうど1ページに収まる")
    void testGetStudents_limitExactTotal() {
        when(repository.findByTeacherIdWithStudentAndClassroom(1)).thenReturn(testData);

        StudentsListResponse response = studentService.getStudents(1, 1, 7, "id", "asc", null, null);

        assertEquals(7, response.getTotalCount());
        assertEquals(7, response.getStudents().size());
    }

    @Test
    @DisplayName("境界値: name_likeとloginId_likeの複合フィルタ")
    void testGetStudents_combinedFilter() {
        when(repository.findByTeacherIdWithStudentAndClassroom(1)).thenReturn(testData);

        StudentsListResponse response = studentService.getStudents(1, 1, 10, "id", "asc", "J", "fizz");

        assertEquals(1, response.getTotalCount());
        assertEquals("Johnson", response.getStudents().get(0).getName());
        assertEquals("fizzbuzz#123", response.getStudents().get(0).getLoginId());
    }

    // ========== 正常系（追加） ==========

    @Test
    @DisplayName("ソート: id降順で取得する")
    void testGetStudents_sortByIdDesc() {
        when(repository.findByTeacherIdWithStudentAndClassroom(1)).thenReturn(testData);

        StudentsListResponse response = studentService.getStudents(1, 1, 10, "id", "desc", null, null);

        List<StudentResponse> students = response.getStudents();
        assertEquals(10, students.get(0).getId());
        assertEquals(1, students.get(students.size() - 1).getId());
        for (int i = 0; i < students.size() - 1; i++) {
            assertTrue(students.get(i).getId() > students.get(i + 1).getId());
        }
    }

    @Test
    @DisplayName("ソート: name昇順で取得する")
    void testGetStudents_sortByNameAsc() {
        when(repository.findByTeacherIdWithStudentAndClassroom(1)).thenReturn(testData);

        StudentsListResponse response = studentService.getStudents(1, 1, 10, "name", "asc", null, null);

        List<StudentResponse> students = response.getStudents();
        for (int i = 0; i < students.size() - 1; i++) {
            assertTrue(students.get(i).getName().compareTo(students.get(i + 1).getName()) <= 0);
        }
    }

    @Test
    @DisplayName("正常: 空文字のname_likeはフィルタなし扱い")
    void testGetStudents_emptyNameLike() {
        when(repository.findByTeacherIdWithStudentAndClassroom(1)).thenReturn(testData);

        StudentsListResponse response = studentService.getStudents(1, 1, 10, "id", "asc", "", null);

        assertEquals(7, response.getTotalCount());
    }

    @Test
    @DisplayName("正常: 空文字のloginId_likeはフィルタなし扱い")
    void testGetStudents_emptyLoginIdLike() {
        when(repository.findByTeacherIdWithStudentAndClassroom(1)).thenReturn(testData);

        StudentsListResponse response = studentService.getStudents(1, 1, 10, "id", "asc", null, "");

        assertEquals(7, response.getTotalCount());
    }
}
