package homework03.json.comment

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

data class CommentsSnapshot(val comments: List<Comment>) {
    fun flatten() : List<Comment> {
        val line: MutableList<Comment> = arrayListOf()
        fun rec(comment: Comment) {
            line.add(comment)
            for (reply in comment.replies) {
                rec(reply)
            }
        }
        comments.forEach(::rec)
        return line
    }

    companion object {
        fun deserialize(objectMapper: ObjectMapper, json : String) : CommentsSnapshot {
            val tree: JsonNode = objectMapper.readTree(json)
            var commentsId = 0L
            fun deserializeComment(commentJsonNode: JsonNode, parentId: Long?, depth: Int): Comment {
                val curId = commentsId++
                val child: MutableList<Comment> = arrayListOf()
                try {
                    for (ch in commentJsonNode["data"]["replies"]["data"]["children"]) {
                        try {
                            val comment = deserializeComment(ch, curId, depth + 1)
                            child.add(comment)
                        } catch (_: NullPointerException) {}
                    }
                } catch (_: NullPointerException) {}

                var body: String
                var author: String

                body = try {
                    commentJsonNode["data"]["body"].toPrettyString()
                } catch (_: NullPointerException) {
                    ""
                }

                author = try {
                    commentJsonNode["data"]["author_fullname"].toPrettyString()
                } catch (_: NullPointerException) {
                    ""
                }

                return Comment(
                    created = commentJsonNode["data"]["created"].asDouble(),
                    ups = commentJsonNode["data"]["ups"].asLong(),
                    downs = commentJsonNode["data"]["downs"].asLong(),
                    body = body,
                    author = author,
                    replyTo = parentId,
                    replies = child,
                    depth = depth,
                    id = curId
                )
            }
            val comments: MutableList<Comment> = arrayListOf()
            for (comment in tree[1]["data"]["children"]) {
                comments.add(deserializeComment(comment, null, 0))
            }
            return CommentsSnapshot(comments)
        }
    }
}
