package com.setung.service

import com.setung.config.TestContainerConfig
import com.setung.document.PostDocument
import com.setung.repo.PostDocRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import kotlin.test.assertEquals

@SpringBootTest
@Import(TestContainerConfig::class)
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class PostDocServiceTest {

    @Autowired
    lateinit var postDocService: PostDocService

    @Autowired
    lateinit var postDocRepository: PostDocRepository

    @BeforeAll
    fun setupAll() {
        postDocRepository.deleteAll()
        saveSampleData()
    }

    @Test
    @DisplayName("운동 태그 exact match 검색")
    fun searchExactMatch() {
        val result = postDocService.searchByTag("운동", Long.MAX_VALUE, 100)
        assertEquals(listOf("7"), result.map { it.id })
    }

    @Test
    @DisplayName("드라이버 fuzzy 검색")
    fun searchFuzzy() {
        val result = postDocService.searchByTag("드라이버", Long.MAX_VALUE, 100)
        assertTrue(result.map { it.id }.containsAll(listOf("6", "9")))
    }

    @Test
    @DisplayName("ngram 검색 - 동물 키워드로 반려동물 문서 검색")
    fun searchNgram() {
        val result = postDocService.searchByTag("동물", Long.MAX_VALUE, 100)
        assertEquals(listOf("6"), result.map { it.id })
    }

    @Test
    @DisplayName("isVisible=false 문서는 검색 제외")
    fun search_shouldExcludeInvisibleDocs() {
        val result = postDocService.searchByTag("헬스", Long.MAX_VALUE, 100)
        assertTrue(result.isEmpty())
    }

    @Test
    @DisplayName("searchAfter를 활용한 페이징 확인")
    fun searchPagingWithSearchAfter() {
        val firstPage = postDocService.searchByTag("여행", Long.MAX_VALUE, 2)
        assertEquals(listOf("9", "3"), firstPage.map { it.id })

        val lastId = firstPage.last().id.toLong()
        val secondPage = postDocService.searchByTag("여행", lastId, 2)
        assertEquals(listOf("1"), secondPage.map { it.id })
    }

    fun saveSampleData() {
        val sampleDocuments = listOf(
            PostDocument("1", "http://example.com/1.jpg", true, "user1", listOf("카페", "여행")),
            PostDocument("2", "http://example.com/2.jpg", false, "user2", listOf("운동", "헬스")),
            PostDocument("3", "http://example.com/3.jpg", true, "user3", listOf("여행", "등산")),
            PostDocument("4", "http://example.com/4.jpg", true, "user1", listOf("카페", "맛집")),
            PostDocument("5", "http://example.com/5.jpg", false, "user4", listOf("독서", "코딩")),
            PostDocument("6", "http://example.com/6.jpg", true, "user5", listOf("반려동물", "드라이브")),
            PostDocument("7", "http://example.com/7.jpg", true, "user2", listOf("운동", "등산")),
            PostDocument("8", "http://example.com/8.jpg", false, "user3", listOf("코딩", "헬스")),
            PostDocument("9", "http://example.com/9.jpg", true, "user4", listOf("여행", "드라이브")),
            PostDocument("10", "http://example.com/10.jpg", true, "user5", listOf("카페", "독서"))
        )

        postDocRepository.saveAll(sampleDocuments)
    }

}