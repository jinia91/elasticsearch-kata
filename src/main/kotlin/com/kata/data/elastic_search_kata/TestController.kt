package com.kata.data.elastic_search_kata

import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class TestController(
    private val elasticsearchOperations: ElasticsearchOperations
) {
    @GetMapping("/search")
    fun searchArticlesByKeyword(@RequestParam keyword: String): List<Article> {
        val query = NativeQuery.builder()
            .withQuery { q ->
                q.multiMatch { mm ->
                    mm.query(keyword)
                        .fields("TITLE", "CONTENTS")
                }
            }
            .build()

        val result = elasticsearchOperations.search(query, Article::class.java)
        return result.map { it.content }.toList()
    }

    data class Response(
        val articles: List<Article>
    )
    @GetMapping("/search2")
    fun searchArticlesByKeyword2(@RequestParam keyword: String): Response {
        val query = NativeQuery.builder()
            .withQuery { q ->
                q.bool { b ->
                    b.must { m ->
                        m.match { mm -> mm.query("DRAFT").field("STATUS") }
                    }
                    b.must { m ->
                        m.multiMatch { mm ->
                            mm.query(keyword)
                                .fields("TITLE^2", "CONTENTS")
                                .type(TextQueryType.MostFields)
                        }
                    }
                }
            }
            .withPageable(PageRequest.of(0, 30))
            .build()

        val result = elasticsearchOperations.search(query, Article::class.java)
        return Response(result.map { it.content }.toList())
    }

    @PostMapping("/add")
    fun addTestArticles() {
        // 검색을 사용할것이므로 다양한 검색 패턴 무작위
        val koreanKeywordList = listOf("테스트", "검색", "엘라스틱서치", "키워드", "잘되나", "이게", "아니", "멍청", "멍청아", "멍청아아", "멍청아아아",)

        // 500개 draft 상태의 article 추가, 키워드 무작위 사용
        for (i in 1..500) {
            val article = Article(
                id = i.toString(),
                title = "제목" + koreanKeywordList.random(),
                content = "내용" + koreanKeywordList.random(),
                status = "DRAFT"
            )
            elasticsearchOperations.save(article)
        }

        // 500개 published 상태의 article 추가
        for (i in 501..1000) {
            val article = Article(
                id = i.toString(),
                title = "제목" + koreanKeywordList.random(),
                content = "내용" + koreanKeywordList.random(),
                status = "PUBLISHED"
            )
            elasticsearchOperations.save(article)
        }
    }
}