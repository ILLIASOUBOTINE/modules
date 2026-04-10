package org.example.dao;


import org.example.entity.UserEntity;
import org.example.exception.DatabaseException;
import org.example.exception.DuplicateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class UserDaoImplTest extends AbstractDaoTest {

    private UserDaoImpl userDao;

    @BeforeEach
    void init() throws Exception {
        userDao = new UserDaoImpl();

        java.lang.reflect.Field field = CrudDaoImpl.class.getDeclaredField("sessionFactory");
        field.setAccessible(true);
        field.set(userDao, sessionFactory);

        try (org.hibernate.Session session = sessionFactory.openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            session.createQuery("DELETE FROM UserEntity").executeUpdate();
            tx.commit();
        }
    }

    @Test
    void save_ShouldAssignId() {
        // Given
        UserEntity user = new UserEntity("User", "user@test.com", 30);

        // When
        userDao.save(user);

        // Then
        assertNotNull(user.getId());
    }

    @Test
    void save_WhenDuplicate() {
        // Given
        UserEntity user1 = new UserEntity("User1", "user@test.com", 30);
        UserEntity user2 = new UserEntity("User2", "user@test.com", 40);
        userDao.save(user1);

        // When & Then
        DuplicateException ex = assertThrows(DuplicateException.class, () -> {
            userDao.save(user2);
        });

        assertEquals("Unique constraint violation during save", ex.getMessage());
    }

    @Test
    void findByEmail_WhenEmailExists() {
        // Given
        String email = "user@test.com";
        userDao.save(new UserEntity("User", email, 30));

        // When
        UserEntity found = userDao.findByEmail(email);

        // Then
        assertNotNull(found);
        assertEquals(email, found.getEmail());
    }

    @Test
    void findByEmail_WhenEmailNotExist() {
        // When
        UserEntity found = userDao.findByEmail("user@test.com");

        // Then
        assertNull(found);
    }

    @Test
    void saveAndFindByEmail_WhenWorkCorrectly() {
        // Given
        UserEntity user = new UserEntity("User", "user@test.com", 30);

        // When
        userDao.save(user);
        UserEntity found = userDao.findByEmail("user@test.com");

        // Then
        assertNotNull(found);
        assertNotNull(found.getId());
        assertEquals("User", found.getName());
        assertEquals("user@test.com", found.getEmail());
    }

    @Test
    void getAll_ListNotEmpty() {
        // Given
        userDao.save(new UserEntity("User1", "u1@test.com", 20));
        userDao.save(new UserEntity("User2", "u2@test.com", 22));

        // When
        List<UserEntity> users = userDao.getAll();

        // Then
        assertEquals(2, users.size());
    }

    @Test
    void getAll_ListIsEmpty() {
        // Given

        // When
        List<UserEntity> users = userDao.getAll();

        // Then
        assertEquals(0, users.size());
    }

    @Test
    void getById_IdExist() {
        // Given
        UserEntity user = new UserEntity("User", "u@test.com", 20);
        userDao.save(user);
        Long id = user.getId();

        // When
        UserEntity userInBD = userDao.getById(id);

        // Then
        assertNotNull(userInBD);
        assertEquals(userInBD.getId(),user.getId());
        assertEquals(userInBD.getName(),user.getName());
        assertEquals(userInBD.getEmail(),user.getEmail());
        assertEquals(userInBD.getAge(),user.getAge());
    }


    @Test
    void getById_IdNotExist() {
        // Given
        UserEntity user = new UserEntity("User", "u@test.com", 20);
        userDao.save(user);
        Long id = user.getId();
        Long idNotExist = id + 10L;

        // When
        UserEntity userInBD = userDao.getById(idNotExist);

        // Then
        assertNull(userInBD);
    }

    @Test
    void delete_UserExist() {
        // Given
        UserEntity user = new UserEntity("User", "user@test.com", 20);
        userDao.save(user);
        Long id = user.getId();

        // When
        userDao.delete(id);

        // Then
        assertNull(userDao.getById(id));
    }

    @Test
    void delete_UserNotExist() {
        // Given
        Long id = 999L;

        // When & Then
        DatabaseException ex = assertThrows(DatabaseException.class, () -> {
            userDao.delete(id);
        });

        assertTrue(ex.getMessage().contains(id.toString()));
    }

    @Test
    void update_WhenWorkCorrectly() {
        // Given
        UserEntity user = new UserEntity("User", "user@test.com", 25);
        userDao.save(user);
        Long id = user.getId();
        user.setName("NewUser");

        // When
        userDao.update(user);

        // Then
        UserEntity updated = userDao.getById(id);
        assertEquals("NewUser", updated.getName());
    }

    @Test
    void update_WhenDuplicateEmail() {
        // Given
        UserEntity user = new UserEntity("User", "user@test.com", 25);
        UserEntity otherUser = new UserEntity("OtherUser", "userother@test.com", 25);
        userDao.save(user);
        userDao.save(otherUser);
        Long id = user.getId();
        user.setEmail("userother@test.com");

        // When & Then
        DuplicateException ex = assertThrows(DuplicateException.class, () -> {
            userDao.update(user);
        });

        assertEquals("Unique constraint violation during update", ex.getMessage());
    }
}