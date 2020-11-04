package com.feng555.rx

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class EventResponse : EventRequest {

    constructor()

    constructor(correlationId: String?, topic: String?, apiVersion: Int?, userId: String?) {
        this.correlationId = correlationId
        this.topic = topic
        this.apiVersion = apiVersion
        this.userId = userId
    }

    constructor(correlationId: String?, topic: String?, apiVersion: Int?, userId: String?,region: String?) {
        this.correlationId = correlationId
        this.topic = topic
        this.apiVersion = apiVersion
        this.userId = userId
        this.region = region
    }

    var response: ResponseContainer = ResponseContainer()

    class ResponseContainer {
        var result: Result? = null

        var code: String? = null

        var message: String? = null

        var payload: Any? = null
    }

    enum class Result {
        SUCCESS, ERROR
    }

}
