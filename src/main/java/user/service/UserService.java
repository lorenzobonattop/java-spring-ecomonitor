
package user.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import user.exception.UserNotFoundException;
import user.model.DTO.UserCreateRequest;
import user.model.DTO.UserDTO;
import user.model.DTO.UserPasswordUpdateRequest;
import user.model.DTO.UserUpdateRequest;
import user.model.User;
import user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
    }

    @Transactional
    public UserDTO createUser(UserCreateRequest request) {
        Objects.requireNonNull(request);
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já existe");
        }

        String encoded = passwordEncoder.encode(request.password());
        User user = new User(null, request.name(), request.email(), encoded, request.role());
        userRepository.save(user);

        return toDTO(user);
    }

    @Transactional
    public UserDTO updateUser(Long id, UserUpdateRequest request) {
        Objects.requireNonNull(request);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        if (request.email() != null && !request.email().isEmpty()) {
            Optional<User> existing = userRepository.findByEmail(request.email());
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já existe");
            }
            user.setEmail(request.email());
        }

        if (request.name() != null && !request.name().isEmpty()) {
            user.setName(request.name());
        }

        if (request.role() != null) {
            user.setRole(request.role());
        }

        userRepository.save(user);
        return toDTO(user);
    }

    @Transactional
    public void updatePassword(Long id, UserPasswordUpdateRequest request) {
        Objects.requireNonNull(request);

        if (request.currentPassword() == null || request.currentPassword().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A senha atual é obrigatória");
        }

        if (request.newPassword() == null || request.newPassword().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A nova senha é obrigatória");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
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