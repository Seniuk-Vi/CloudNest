package org.brain.uploadservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.brain.uploadservice.payload.NestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/nest")
public interface NestController {

    @GetMapping
    @Operation(summary = "Retrieves the whole storage hierarchy for a user")
    NestResponse getNest(@RequestParam("userId") Long userId);

    @PostMapping
    @Operation(summary = "Creates a base folder for a user")
    @ResponseStatus(HttpStatus.CREATED)
    NestResponse createNest(@RequestParam("userId") Long userId);
}
