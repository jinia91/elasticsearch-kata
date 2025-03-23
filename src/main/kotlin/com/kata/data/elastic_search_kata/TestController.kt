package com.kata.data.elastic_search_kata

import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.web.bind.annotation.GetMapping
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
                        .fields("TITLE", "CONTENT")
                }
            }
            .build()

        val result = elasticsearchOperations.search(query, Article::class.java)
        return result.map { it.content }.toList()
    }
}