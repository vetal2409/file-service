package com.sigma.university.cloud.sample.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/search")
public class SearchApiController {

    @Autowired
    private ObjectStorageService objectStorageService;

    @Autowired
    private SearchService searchService;

    @RequestMapping(path = "", method = RequestMethod.POST)
    public ResponseEntity<List<ObjectSearchResponse>> searchDocument(
            @RequestBody ObjectSearchRequest objectSearchRequest) {
        return ResponseEntity.ok(searchService.search(objectSearchRequest));
    }

    @RequestMapping(path = "/{objectKey}", method = RequestMethod.GET)
    public ResponseEntity<ObjectDownloadResponse> downloadDocument(
            @PathVariable("objectKey") String objectKey) {
        return ResponseEntity.ok(objectStorageService.generateDownloadUrl(objectKey));
    }

}
