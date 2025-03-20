package org.brain.compressionworker.mapper;

import org.brain.uploadservice.mapper.ObjectMapper;
import org.brain.uploadservice.model.Folder;
import org.brain.uploadservice.payload.FolderResponse;
import org.brain.uploadservice.payload.NestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {ObjectMapper.class})
public interface FolderMapper {



    // Folder to FolderResponse
    FolderResponse toFolderResponse(Folder folder);

    // Folder to NestResponse
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "folder", target = "folderResponse")
    NestResponse folderToNestResponse(Folder folder);

}
