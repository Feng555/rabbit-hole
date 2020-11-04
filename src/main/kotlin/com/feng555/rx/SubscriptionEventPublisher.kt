package com.feng555.rx

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate

open class SubscriptionEventPublisher(private val rabbitTemplate: RabbitTemplate,
                                      private val exchangeName: String,
                                      private val routingKey: String) {

    open fun publish(eventRequest: EventRequest) {
        val properties = MessageProperties()
        properties.expiration = "5000"
        rabbitTemplate.exchange = exchangeName
        rabbitTemplate.routingKey = routingKey
        rabbitTemplate.send(Message(ObjectMapper().writeValueAsString(eventRequest).toByteArray(), properties))
    }

}
