package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.dto.UserCreateDto;
import org.example.dto.UserDTO;
import org.example.dto.UserUpdateDto;
import org.example.service.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST controller for managing user-related operations.
 * Provides endpoints for creating, retrieving, updating, and deleting users.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management API", description = "Operations related to user lifecycle")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves a list of all users.
     *
     * @return ResponseEntity containing a list of {@link UserDTO} and HTTP 200 (OK).
     */
    @Operation(summary = "Get all users", description = "Retrieves a list of all users with HATEOAS links")
    @GetMapping
    public CollectionModel<EntityModel<UserDTO>> getAll() {
        List<EntityModel<UserDTO>> users = userService.getAllUser().stream()
                .map(this::toEntityModel)
                .collect(Collectors.toList());

        return CollectionModel.of(users,
                linkTo(methodOn(UserController.class).getAll()).withSelfRel());
    }

    /**
     * Creates a new user based on the provided data.
     *
     * @param dto the data transfer object containing new user details.
     * @return ResponseEntity containing the created {@link UserDTO} and HTTP 201 (Created).
     */
    @Operation(summary = "Create a new user", description = "Saves a new user and returns it with navigation links")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @PostMapping
    public ResponseEntity<EntityModel<UserDTO>> create(@Valid @RequestBody UserCreateDto dto) {
        UserDTO created = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toEntityModel(created));
    }

    /**
     * Updates an existing user's information.
     *
     * @param id the unique identifier of the user to update.
     * @param dto the data transfer object containing updated information.
     * @return ResponseEntity containing the updated {@link UserDTO} and HTTP 200 (OK).
     */
    @Operation(summary = "Update an existing user", description = "Updates user details by ID")
    @PutMapping("/{id}")
    public EntityModel<UserDTO> update(@PathVariable("id") Long id, @Valid @RequestBody UserUpdateDto dto) {
        UserDTO updated = userService.updateUser(id, dto);
        return toEntityModel(updated);
    }

    /**
     * Retrieves a single user by their unique ID.
     *
     * @param id the unique identifier of the user.
     * @return ResponseEntity containing the found {@link UserDTO} and HTTP 200 (OK).
     */
    @Operation(summary = "Get user by ID", description = "Returns a single user identified by their ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{id}")
    public EntityModel<UserDTO> getById(@PathVariable("id") Long id) {
        UserDTO dto = userService.getUserById(id);
        return toEntityModel(dto);
    }

    /**
     * Deletes a user from the system.
     *
     * @param id the unique identifier of the user to remove.
     * @return ResponseEntity with HTTP 204 (No Content) upon successful deletion.
     */
    @Operation(summary = "Delete a user", description = "Removes a user from the system")
    @ApiResponse(responseCode = "204", description = "User deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Converts a Record DTO into an EntityModel and adds HATEOAS links.
     */
    private EntityModel<UserDTO> toEntityModel(UserDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(UserController.class).getById(dto.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll()).withRel("all_users"),
                linkTo(methodOn(UserController.class).update(dto.id(), null)).withRel("update"),
                linkTo(methodOn(UserController.class).delete(dto.id())).withRel("delete")
        );
    }
}