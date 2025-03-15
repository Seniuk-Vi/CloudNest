package org.brain.uploadservice.mapper;

import org.brain.uploadservice.model.Folder;
import org.brain.uploadservice.payload.FolderResponse;
import org.brain.uploadservice.payload.NestResponse;
import org.brain.uploadservice.payload.ObjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ObjectMapper {


    // Object to ObjectResponse
    ObjectResponse toObjectResponse(Object object);

    // ObjectResponse to Object
    Object toObject(ObjectResponse objectResponse);
}
