package com.sigma.university.cloud.sample.delete;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/delete")
public class DeleteApiController {

    @Autowired
    private DeleteIntegrationFlow deleteIntegrationFlow;

    @RequestMapping(path = "/{objectKey}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> putMetadata(@PathVariable("objectKey") String objectKey) {
        deleteIntegrationFlow.sendDeleteCommand(objectKey);
        return ResponseEntity.ok().build();
    }

}
