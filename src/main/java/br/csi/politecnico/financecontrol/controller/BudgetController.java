package br.csi.politecnico.financecontrol.controller;

import br.csi.politecnico.financecontrol.dto.BudgetDTO;
import br.csi.politecnico.financecontrol.dto.ResponseDTO;
import br.csi.politecnico.financecontrol.exception.BadRequestException;
import br.csi.politecnico.financecontrol.exception.NotFoundException;
import br.csi.politecnico.financecontrol.service.BudgetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/budget")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<BudgetDTO>>> findAll(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        try {
            return ResponseEntity.ok(ResponseDTO.ok(budgetService.getBudgets(month, year)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.err(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<BudgetDTO>> create(@RequestBody BudgetDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseDTO.ok("Orçamento criado com sucesso!", budgetService.create(dto)));
        } catch (BadRequestException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDTO<BudgetDTO>> update(@PathVariable Long id, @RequestBody BudgetDTO dto) {
        try {
            return ResponseEntity.ok(ResponseDTO.ok("Orçamento atualizado com sucesso!", budgetService.update(id, dto)));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> delete(@PathVariable Long id) {
        try {
            budgetService.delete(id);
            return ResponseEntity.ok(ResponseDTO.ok("Orçamento removido com sucesso!", null));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }
}
