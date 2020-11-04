package com.feng555.rx

@Target(AnnotationTarget.CLASS)
annotation class RxListener(val exchange: String, val inbound: String, val outbound: String = "")
