package org.brain.uploadservice.controller.impl;

import lombok.AllArgsConstructor;
import org.brain.uploadservice.controller.NestController;
import org.brain.uploadservice.payload.NestResponse;
import org.brain.uploadservice.service.NestService;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@AllArgsConstructor
@RestController
public class NestControllerImpl implements NestController {

    private final NestService folderRepository;


    @Override
    public NestResponse getNest(UUID userId) {
        return folderRepository.getNest(userId);
    }

    @Override
    public NestResponse createNest(UUID userId) {
        return folderRepository.createNest(userId);
    }
}
