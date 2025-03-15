package org.brain.uploadservice.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NestResponse {

    private Long userId;
    private FolderResponse folderResponse;
}
