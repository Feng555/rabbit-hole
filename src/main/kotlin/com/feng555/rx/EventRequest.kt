package com.feng555.rx

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class EventRequest() {

    var method: Method? = null

    var correlationId: String? = null

    var topic: String? = null

    var apiVersion: Int? = null

    var userId: String? = null

    var region: String? = null

    enum class Method {
        GET, CREATE, UPDATE, DELETE, SUBSCRIBE, UNSUBSCRIBE
    }

    companion object {
        @JvmStatic
        fun initUnsubscribeRequest(topic: String, userId: String): EventRequest {
            val request = EventRequest()
            request.topic = topic
            request.userId = userId
            request.method = Method.UNSUBSCRIBE
            return request
        }
    }

}
