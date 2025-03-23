package com.kata.data.elastic_search_kata

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field

@Document(indexName = "article_filtered_stream")
data class Article(
    @field:Id
    @field:Field(
        name = "ID",
    )
    val id: String,

    @field:Field(
        name = "TITLE",
    )
    val title: String,

    @field:Field(
        name = "CONTENTS",
    )
    val content: String,

    @field:Field(
        name = "STATUS",
    )
    val status: String,
)