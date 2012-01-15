package jp.mystera.grails.plugin.activemq.test.message

class MessageService {

    static transactional = true

    def serviceMethod() {
        sendMessage('TEST_QUEUE', "This is test message. ${new Date()}")
    }
}
