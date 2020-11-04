package com.feng555.rx

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SubscriptionContextHolder {

    private val logger = LoggerFactory.getLogger(SubscriptionContextHolder::class.java)

    private val subscriptionProcesses: MutableMap<String, MutableMap<String, Thread>> = HashMap()

    fun isSubscribed(topic: String): Boolean {
        val userId = EventContextHolder.getEventContext()!!.userId
        return subscriptionProcesses[topic]?.get(userId!!) != null
    }

    fun addSubscription(topic: String, process: Thread) {
        if (!subscriptionProcesses.containsKey(topic)) {
            subscriptionProcesses[topic] = HashMap()
        }
        val userId = EventContextHolder.getEventContext()!!.userId
        subscriptionProcesses[topic]!![userId!!] = process
        logger.info("Subscribed $topic for $userId")
    }

    fun removeSucscription(topic: String, userId: String) {
        val process = subscriptionProcesses[topic]?.get(userId)
        if (process != null) {
            process!!.interrupt()
            logger.info("Unsubscribed $topic for $userId")
            subscriptionProcesses[topic]!!.remove(userId)
            if (subscriptionProcesses[topic]!!.isEmpty()) {
                subscriptionProcesses.remove(topic)
            }
        }
    }

}
