package org.brain.uploadservice.controller.impl;

import lombok.AllArgsConstructor;
import org.brain.uploadservice.controller.NestController;
import org.brain.uploadservice.payload.NestResponse;
import org.brain.uploadservice.service.NestService;

@AllArgsConstructor
public class NestControllerImpl implements NestController {

    private final NestService folderRepository;


    @Override
    public NestResponse getNest(Long userId) {
        return folderRepository.getNest(userId);
    }

    @Override
    public NestResponse createNest(Long userId) {
        return folderRepository.createNest(userId);
    }
}
