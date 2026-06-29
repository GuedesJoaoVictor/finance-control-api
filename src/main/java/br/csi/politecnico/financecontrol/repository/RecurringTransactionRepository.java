package br.csi.politecnico.financecontrol.repository;

import br.csi.politecnico.financecontrol.model.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {
    List<RecurringTransaction> findAllByUser_Uuid(UUID uuid);

    List<RecurringTransaction> findAllByUser_UuidAndActiveTrue(UUID uuid);
}
