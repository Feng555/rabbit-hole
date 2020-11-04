package com.feng555.rx

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractSubscriptionEventListener<T : EventRequest> : AbstractEventListener<T>() {

    private val logger = LoggerFactory.getLogger(AbstractSubscriptionEventListener::class.java)

    @Autowired
    lateinit var subscriptionContextHolder: SubscriptionContextHolder

    @Autowired
    lateinit var subscriptionEventPublisher: SubscriptionEventPublisher

    fun startSubscription(executor: SubscriptionExecutor<T>, eventRequest: T) {
        if (subscriptionContextHolder.isSubscribed(eventRequest.topic!!)) {
            logger.warn("${eventRequest.userId} already subscribed to ${eventRequest.topic}")
            return
        }
        ThreadHandler.startThread {
            subscriptionContextHolder.addSubscription(eventRequest.topic!!, Thread.currentThread())
            executor.execute(eventRequest)
        }
    }

    fun stopSucscription() {
        val eventRequest = EventRequest.initUnsubscribeRequest(EventContextHolder.getEventContext()!!.topic!!, EventContextHolder.getEventContext()!!.userId!!)
        subscriptionEventPublisher.publish(eventRequest)
    }

    interface SubscriptionExecutor<T : EventRequest> {
        fun execute(eventRequest: T)
    }

}
