package homework03.client

import com.soywiz.korio.file.std.resourcesVfsDebug

object RedditApi {
    private const val reddit = "https://www.reddit.com"
    private const val json = "json"
    fun getTopicJSONURL(topicName : String) = "$reddit/r/$topicName/.$json"

    fun getTopicJSONAboutURL(topicName : String) = "$reddit/r/$topicName/about.$json"

    fun getCommentsJSONURL(permalink: String) = "${reddit}${permalink}.$json"
}
