import org.apache.activemq.ActiveMQConnection
import org.apache.activemq.ActiveMQConnectionFactory
import javax.jms.Session
import org.springframework.jms.core.JmsTemplate

class GrailsActivemqGrailsPlugin {
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
        // TODO Implement post initialization spring config (optional)
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
