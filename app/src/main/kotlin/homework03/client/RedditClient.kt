package homework03.client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import homework03.client.RedditApi.getCommentsJSONURL
import homework03.client.RedditApi.getTopicJSONAboutURL
import homework03.client.RedditApi.getTopicJSONURL
import homework03.json.comment.CommentsSnapshot
import homework03.json.topic.PostChildData
import homework03.json.topic.TopicInfoData
import homework03.json.topic.TopicSnapshot
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*

object  RedditClient {
    private val httpClient = HttpClient(CIO)
    private val objectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    suspend fun getTopic(name: String): TopicSnapshot {
        val jsonAbout: String = httpClient.get(getTopicJSONAboutURL(name)).body()
        val jsonPosts: String = httpClient.get(getTopicJSONURL(name)).body()
        val topicData = objectMapper.readValue(jsonAbout, TopicInfoData::class.java)
        val topicPosts = objectMapper.readValue(jsonPosts, PostChildData::class.java)
        return TopicSnapshot(
            creationTime = topicData.data.created,
            online = topicData.data.activeUserCount,
            description = topicData.data.publicDescription,
            posts = topicPosts.data.children.map { it.data }
        )
    }

    suspend fun getComments(permalink: String): CommentsSnapshot {
        val json: String = httpClient.get(getCommentsJSONURL(permalink)).body()
        return CommentsSnapshot.deserialize(objectMapper, json)
    }
}
