package com.feng555.rx

import org.apache.commons.lang3.StringUtils
import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.Configuration
import org.springframework.core.type.filter.AnnotationTypeFilter

@Configuration
open class RxListenerRegister : BeanFactoryPostProcessor {

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val connectionFactory = beanFactory.getBean(ConnectionFactory::class.java)
        val rabbitTemplate = beanFactory.getBean(RabbitTemplate::class.java)
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter(AnnotationTypeFilter(RxListener::class.java))
        for (rxListenerDefinition in scanner.findCandidateComponents("com.dbs.witp")) {
            val beanClassName = rxListenerDefinition.beanClassName!!
            val beanName = beanClassName.split(".").last()
            val rxListenerClass = beanFactory.beanClassLoader!!.loadClass(beanClassName)
            val exchange = rxListenerClass.getAnnotation(RxListener::class.java).exchange
            val inbound = rxListenerClass.getAnnotation(RxListener::class.java).inbound
            val outbound = rxListenerClass.getAnnotation(RxListener::class.java).outbound
            if (!beanFactory.containsBean(exchange)) {
                val directExchange = exchange(exchange)
                beanFactory.registerSingleton(exchange, directExchange)
            }
            if (!beanFactory.containsBean(inbound)) {
                val queue = queue(inbound)
                beanFactory.registerSingleton(inbound, queue)
            }
            if (!beanFactory.containsBean("$exchange|$inbound")) {
                val directExchange = beanFactory.getBean(exchange) as DirectExchange
                val queue = beanFactory.getBean(inbound) as Queue
                val binding = binding(directExchange, queue)
                beanFactory.registerSingleton("$exchange|$inbound", binding)
            }
            if (StringUtils.isNotEmpty(outbound)) {
                val messagePublisher = messagePublisher(rabbitTemplate, exchange, outbound)
                beanFactory.registerSingleton(beanName.replace("Listener", "Publisher"), messagePublisher)
            }
            val messageListenerContainer = messageListenerContainer(connectionFactory, inbound)
            beanFactory.registerSingleton(beanName, messageListenerContainer)
            RxListenerContainerHolder.rxListenerContainers[beanName.toLowerCase()] = messageListenerContainer
        }
    }

    private fun messageListenerContainer(connectionFactory: ConnectionFactory, queue: String): SimpleMessageListenerContainer {
        val container = SimpleMessageListenerContainer()
        container.setConcurrentConsumers(10)
        container.connectionFactory = connectionFactory
        container.setQueueNames(queue)
        return container
    }

    private fun messagePublisher(rabbitTemplate: RabbitTemplate, exchange: String, queue: String): EventPublisher {
        return EventPublisher(rabbitTemplate, exchange, queue)
    }

    private fun exchange(exchange: String): DirectExchange {
        return DirectExchange(exchange, true, false)
    }

    private fun queue(queue: String): Queue {
        return Queue(queue, true, false, false)
    }

    private fun binding(exchange: DirectExchange, queue: Queue): Binding {
        return BindingBuilder.bind(queue).to(exchange).with(queue.name)
    }

}

@Configuration
open class RxListenerLoader : BeanPostProcessor {

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        if (bean is MessageListener) {
            if (RxListenerContainerHolder.rxListenerContainers.containsKey(beanName.toLowerCase())) {
                RxListenerContainerHolder.rxListenerContainers[beanName.toLowerCase()]!!.setMessageListener(bean)
            }
        }
        return bean
    }

}

object RxListenerContainerHolder {

    @JvmStatic
    var rxListenerContainers = mutableMapOf<String, SimpleMessageListenerContainer>()

}
