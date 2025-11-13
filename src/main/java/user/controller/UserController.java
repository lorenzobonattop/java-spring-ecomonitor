package user.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import user.model.DTO.UserCreateRequest;
import user.model.DTO.UserDTO;
import user.model.DTO.UserPasswordUpdateRequest;
import user.model.DTO.UserUpdateRequest;
import user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.findById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateRequest req) {
        UserDTO create = userService.createUser(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(create);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest req) {
        UserDTO update = userService.updateUser(id, req);
        return ResponseEntity.ok(update);
    }

    @PostMapping("{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @Valid @RequestBody UserPasswordUpdateRequest req){
        userService.updatePassword(id, req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        UserDTO delete = userService.findById(id);
        userService.deleteUser(id);
    }
}
