package org.brain.uploadservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.brain.uploadservice.payload.FolderResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/folders")
public interface FolderController {

    @GetMapping
    @Operation(summary = "Retrieves the root folder")
    FolderResponse getRootFolder(@RequestParam("userId") UUID userId);

    @GetMapping("/{folderId}")
    @Operation(summary = "Retrieves the folder by id")
    FolderResponse getFolderById(@PathVariable("folderId") UUID folderId);

    @PostMapping
    @Operation(summary = "Creates a folder")
    FolderResponse createFolder(@RequestParam("name") String name, @RequestParam("parentFolderId") UUID parentFolderId);

}
