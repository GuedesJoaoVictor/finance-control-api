package br.csi.politecnico.financecontrol.repository;

import br.csi.politecnico.financecontrol.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findAllByUser_Uuid(UUID uuid);

    Optional<Budget> findByCategory_IdAndUser_UuidAndMonthAndYear(Long categoryId, UUID uuid, Integer month, Integer year);
}
