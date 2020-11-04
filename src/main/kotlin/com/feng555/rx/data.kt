package com.feng555.rx

data class UserRequestContext(val requestId: String,
                              val userId: String,
                              val sessionId: String)

data class HttpRequestContext(var requestId: String)

data class EventContext(var correlationId: String?, var topic: String?, var apiVersion: Int?, var userId: String?, var region: String?)
