package com.feng555.rx

import com.feng555.rx.EventPublisher
import com.feng555.rx.EventRequest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import org.springframework.amqp.rabbit.core.RabbitTemplate

@RunWith(MockitoJUnitRunner::class)
class EventPublisherTest {

    @Mock
    lateinit var rabbitTemplate: RabbitTemplate

    val exchange: String = "exchange"

    val routingKey: String = "routingKey"

    @Test
    fun itShouldPublishSuccess() {
        val publisher = EventPublisher(rabbitTemplate, exchange, routingKey)
        publisher.publishSuccess()
        publisher.publishSuccess("message")
        publisher.publishSuccess("message", emptyList<Any>())
        verify(rabbitTemplate, times(3)).convertAndSend(any(), any(), any<String>())
        publisher.publishSuccess(1000)
        publisher.publishSuccess(1000, "message")
        publisher.publishSuccess(1000, "message", emptyList<Any>())
        verify(rabbitTemplate, times(3)).send(any())
    }

    @Test
    fun itShouldPublishError() {
        val publisher = EventPublisher(rabbitTemplate, exchange, routingKey)
        publisher.publishError("errorCode", "message")
        publisher.publishError("errorCode", "message", emptyList<Any>())
        verify(rabbitTemplate, times(2)).convertAndSend(any(), any(), any<String>())
        publisher.publishError(1000, "errorCode", "message")
        publisher.publishError(1000, "errorCode", "message", emptyList<Any>())
        verify(rabbitTemplate, times(2)).send(any())
    }

    @Test
    fun itShouldPublishRequest() {
        val publisher = EventPublisher(rabbitTemplate, exchange, routingKey)
        publisher.publishRequest(EventRequest())
        verify(rabbitTemplate, times(1)).convertAndSend(any(), any(), any<String>())
        publisher.publishRequest(1000, EventRequest())
        verify(rabbitTemplate, times(1)).send(any())
    }

    @Test
    fun itShouldPublishRaw() {
        val publisher = EventPublisher(rabbitTemplate, exchange, routingKey)
        publisher.publishRaw("Test")
        verify(rabbitTemplate, times(1)).convertAndSend(any(), any(), any<String>())
        publisher.publishRaw(1000, "Test")
        verify(rabbitTemplate, times(1)).send(any())
    }

    @Test
    fun itShouldPublishBroadcast() {
        val publisher = EventPublisher(rabbitTemplate, exchange, routingKey)
        publisher.publishBroadcast("Topic", emptyList<Any>())
        verify(rabbitTemplate, times(1)).convertAndSend(any(), any(), any<String>())
    }

}
