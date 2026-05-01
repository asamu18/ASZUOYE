package com.example.aszuoye.ui

data class News(
    val title: String,
    val content: String
)

object NewsRepository {
    fun sampleNews(): List<News> {
        return List(20) { index ->
            val i = index + 1
            News(
                title = "新闻标题 $i",
                content = buildString {
                    append("这是第 $i 条新闻的正文内容。\n\n")
                    append("本示例用于演示“碎片（Fragment）+ 列表/详情”结构：\n")
                    append("1）左侧列表展示标题\n")
                    append("2）点击后在详情页显示正文\n\n")
                    append("你可以把这里替换成真实接口数据或本地数据库数据。")
                }
            )
        }
    }
}

