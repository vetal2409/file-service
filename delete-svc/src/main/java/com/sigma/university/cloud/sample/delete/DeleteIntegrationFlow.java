package com.sigma.university.cloud.sample.delete;

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
public class DeleteIntegrationFlow {

    @Autowired
    private AmazonSQS amazonSQS;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ObjectStorageService objectStorageService;

    @Autowired
    private IndexService indexService;

    @Value("${storage.deleteCommandQueue}")
    private String deleteCommandQueue;

    private String deleteCommandQueueUrl;

    @PostConstruct
    private void initialize() {
        try {
            deleteCommandQueueUrl = amazonSQS.getQueueUrl(deleteCommandQueue).getQueueUrl();
        } catch (QueueDoesNotExistException e) {
            throw new RuntimeException("SQS queue " + deleteCommandQueue + " does not exist");
        }
    }

    public void sendDeleteCommand(String objectKey) {
        try {
            amazonSQS.sendMessage(
                    deleteCommandQueueUrl,
                    objectMapper.writeValueAsString(
                            new DeleteCommand()
                                    .setObjectKey(objectKey)
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Unable to send delete command");
        }
    }

    @Scheduled(fixedDelayString = "${storage.deletePollTimeout}")
    public void pollDeleteCommandQueue() {
        amazonSQS.receiveMessage(deleteCommandQueueUrl).getMessages().forEach(message -> {
            try {
                DeleteCommand command = objectMapper.readValue(message.getBody(), DeleteCommand.class);
                handleDelete(command);
                amazonSQS.deleteMessage(deleteCommandQueueUrl, message.getReceiptHandle());
            } catch (Exception e) {
                throw new RuntimeException("Cannot handle message", e);
            }
        });
    }

    @PreDestroy
    public void close() {
        amazonSQS.shutdown();
    }

    public void handleDelete(DeleteCommand deleteCommand) {
        String objectKey = deleteCommand.getObjectKey();

        objectStorageService.deleteObject(objectKey);
        indexService.delete(objectKey);
    }

    public static class DeleteCommand {
        private String objectKey;

        public String getObjectKey() {
            return objectKey;
        }

        public DeleteCommand setObjectKey(String objectKey) {
            this.objectKey = objectKey;
            return this;
        }
    }

}
