package com.example.dddshare.DDD四层架构.infrastructure.audit;

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
