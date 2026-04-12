package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.UserCreateDto;
import org.example.dto.UserDTO;
import org.example.dto.UserUpdateDto;
import org.example.service.UserService;
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

/**
 * REST controller for managing user-related operations.
 * Provides endpoints for creating, retrieving, updating, and deleting users.
 */
@RestController
@RequestMapping("/api/users")
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
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() {
        return ResponseEntity.ok(userService.getAllUser());
    }

    /**
     * Creates a new user based on the provided data.
     *
     * @param dto the data transfer object containing new user details.
     * @return ResponseEntity containing the created {@link UserDTO} and HTTP 201 (Created).
     */
    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto));
    }

    /**
     * Updates an existing user's information.
     *
     * @param id the unique identifier of the user to update.
     * @param dto the data transfer object containing updated information.
     * @return ResponseEntity containing the updated {@link UserDTO} and HTTP 200 (OK).
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable("id") Long id, @Valid @RequestBody UserUpdateDto dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    /**
     * Retrieves a single user by their unique ID.
     *
     * @param id the unique identifier of the user.
     * @return ResponseEntity containing the found {@link UserDTO} and HTTP 200 (OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Deletes a user from the system.
     *
     * @param id the unique identifier of the user to remove.
     * @return ResponseEntity with HTTP 204 (No Content) upon successful deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}