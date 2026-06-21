package br.csi.politecnico.financecontrol.controller;

import br.csi.politecnico.financecontrol.dto.CategoryDTO;
import br.csi.politecnico.financecontrol.dto.ResponseDTO;
import br.csi.politecnico.financecontrol.exception.BadRequestException;
import br.csi.politecnico.financecontrol.exception.EntityExistsException;
import br.csi.politecnico.financecontrol.exception.NotFoundException;
import br.csi.politecnico.financecontrol.service.CategoryService;
import br.csi.politecnico.financecontrol.utils.AuthUtil;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping("/find-all")
    public ResponseEntity<ResponseDTO<List<CategoryDTO>>> findAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.ok(service.findALl()));
        } catch (NotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }

    @PostMapping("create")
    public ResponseEntity<CategoryDTO> create(@RequestBody CategoryDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    

    @GetMapping("/find-all-by-user/{uuid}")
    public ResponseEntity<ResponseDTO<List<CategoryDTO>>> findAllByUser(@PathVariable @Valid String uuid) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.ok(service.findAllByUser(uuid)));
        } catch (NotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }

    @GetMapping("/find-all-by-user")
    public ResponseEntity<ResponseDTO<List<CategoryDTO>>> findAllByCurrentUser() {
        try {
            String uuid = AuthUtil.getUuid();
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.ok(service.findAllByUser(uuid)));
        } catch (NotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }

    @PostMapping("/create-by-user-uuid/{uuid}")
    public ResponseEntity<ResponseDTO<CategoryDTO>> createByUserUuid(@PathVariable @Valid String uuid, @RequestBody @Valid CategoryDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.ok("Categoria criada com sucesso!", service.createByUserUuid(uuid, dto)));
        } catch (BadRequestException | EntityExistsException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }

    @PostMapping("/create-by-user")
    public ResponseEntity<ResponseDTO<CategoryDTO>> createByCurrentUser(@RequestBody @Valid CategoryDTO dto) {
        try {
            String uuid = AuthUtil.getUuid();
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.ok("Categoria criada com sucesso!", service.createByUserUuid(uuid, dto)));
        } catch (BadRequestException | EntityExistsException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<ResponseDTO<Boolean>> deleteById(@PathVariable @Valid Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.ok("Deletado com sucesso!", service.deleteById(id)));
        } catch (NotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }

    @PatchMapping("/update-by-id/{id}")
    public ResponseEntity<ResponseDTO<CategoryDTO>> updateById(@PathVariable("id") @Valid Long categoryId, @RequestBody @Valid CategoryDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.ok("Atualizado com sucesso!", service.updateById(categoryId, dto)));
        } catch (NotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }

}
