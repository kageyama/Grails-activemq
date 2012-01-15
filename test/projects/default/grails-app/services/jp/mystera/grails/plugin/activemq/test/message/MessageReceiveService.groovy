package jp.mystera.grails.plugin.activemq.test.message

class MessageReceiveService {
    static def queueName = "TEST_QUEUE"

    def onMessage(message) {
        println "message received !! message => ${message}"
    }
}
