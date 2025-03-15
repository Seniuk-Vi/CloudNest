package org.brain.uploadservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.brain.uploadservice.payload.FolderResponse;
import org.brain.uploadservice.payload.ObjectResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/objects")
public interface ObjectController {

    @GetMapping("/{objectId}/metadata")
    @Operation(summary = "Retrieves the object metadata by id")
    ObjectResponse getObjectMetadataById(@PathVariable("objectId") Long objectId);

    @GetMapping("/{objectId}")
    @Operation(summary = "Retrieves the object by id")
    Resource getObjectById(@PathVariable("objectId") Long objectId);


    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Creates an object")
    ObjectResponse createObject(@RequestParam("folderId") Long folderId,
                                @RequestParam("file") MultipartFile file);

}
