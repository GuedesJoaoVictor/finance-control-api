package br.csi.politecnico.financecontrol.controller;

import br.csi.politecnico.financecontrol.dto.ResponseDTO;
import br.csi.politecnico.financecontrol.dto.UserDTO;
import br.csi.politecnico.financecontrol.exception.BadRequestException;
import br.csi.politecnico.financecontrol.exception.NotFoundException;
import br.csi.politecnico.financecontrol.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping("/find-all")
    public ResponseEntity<List<UserDTO>> findAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseDTO<UserDTO>> getCurrentUser() {
        try {
            return ResponseEntity.ok(ResponseDTO.ok(userService.getCurrentUser()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }

    @PatchMapping("/me")
    public ResponseEntity<ResponseDTO<UserDTO>> updateName(@RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(ResponseDTO.ok("Nome atualizado com sucesso!", userService.updateName(body.get("name"))));
        } catch (BadRequestException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }

    @PatchMapping("/me/password")
    public ResponseEntity<ResponseDTO<Void>> updatePassword(@RequestBody Map<String, String> body) {
        try {
            userService.updatePassword(body.get("currentPassword"), body.get("newPassword"));
            return ResponseEntity.ok(ResponseDTO.ok("Senha alterada com sucesso!", null));
        } catch (BadRequestException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }
}
