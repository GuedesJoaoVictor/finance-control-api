package br.csi.politecnico.financecontrol.repository;

import br.csi.politecnico.financecontrol.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankRepository extends JpaRepository<Bank, Long> {

    Optional<Bank> findBankByName(String name);

    @Query(nativeQuery = true, value = """
    SELECT b.* FROM bank b
    WHERE NOT EXISTS (
        SELECT 1
        FROM user_bank ub
        JOIN users u ON u.id = ub.user_id
        WHERE ub.bank_id = b.id
          AND u.uuid = :uuid
    );
    """)
    List<Bank> findAllBanksByUserUuid(UUID uuid);
}
