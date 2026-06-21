package br.csi.politecnico.financecontrol.repository;

import br.csi.politecnico.financecontrol.model.Revenue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RevenuesRepository extends JpaRepository<Revenue, Long> {

    List<Revenue> findAllByUser_Uuid(UUID uuid);

    List<Revenue> findAllByBank_IdAndUser_Uuid(Long bankId, UUID userUuid);

    void deleteAllByBank_IdAndUser_Uuid(Long bankId, UUID userUuid);
}
