package user.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import user.model.DTO.UserCreateRequest;
import user.model.DTO.UserDTO;
import user.model.User;
import user.repository.UserRepository;
import user.exception.UserNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    @Transactional
    public UserDTO createUser(UserCreateRequest request) {
        Objects.requireNonNull(request);
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já existe");
        }

        User user = new User(null, request.name(), request.email(), request.password(), request.role());
        userRepository.save(user);

        return toDTO(user);
    }

    public UserDTO findById(Long id) {
        return userRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new UserNotFoundException("Usuário com id " + id + " não encontrado"));
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Usuário com id " + userId + " não encontrado");
        }
        userRepository.deleteById(userId);
    }

    private UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}