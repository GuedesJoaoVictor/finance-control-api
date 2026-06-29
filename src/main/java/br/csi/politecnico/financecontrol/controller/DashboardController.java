package br.csi.politecnico.financecontrol.controller;

import br.csi.politecnico.financecontrol.dto.DashboardDTO;
import br.csi.politecnico.financecontrol.dto.ResponseDTO;
import br.csi.politecnico.financecontrol.exception.BadRequestException;
import br.csi.politecnico.financecontrol.service.DashboardService;
import br.csi.politecnico.financecontrol.utils.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<DashboardDTO>> getDashboard() {
        try {
            String uuid = AuthUtil.getUuid();
            return ResponseEntity.ok(ResponseDTO.ok(dashboardService.getDashboard(uuid)));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.err(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.err(e.getMessage()));
        }
    }
}
