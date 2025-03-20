package org.brain.uploadservice.mapper;

import org.brain.uploadservice.model.FileObject;
import org.brain.uploadservice.payload.ObjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ObjectMapper {


    // Object to ObjectResponse
    @Mapping(target = "uploadToken", ignore = true)
    ObjectResponse toObjectResponse(FileObject object);
}
