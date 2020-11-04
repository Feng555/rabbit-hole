package com.feng555.rx

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageListener
import java.util.concurrent.Executors

abstract class AbstractEventListener<T : EventRequest> : MessageListener {

    private val logger = LoggerFactory.getLogger(AbstractEventListener::class.java)

    override fun onMessage(message: Message?) {
        try {
            if (message != null) {
                val executors = Executors.newFixedThreadPool(1)
                executors.submit {
                    val request = deserializeContext(message.body)
                    logger.info("Event message received correlationId:${request.correlationId} topic:${request.topic} userId:${request.userId}")
                    setRequestContext(request.correlationId, request.topic, request.apiVersion, request.userId, request.region)
                    onEvent(deserialize(message.body))
                }
                executors.shutdown()
            } else {
                logger.warn("Event message empty")
            }
        } catch (e: Exception) {
            logger.warn("Unable to process event message ${String(message!!.body)}", e)
        }
    }

    abstract fun onEvent(eventRequest: T)

    abstract fun deserialize(messageBody: ByteArray): T

    private fun deserializeContext(messageBody: ByteArray): EventRequest = ObjectMapper().readValue(messageBody, EventRequest::class.java)

    private fun setRequestContext(correlationId: String?, topic: String?, apiVersion: Int?, userId: String?, region: String?) {
        val eventContext = EventContext(correlationId, topic, apiVersion, userId, region)
        EventContextHolder.setEventContext(eventContext)
    }

}
