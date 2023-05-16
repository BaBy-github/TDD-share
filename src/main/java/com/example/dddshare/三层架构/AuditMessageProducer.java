package com.example.dddshare.三层架构;

import com.example.dddshare.mock.KafkaTemplate;

import static com.example.dddshare.mock.KafkaTemplate.TOPIC_AUDIT_LOG;

public class AuditMessageProducer {
    private KafkaTemplate kafkaTemplate;

    public boolean send(AuditMessage auditMessage) {
        String message = auditMessage.getUserId() + "," + auditMessage.getStoreAccountId() + "," + auditMessage.getAmount();
        kafkaTemplate.send(TOPIC_AUDIT_LOG, message);
        return true;
    }
}
