package org.example.service;

import org.example.dto.OperationType;
import org.example.dto.UserCreateDto;
import org.example.dto.UserDTO;
import org.example.dto.UserUpdateDto;
import org.example.entity.UserEntity;
import org.example.kafka.producer.UserProducer;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link UserService} providing business logic,
 * including validation for unique emails and existing IDs.
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserProducer userProducer;

    public UserServiceImpl(UserRepository userRepository, UserProducer userProducer) {
        this.userRepository = userRepository;
        this.userProducer = userProducer;
    }

    /**
     * {@inheritDoc}
     * @throws RuntimeException if the email address is already registered.
     */
    @Override
    @Transactional
    public UserDTO createUser(UserCreateDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        UserEntity user = new UserEntity();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setAge(dto.age());

        UserEntity saved = userRepository.save(user);
        userProducer.sendEvent(dto.email(), OperationType.CREATE);
        return mapToDto(saved);
    }

    /**
     * {@inheritDoc}
     * @throws RuntimeException if user is not found or if the new email belongs to another user.
     */
    @Override
    public UserDTO updateUser(Long id, UserUpdateDto dto) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь с ID " + id + " не найден"));

        if (!user.getEmail().equals(dto.email()) && userRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Email " + dto.email() + " уже занят другим пользователем");
        }

        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setAge(dto.age());

        return mapToDto(userRepository.save(user));
    }

    @Override
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Пользователь с ID " + id + " не найден"));
    }

    @Override
    public List<UserDTO> getAllUser() {
        return userRepository.findAll()
                .stream().map(this::mapToDto)
                .toList();
    }

    /**
     * {@inheritDoc}
     * @throws RuntimeException if user does not exist.
     */
    @Override
    @Transactional
    public void deleteUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Невозможно удалить: Пользователь с ID " + id + " не найден"));

        String email = user.getEmail();

        userRepository.deleteById(id);
        userProducer.sendEvent(email, OperationType.DELETE);
    }

    /**
     * Converts a {@link UserEntity} to a {@link UserDTO}.
     *
     * @param user the database entity.
     * @return the mapped data transfer object.
     */
    private UserDTO mapToDto(UserEntity user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge()
        );
    }
}