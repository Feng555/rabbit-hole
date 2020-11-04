package com.feng555.rx

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
open class SubscriptionEventListener : AbstractEventListener<EventRequest>() {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var subscriptionContextHolder: SubscriptionContextHolder

    override fun onEvent(eventRequest: EventRequest) {
        subscriptionContextHolder.removeSucscription(eventRequest.topic!!, eventRequest.userId!!)
    }

    override fun deserialize(messageBody: ByteArray): EventRequest = objectMapper.readValue(messageBody, EventRequest::class.java)

}
