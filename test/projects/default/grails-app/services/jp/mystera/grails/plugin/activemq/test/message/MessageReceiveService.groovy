package jp.mystera.grails.plugin.activemq.test.message

class MessageReceiveService {
    static def queueName = "TEST_QUEUE"

    def handleMessage(message) {
        log.info "message(Object) received !! message => ${message}"
    }
}
