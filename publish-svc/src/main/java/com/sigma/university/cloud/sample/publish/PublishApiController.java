package com.sigma.university.cloud.sample.publish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/publish")
public class PublishApiController {

    @Autowired
    private ObjectStorageService objectStorageService;

    @Autowired
    private IndexingService indexingService;

    @Autowired
    private AuditIntegrationFlow auditIntegrationFlow;

    @RequestMapping(path = "", method = RequestMethod.POST)
    public ResponseEntity<UploadUrlResponse> generateUploadLink(
            @RequestBody UploadUrlRequest uploadUrlRequest) {
        UploadUrlResponse response = objectStorageService.generateUploadUrl(uploadUrlRequest);

        auditIntegrationFlow.sendAuditEvent(response.getObjectKey());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/{objectKey}", method = RequestMethod.PUT)
    public ResponseEntity<Void> putMetadata(
            @PathVariable("objectKey") String objectKey,
            @RequestBody PutMetadataRequest putMetadataRequest) {
        indexingService.index(objectKey, putMetadataRequest.getMetadata());

        return ResponseEntity.ok().build();
    }

}
