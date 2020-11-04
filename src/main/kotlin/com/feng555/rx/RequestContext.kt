package com.feng555.rx

import com.feng555.rx.HTTP_REQUEST_CONTEXT_KEY
import com.feng555.rx.HttpRequestContext
import com.feng555.rx.USER_REQUEST_CONTEXT_KEY
import com.feng555.rx.UserRequestContext
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder

object RequestContext {

    @JvmStatic
    fun setUserRequestContext(userRequestContext: UserRequestContext) {
        val reqAttributes = RequestContextHolder.getRequestAttributes()
        if (reqAttributes != null) {
            reqAttributes.setAttribute(USER_REQUEST_CONTEXT_KEY, userRequestContext, RequestAttributes.SCOPE_REQUEST)
            RequestContextHolder.setRequestAttributes(reqAttributes, true)
        }
    }

    @JvmStatic
    fun setHttpRequestContext(httpRequestContext: HttpRequestContext) {
        val reqAttributes = RequestContextHolder.getRequestAttributes()
        if (reqAttributes != null) {
            reqAttributes.setAttribute(HTTP_REQUEST_CONTEXT_KEY, httpRequestContext, RequestAttributes.SCOPE_REQUEST)
            RequestContextHolder.setRequestAttributes(reqAttributes, true)
        }
    }

    @JvmStatic
    fun getUserRequestContext(): UserRequestContext? {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return null
        }
        val attribute = RequestContextHolder.getRequestAttributes()!!.getAttribute(USER_REQUEST_CONTEXT_KEY, RequestAttributes.SCOPE_REQUEST)
        return when (attribute) {
            null -> null
            else -> attribute as UserRequestContext
        }
    }

    @JvmStatic
    fun getHttpRequestContext(): HttpRequestContext? {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return null
        }
        val attribute = RequestContextHolder.getRequestAttributes()!!.getAttribute(HTTP_REQUEST_CONTEXT_KEY, RequestAttributes.SCOPE_REQUEST)
        return if (attribute != null) attribute as HttpRequestContext else return null
    }

}
