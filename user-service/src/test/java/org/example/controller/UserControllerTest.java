package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UserCreateDto;
import org.example.dto.UserDTO;
import org.example.dto.UserUpdateDto;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_ShouldReturnList() throws Exception {
        UserDTO user = new UserDTO(1L, "user", "user@mail.com", 25);
        Mockito.when(userService.getAllUser()).thenReturn(List.of(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("user"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("user@mail.com"));
    }

    @Test
    void create_ShouldReturnCreated() throws Exception {
        UserCreateDto createDto = new UserCreateDto("user", "user@mail.com", 25);
        UserDTO responseDto = new UserDTO(1L, "user", "user@mail.com", 25);

        Mockito.when(userService.createUser(any(UserCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("user"));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenInvalidEmail() throws Exception {
        UserCreateDto invalidDto = new UserCreateDto("user", "invalid-email", 25);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getById_ShouldReturnUser() throws Exception {
        UserDTO user = new UserDTO(1L, "user", "user@mail.com", 25);
        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("user"));
    }

    @Test
    void update_ShouldReturnUpdatedUser() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto("user updated", "updated@mail.com", 26);
        UserDTO responseDto = new UserDTO(1L, "user updated", "updated@mail.com", 26);

        Mockito.when(userService.updateUser(eq(1L), any(UserUpdateDto.class))).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("user updated"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("updated@mail.com"));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(userService, Mockito.times(1)).deleteUser(1L);
    }
}