package br.csi.politecnico.financecontrol.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "budgets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"category_id", "user_id", "month", "year"})
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "limit_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal limitAmount;
}
