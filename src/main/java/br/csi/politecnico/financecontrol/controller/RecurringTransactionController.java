package br.csi.politecnico.financecontrol.controller;

import br.csi.politecnico.financecontrol.dto.RecurringTransactionDTO;
import br.csi.politecnico.financecontrol.dto.ResponseDTO;
import br.csi.politecnico.financecontrol.exception.BadRequestException;
import br.csi.politecnico.financecontrol.exception.NotFoundException;
import br.csi.politecnico.financecontrol.service.RecurringTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recurring")
public class RecurringTransactionController {

    private final RecurringTransactionService service;

    public RecurringTransactionController(RecurringTransactionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<RecurringTransactionDTO>>> findAll() {
        try {
            return ResponseEntity.ok(ResponseDTO.ok(service.findAll()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.err(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<RecurringTransactionDTO>> create(@RequestBody RecurringTransactionDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseDTO.ok("Transação recorrente criada com sucesso!", service.create(dto)));
        } catch (BadRequestException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDTO<RecurringTransactionDTO>> update(@PathVariable Long id, @RequestBody RecurringTransactionDTO dto) {
        try {
            return ResponseEntity.ok(ResponseDTO.ok("Atualizada com sucesso!", service.update(id, dto)));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(ResponseDTO.ok("Removida com sucesso!", null));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }

    @PostMapping("/apply")
    public ResponseEntity<ResponseDTO<Integer>> apply(
            @RequestParam int month,
            @RequestParam int year) {
        try {
            int count = service.apply(month, year);
            return ResponseEntity.ok(ResponseDTO.ok(
                    count + " transa\u00e7\u00f5es criadas com sucesso!", count));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }
}
