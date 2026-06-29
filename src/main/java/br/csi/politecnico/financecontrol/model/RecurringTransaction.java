package br.csi.politecnico.financecontrol.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "recurring_transactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RecurringTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal value;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Integer day;

    @Builder.Default
    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "bank_id", referencedColumnName = "id", nullable = false)
    private Bank bank;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
}
