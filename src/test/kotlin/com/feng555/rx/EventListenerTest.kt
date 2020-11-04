package com.feng555.rx

import org.junit.Ignore
import org.junit.Test
import org.mockito.Mockito
import org.slf4j.Logger
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.test.util.ReflectionTestUtils
import kotlin.test.assertEquals

class EventListenerTest {

    @Ignore
    @Test
    fun itShouldConvertMessageToEvent() {
        val message = Message("{\"method\":\"GET\",\"correlationId\":\"1234567890abcdef\",\"apiVersion\":1,\"userId\":\"itsupp01\"}".toByteArray(), MessageProperties())
        val listener = Mockito.mock(AbstractEventListener::class.java, Mockito.CALLS_REAL_METHODS)
        ReflectionTestUtils.setField(listener, "logger", Mockito.mock(Logger::class.java), Logger::class.java)
        listener.onMessage(message)
        assertEquals("1234567890abcdef", EventContextHolder.getEventContext()?.correlationId)
        assertEquals(1, EventContextHolder.getEventContext()?.apiVersion)
        assertEquals("itsupp01", EventContextHolder.getEventContext()?.userId)
    }

}
