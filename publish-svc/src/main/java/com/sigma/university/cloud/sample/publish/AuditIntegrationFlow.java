package com.sigma.university.cloud.sample.publish;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@EnableScheduling
public class AuditIntegrationFlow {

    @Autowired
    private AmazonSQS amazonSQS;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ObjectStorageService objectStorageService;

    @Autowired
    private IndexingService indexingService;

    @Value("${storage.uploadUrlExpireTimeout}")
    private long urlExpireTimeout;

    @Value("${storage.auditEventQueue}")
    private String auditEventQueue;

    private String auditEventQueueUrl;

    @PostConstruct
    private void initialize() {
        try {
            auditEventQueueUrl = amazonSQS.getQueueUrl(auditEventQueue).getQueueUrl();
        } catch (QueueDoesNotExistException e) {
            throw new RuntimeException("SQS queue " + auditEventQueue + " does not exist");
        }
    }

    public void sendAuditEvent(String objectKey) {
        try {
            amazonSQS.sendMessage(
                    auditEventQueueUrl,
                    objectMapper.writeValueAsString(
                            new AuditEvent()
                                    .setObjectKey(objectKey)
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Unable to send audit event");
        }
    }

    @Scheduled(fixedDelayString = "${storage.auditPollTimeout}")
    public void pollAuditEventQueue() {
        amazonSQS.receiveMessage(auditEventQueueUrl).getMessages().forEach(message -> {
            try {
                AuditEvent event = objectMapper.readValue(message.getBody(), AuditEvent.class);
                handleAudit(event);
                amazonSQS.deleteMessage(auditEventQueueUrl, message.getReceiptHandle());
            } catch (Exception e) {
                throw new RuntimeException("Cannot handle message", e);
            }
        });
    }

    @PreDestroy
    public void close() {
        amazonSQS.shutdown();
    }

    public void handleAudit(AuditEvent auditEvent) {
        String objectKey = auditEvent.getObjectKey();

        boolean objectExists = objectStorageService.objectExists(objectKey);
        boolean metadataExists = indexingService.isDocumentIndexed(objectKey);

        if (objectExists && metadataExists) {
            return;
        }

        if (objectExists) {
            objectStorageService.deleteObject(objectKey);
        }

        if (metadataExists) {
            indexingService.delete(objectKey);
        }
    }

    public static class AuditEvent {
        private String objectKey;

        public String getObjectKey() {
            return objectKey;
        }

        public AuditEvent setObjectKey(String objectKey) {
            this.objectKey = objectKey;
            return this;
        }
    }

}
