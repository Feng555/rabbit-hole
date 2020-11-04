package com.feng555.rx

import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

object ThreadHandler {

    private val logger = LoggerFactory.getLogger(ThreadHandler::class.java)

    @JvmStatic
    fun startThread(f: (userId: String) -> Unit) {
        val userRequestContext = RequestContext.getUserRequestContext()
        val httpRequestContext = RequestContext.getHttpRequestContext()
        logger.info("Thread started by user ${userRequestContext?.userId} and request ${httpRequestContext?.requestId}")
        val executors = Executors.newFixedThreadPool(1)
        executors.submit {
            if (userRequestContext != null) {
                RequestContext.setUserRequestContext(userRequestContext)
            }
            if (httpRequestContext != null) {
                RequestContext.setHttpRequestContext(httpRequestContext)
            }
            f(userRequestContext?.userId ?: SYSTEM_USER_ID)
        }
        executors.shutdown()
    }

}
