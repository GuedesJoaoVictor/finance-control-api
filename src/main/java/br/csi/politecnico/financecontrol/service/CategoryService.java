package br.csi.politecnico.financecontrol.service;

import br.csi.politecnico.financecontrol.dto.CategoryDTO;
import br.csi.politecnico.financecontrol.dto.UserDTO;
import br.csi.politecnico.financecontrol.exception.BadRequestException;
import br.csi.politecnico.financecontrol.exception.EntityExistsException;
import br.csi.politecnico.financecontrol.exception.NotFoundException;
import br.csi.politecnico.financecontrol.model.Category;
import br.csi.politecnico.financecontrol.model.User;
import br.csi.politecnico.financecontrol.repository.CategoryRepository;
import br.csi.politecnico.financecontrol.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public CategoryService(UserRepository userRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDTO> findALl() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDTO> list = new ArrayList<>();

        if (CollectionUtils.isEmpty(categories)) {
            throw new NotFoundException("Categorias não econtradas.");
        }

        for (Category category : categories) {
            list.add(CategoryDTO.builder()
                    .id(category.getId())
                    .user(category.getUser() != null ? new UserDTO(category.getUser()) : null)
                    .name(category.getName())
                    .type(category.getType())
                    .build());
        }

        return list;
    }

    public List<CategoryDTO> findAllByUser(String uuid) {
        List<Category> categories = categoryRepository.findAllDefaultAndAllByUserUuid(UUID.fromString(uuid));
        List<CategoryDTO> list = new ArrayList<>();
        for (Category category : categories) {
            list.add(CategoryDTO.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .type(category.getType())
                    .build());
        }

        return list;
    }

    public CategoryDTO createByUserUuid(String uuid, CategoryDTO dto) {
        User user = userRepository.findByUuid(UUID.fromString(uuid)).
                orElseThrow(() -> new BadRequestException("Usuário não encontrado"));
        Category category = categoryRepository.findCategoryExistsPerUser(user.getId(), dto.getName());
        if (category != null) {
            throw new EntityExistsException("Categoria já existe.");
        }
        if (dto.getName() == null || dto.getName().isEmpty() || dto.getType() == null || dto.getType().isEmpty()) {
            throw new BadRequestException("Campos inválidos.");
        }
        Category newCategory = Category.builder()
                .user(user)
                .name(dto.getName())
                .type(dto.getType())
                .build();
        categoryRepository.saveAndFlush(newCategory);
        dto.setId(newCategory.getId());
        dto.setUser(new  UserDTO(user));

        return dto;
    }

    public Boolean deleteById(Long id) {
        Category category = categoryRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Categoria não encontrada"));

        categoryRepository.deleteById(category.getId());
        return true;
    }

    public CategoryDTO updateById(Long id, CategoryDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada."));

        category.setName(dto.getName());
        category.setType(dto.getType());
        categoryRepository.saveAndFlush(category);

        return new CategoryDTO(category);
    }

    public CategoryDTO create(CategoryDTO dto) {
        Category category = Category.builder()
                .name(dto.getName())
                .type(dto.getType())
                .build();
        categoryRepository.saveAndFlush(category);
        dto.setId(category.getId());

        return dto;
    }

}
