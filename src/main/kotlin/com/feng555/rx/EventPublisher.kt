package com.feng555.rx

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate

open class EventPublisher(private val rabbitTemplate: RabbitTemplate,
                          private val exchangeName: String,
                          private val routingKey: String) {

    open fun publishRaw(message: Any) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, ObjectMapper().writeValueAsString(message))
    }

    open fun publishRaw(expiration: Long, message: Any) {
        val properties = MessageProperties()
        properties.expiration = expiration.toString()
        rabbitTemplate.exchange = exchangeName
        rabbitTemplate.routingKey = routingKey
        rabbitTemplate.send(Message(ObjectMapper().writeValueAsString(message).toByteArray(), properties))
    }

    open fun publishSuccess() {
        val eventResponse = initEventResponse()
        eventResponse.response.result = EventResponse.Result.SUCCESS
        rabbitTemplate.convertAndSend(exchangeName, routingKey, ObjectMapper().writeValueAsString(eventResponse))
    }

    open fun publishSuccess(expiration: Long) {
        val eventResponse = initEventResponse()
        eventResponse.response.result = EventResponse.Result.SUCCESS
        publishWithExpiration(eventResponse, expiration)
    }

    open fun publishSuccess(message: String) {
        val eventResponse = initEventResponse()
        eventResponse.response.result = EventResponse.Result.SUCCESS
        eventResponse.response.message = message
        rabbitTemplate.convertAndSend(exchangeName, routingKey, ObjectMapper().writeValueAsString(eventResponse))
    }

    open fun publishSuccess(expiration: Long, message: String?) {
        val eventResponse = initEventResponse()
        eventResponse.response.result = EventResponse.Result.SUCCESS
        eventResponse.response.message = message
        publishWithExpiration(eventResponse, expiration)
    }

    open fun publishSuccess(message: String?, response: Any?) {
        val eventResponse = initEventResponse()
        eventResponse.response.result = EventResponse.Result.SUCCESS
        eventResponse.response.message = message
        eventResponse.response.payload = response
        rabbitTemplate.convertAndSend(exchangeName, routingKey, ObjectMapper().writeValueAsString(eventResponse))
    }

    open fun publishSuccess(message: String?,topic: String?, response: Any?) {
        val eventResponse = initEventResponse()
        eventResponse.response.result = EventResponse.Result.SUCCESS
        eventResponse.response.message = message
        eventResponse.response.payload = response
        eventResponse.topic=topic
        rabbitTemplate.convertAndSend(exchangeName, routingKey, ObjectMapper().writeValueAsString(eventResponse))
    }

    open fun publishSuccess(expiration: Long, message: String?, response: Any?) {
        val eventResponse = initEventResponse()
        eventResponse.response.result = EventResponse.Result.SUCCESS
        eventResponse.response.message = message
        eventResponse.response.payload = response
        publishWithExpiration(eventResponse, expiration)
    }

    open fun publishError(errorCode: String, message: String) {
        val eventResponse = initEventResponse()
        eventResponse.response.result = EventResponse.Result.ERROR
        eventResponse.response.code = errorCode
        eventResponse.response.message = message
        rabbitTemplate.convertAndSend(exchangeName, routingKey, ObjectMapper().writeValueAsString(eventResponse))
    }

    open fun publishError(expiration: Long, errorCode: String, message: String) {
        val eventResponse = initEventResponse()
        eventResponse.response.result = EventResponse.Result.ERROR
        eventResponse.response.code = errorCode
        eventResponse.response.message = message
        publishWithExpiration(eventResponse, expiration)
    }

    open fun publishError(errorCode: String, message: String, response: Any) {
        val eventResponse = initEventResponse()
        eventResponse.response.result = EventResponse.Result.ERROR
        eventResponse.response.code = errorCode
        eventResponse.response.message = message
        eventResponse.response.payload = response
        rabbitTemplate.convertAndSend(exchangeName, routingKey, ObjectMapper().writeValueAsString(eventResponse))
    }

    open fun publishError(expiration: Long, errorCode: String, message: String, response: Any) {
        val eventResponse = initEventResponse()
        eventResponse.response.result = EventResponse.Result.ERROR
        eventResponse.response.code = errorCode
        eventResponse.response.message = message
        eventResponse.response.payload = response
        publishWithExpiration(eventResponse, expiration)
    }

    open fun publishBroadcast(topic: String?, response: Any?) {
        val eventResponse = EventResponse()
        eventResponse.response.result = EventResponse.Result.SUCCESS
        eventResponse.topic = topic
        eventResponse.response.payload = response
        rabbitTemplate.convertAndSend(exchangeName, routingKey, ObjectMapper().writeValueAsString(eventResponse))
    }

    open fun <T : EventRequest> publishRequest(eventRequest: T) {
        eventRequest.correlationId = EventContextHolder.getEventContext()?.correlationId
        eventRequest.topic = EventContextHolder.getEventContext()?.topic
        eventRequest.apiVersion = EventContextHolder.getEventContext()?.apiVersion
        eventRequest.userId = EventContextHolder.getEventContext()?.userId
        eventRequest.region = EventContextHolder.getEventContext()?.region
        rabbitTemplate.convertAndSend(exchangeName, routingKey, ObjectMapper().writeValueAsString(eventRequest))
    }

    open fun <T : EventRequest> publishRequest(expiration: Long, eventRequest: T) {
        eventRequest.correlationId = EventContextHolder.getEventContext()?.correlationId
        eventRequest.topic = EventContextHolder.getEventContext()?.topic
        eventRequest.apiVersion = EventContextHolder.getEventContext()?.apiVersion
        eventRequest.userId = EventContextHolder.getEventContext()?.userId
        eventRequest.region = EventContextHolder.getEventContext()?.region
        publishWithExpiration(eventRequest, expiration)
    }

    private fun initEventResponse(): EventResponse {
        return EventResponse(
                EventContextHolder.getEventContext()?.correlationId,
                EventContextHolder.getEventContext()?.topic,
                EventContextHolder.getEventContext()?.apiVersion,
                EventContextHolder.getEventContext()?.userId,
                EventContextHolder.getEventContext()?.region)
    }

    private fun <T : EventRequest> publishWithExpiration(eventResponse: T, expiration: Long) {
        val properties = MessageProperties()
        properties.expiration = expiration.toString()
        rabbitTemplate.exchange = exchangeName
        rabbitTemplate.routingKey = routingKey
        rabbitTemplate.send(Message(ObjectMapper().writeValueAsString(eventResponse).toByteArray(), properties))
    }

}
