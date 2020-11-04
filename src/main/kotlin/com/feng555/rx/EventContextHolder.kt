package com.feng555.rx

object EventContextHolder {

    val eventContextThreadLocal = InheritableThreadLocal<EventContext>()

    @JvmStatic
    fun setEventContext(eventContext: EventContext) {
        eventContextThreadLocal.set(eventContext)
    }

    @JvmStatic
    fun getEventContext(): EventContext? {
        return eventContextThreadLocal.get()
    }

}
