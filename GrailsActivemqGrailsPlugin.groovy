import org.apache.activemq.ActiveMQConnection
import org.apache.activemq.ActiveMQConnectionFactory
import javax.jms.Session
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.listener.DefaultMessageListenerContainer
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.springframework.jms.listener.adapter.MessageListenerAdapter

class GrailsActivemqGrailsPlugin {
    private static final LISTENER_CONTAINER_SUFFIX = 'MessageListenerContainer'

    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.7 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def author = "Your name"
    def authorEmail = ""
    def title = "Plugin summary/headline"
    def description = '''\\
Brief description of the plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/grails-activemq"

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        activeMQConnectionFactory(ActiveMQConnectionFactory) {
            brokerURL = ActiveMQConnection.DEFAULT_BROKER_URL
        }

        jmsTemplate(JmsTemplate) {
            connectionFactory = activeMQConnectionFactory
        }

        application.serviceClasses.each { currentService ->
            if (currentService.hasProperty('queueName')) {
                "${currentService.propertyName}${LISTENER_CONTAINER_SUFFIX}"(DefaultMessageListenerContainer) {
                    connectionFactory = activeMQConnectionFactory
                    destinationName = GrailsClassUtils.getStaticPropertyValue(currentService.clazz, 'queueName')
                    autoStartup = false
                }
            }
        }
    }

    def doWithDynamicMethods = { ctx ->
        application.allClasses.each { currentClass ->
            currentClass.metaClass.sendMessage = {queueName, messageBody ->
                println "send message with jmsTemplate. ${queueName}:${messageBody}"
                ctx.jmsTemplate.convertAndSend queueName, messageBody.toString() //toString()しないと、GStringはSerializableではないのでMQ側で認識できない
            }
        }


    }

    def doWithApplicationContext = { applicationContext ->
        applicationContext.getBeansOfType(DefaultMessageListenerContainer).each { beanName, bean ->
            if (beanName.endsWith(LISTENER_CONTAINER_SUFFIX)) {
                def serviceName = beanName - LISTENER_CONTAINER_SUFFIX
                def adapter = new MessageListenerAdapter(delegate: applicationContext.getBean(serviceName))
                bean.messageListener = adapter
                bean.start()
            }
        }
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
