package org.brain.uploadservice.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.brain.uploadservice.model.FileObject;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class FolderResponse {

    private UUID folderId;

    private String name;

    private LocalDateTime createdAt;

    private List<FolderResponse> childFolders;

    private List<ObjectResponse> fileObjects;
}
