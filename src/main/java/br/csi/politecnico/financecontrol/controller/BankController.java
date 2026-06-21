package br.csi.politecnico.financecontrol.controller;

import br.csi.politecnico.financecontrol.dto.BankDTO;
import br.csi.politecnico.financecontrol.dto.ResponseDTO;
import br.csi.politecnico.financecontrol.dto.UserBankDTO;
import br.csi.politecnico.financecontrol.exception.BadRequestException;
import br.csi.politecnico.financecontrol.exception.NotFoundException;
import br.csi.politecnico.financecontrol.service.BankService;
import br.csi.politecnico.financecontrol.utils.AuthUtil;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/bank")
@RestController
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping
    public ResponseEntity<ResponseDTO<BankDTO>> createBank(@RequestBody @Valid BankDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.ok(bankService.create(dto)));
        } catch (BadRequestException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.err(ex.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping("/find-all")
    public ResponseEntity<List<BankDTO>> findAllBanks() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(bankService.findAll());
        } catch (NotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<ResponseDTO<BankDTO>> findBankById(@PathVariable("id") @Valid Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.ok(bankService.findById(id)));
        } catch (NotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }

    @Secured({"ROLE_ADMIN"})
    @PatchMapping("/update-by-id/{id}")
    public ResponseEntity<ResponseDTO<BankDTO>> updateBank(@RequestBody @Valid BankDTO dto, @PathVariable("id") @Valid Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.ok("Atualizado com sucesso!", bankService.update(id, dto)));
        } catch (NotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<String> deleteBankById(@PathVariable("id") @Valid Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(bankService.deleteById(id));
        } catch (NotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/vinculate/by/user/{uuid}")
    public ResponseEntity<ResponseDTO<UserBankDTO>> vinculateUserBank(@PathVariable @Valid String uuid, @RequestBody @Valid UserBankDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.ok("Vinculado com sucesso!", bankService.vinculateUserBank(uuid, dto)));
        } catch (BadRequestException | NotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }

    @DeleteMapping("/delete/user-bank/by/{id}")
    public ResponseEntity<ResponseDTO<Boolean>> deleteUserBankById(@PathVariable @Valid Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.ok("Deletado com sucesso!", bankService.deleteUserBankById(id)));
        } catch (NotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }

    @GetMapping("/find-all/links/by/user/{uuid}")
    public ResponseEntity<List<BankDTO>> findByAllUserBanksByUuid(@PathVariable() @Valid String uuid) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(bankService.findUserBankByUuid(uuid));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/find-all/links/by-user")
    public ResponseEntity<List<BankDTO>> findByCurrentUserBanks() {
        try {
            String uuid = AuthUtil.getUuid();
            return ResponseEntity.status(HttpStatus.OK).body(bankService.findUserBankByUuid(uuid));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping("/find-all/links")
    public ResponseEntity<List<UserBankDTO>> findAllLinks() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(bankService.findAllLinks());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Secured({"ROLE_USER"})
    @GetMapping("/find-all/by/{uuid}")
    public ResponseEntity<List<BankDTO>> findAllBanksByUserUuid(@PathVariable @Valid String uuid) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(bankService.findAllBanksByUserUuid(uuid));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
}
