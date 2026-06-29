package br.csi.politecnico.financecontrol.service;

import br.csi.politecnico.financecontrol.dto.UserDTO;
import br.csi.politecnico.financecontrol.exception.BadRequestException;
import br.csi.politecnico.financecontrol.exception.NotFoundException;
import br.csi.politecnico.financecontrol.model.User;
import br.csi.politecnico.financecontrol.repository.UserRepository;
import br.csi.politecnico.financecontrol.utils.AuthUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> findAll() {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            return Collections.emptyList();
        }

        List<UserDTO> dtos = new ArrayList<>();
        users.forEach(user -> dtos.add(new UserDTO(user)));

        return dtos;
    }

    public UserDTO getCurrentUser() {
        String uuid = AuthUtil.getUuid();
        User user = userRepository.findByUuid(UUID.fromString(uuid))
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        return new UserDTO(user);
    }

    public UserDTO updateName(String name) {
        String uuid = AuthUtil.getUuid();
        User user = userRepository.findByUuid(UUID.fromString(uuid))
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Nome não pode ser vazio");
        }

        user.setName(name.trim());
        return new UserDTO(userRepository.saveAndFlush(user));
    }

    public void updatePassword(String currentPassword, String newPassword) {
        String uuid = AuthUtil.getUuid();
        User user = userRepository.findByUuid(UUID.fromString(uuid))
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadRequestException("Senha atual incorreta");
        }

        if (newPassword == null || newPassword.length() < 6) {
            throw new BadRequestException("Nova senha deve ter no mínimo 6 caracteres");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.saveAndFlush(user);
    }
}
