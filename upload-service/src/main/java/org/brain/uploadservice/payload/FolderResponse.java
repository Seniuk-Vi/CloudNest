package org.brain.uploadservice.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.brain.uploadservice.model.FileObject;


import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class FolderResponse {

    private Long folderId;

    private String name;

    private LocalDateTime createdAt;

    private List<ObjectResponse> childFolders;

    private List<FileObject> fileObjects;
}
