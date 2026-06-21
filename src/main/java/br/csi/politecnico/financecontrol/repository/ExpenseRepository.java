package br.csi.politecnico.financecontrol.repository;

import br.csi.politecnico.financecontrol.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByUser_Uuid(UUID uuid);

    List<Expense> findAllByBank_IdAndUser_Uuid(Long bankId, UUID userUuid);

    void deleteAllByBank_IdAndUser_Uuid(Long bankId, UUID userUuid);
}
