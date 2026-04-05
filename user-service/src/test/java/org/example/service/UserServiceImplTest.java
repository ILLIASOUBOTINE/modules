package org.example.service;

import org.example.dao.UserDao;
import org.example.dto.UserUpdateDto;
import org.example.entity.UserEntity;
import org.example.exception.DatabaseException;
import org.example.exception.DuplicateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_WhenEmailIsUnique() {
        // Given
        String email = "newuser@test.com";
        when(userDao.findByEmail(email)).thenReturn(null);

        // When
        userService.createUser("User", email, 34);

        // Then
        verify(userDao, times(1)).save(any(UserEntity.class));
    }

    @Test
    void createUser_WhenEmailExist() {
        // Given
        String email = "existuser@test.com";
        when(userDao.findByEmail(email)).thenReturn(new UserEntity());

        // When & Then
        assertThrows(DuplicateException.class, () ->
                userService.createUser("User", email, 34)
        );

        verify(userDao, never()).save(any());
    }

    @Test
    void getUser_WhenIdNotFounded() {
        // Given
        Long id = 5L;
        when(userDao.getById(id)).thenReturn(null);

        // When & Then
       DatabaseException ex = assertThrows(DatabaseException.class, () ->
                userService.getUser(id)
       );

       assertEquals("User not found with id: " + id, ex.getMessage());
    }

    @Test
    void getUser_WhenIdExist() {
        // Given
        Long id = 5L;
        UserEntity expectedUser = new UserEntity("User", "user@test.com", 25);
        when(userDao.getById(id)).thenReturn(expectedUser);

        // When
        UserEntity user = userService.getUser(id);

        // Then
        assertNotNull(user);
        assertEquals(expectedUser.getName(), user.getName());
        assertEquals(expectedUser.getEmail(), user.getEmail());
        assertEquals(expectedUser.getAge(), user.getAge());
        assertEquals(expectedUser.getCreatedAt(), user.getCreatedAt());
        verify(userDao, times(1)).getById(id);
    }

    @Test
    void getUser_WhenUnexpectedError() {
        // Given
        Long id = 5L;
        String messageException = "Exception";
        when(userDao.getById(id)).thenThrow(new RuntimeException(messageException));

        // When & Then
        DatabaseException ex = assertThrows(DatabaseException.class, () ->
                userService.getUser(id)
        );

        assertEquals("Unexpected error while fetching user", ex.getMessage());
        assertEquals(messageException, ex.getCause().getMessage());
    }

    @Test
    void deleteUser_WhenIdNotFounded() {
        // Given
        Long id = 5L;
        when(userDao.getById(id)).thenReturn(null);

        // When & Then
        DatabaseException ex = assertThrows(DatabaseException.class, () ->
            userService.deleteUser(id)
        );

        assertEquals("User not found with id: " + id, ex.getMessage());

        verify(userDao, never()).delete(id);
    }

    @Test
    void deleteUser_WhenIdExist() {
        // Given
        Long id = 5L;
        when(userDao.getById(id)).thenReturn(new UserEntity());

        // When
        userService.deleteUser(id);

        // Then
        verify(userDao, times(1)).delete(id);
    }

    @Test
    void deleteUser_ThrowDatabaseException() {
        // Given
        Long id = 5L;
        String messageException = "Exception";
        when(userDao.getById(id)).thenThrow(new RuntimeException(messageException));

        // When & Then
        DatabaseException ex = assertThrows(DatabaseException.class, () ->
                userService.deleteUser(id)
        );

        assertEquals("Unexpected error while deleting user", ex.getMessage());
        assertEquals(messageException, ex.getCause().getMessage());
    }

    @Test
    void getAllUsers_Success() {
        // Given
        List<UserEntity> users = List.of(new UserEntity("User", "user@test.com", 25));
        when(userDao.getAll()).thenReturn(users);

        // When
        List<UserEntity> result = userService.getAllUsers();

        // Then
        assertEquals(1, result.size());
        assertEquals("User", result.get(0).getName());
        verify(userDao, times(1)).getAll();
    }

    @Test
    void getAllUsers_ThrowDatabaseException() {
        // Given
        when(userDao.getAll()).thenThrow(new RuntimeException("Exception"));

        // When & Then
        DatabaseException ex = assertThrows(DatabaseException.class, () ->
                userService.getAllUsers()
        );

        assertEquals("Unexpected error while fetching users", ex.getMessage());
    }

    @Test
    void updateUser_WhenUserNotFounded() {
        // Given
        Long id = 1L;
        UserUpdateDto dto = new UserUpdateDto("NewName", null, null);
        when(userDao.getById(id)).thenReturn(null);

        // When & Then
        DatabaseException ex = assertThrows(DatabaseException.class, () ->
                userService.updateUser(id, dto)
        );

        assertTrue(ex.getMessage().contains("User not found with id: " + id));
        verify(userDao, never()).update(any());
    }

    @Test
    void updateUser_WhenEmailAlreadyExists() throws Exception {
        // Given
        Long id = 1L;
        String newEmail = "busy@test.com";
        UserUpdateDto dto = new UserUpdateDto(null, newEmail, null);

        UserEntity existingUser = new UserEntity("Ilya", "old@test.com", 25);
        setId(existingUser, 1L);

        UserEntity differentUser = new UserEntity("Other", newEmail, 30);
        setId(differentUser, 2L);

        when(userDao.getById(id)).thenReturn(existingUser);
        when(userDao.findByEmail(newEmail)).thenReturn(differentUser);

        // When & Then
        assertThrows(DuplicateException.class, () ->
                userService.updateUser(id, dto)
        );

        verify(userDao, never()).update(any());
    }

    @Test
    void updateUser_Success() {
        // Given
        Long id = 1L;
        UserUpdateDto dto = new UserUpdateDto("NewName", "new@test.com", 30);
        UserEntity userInDb = new UserEntity("OldName", "old@test.com", 20);

        when(userDao.getById(id)).thenReturn(userInDb);
        when(userDao.findByEmail("new@test.com")).thenReturn(null); // Email свободен

        // When
        userService.updateUser(id, dto);

        // Then
        assertEquals("NewName", userInDb.getName());
        assertEquals("new@test.com", userInDb.getEmail());
        assertEquals(30, userInDb.getAge());
        verify(userDao, times(1)).update(userInDb);
    }

    // Вспомогательный метод для установки ID
    private void setId(UserEntity entity, Long id) throws Exception {
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }

}