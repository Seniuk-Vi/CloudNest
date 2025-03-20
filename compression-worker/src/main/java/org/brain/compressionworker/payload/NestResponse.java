package org.brain.compressionworker.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class NestResponse {

    private UUID userId;
    private FolderResponse folderResponse;
}
