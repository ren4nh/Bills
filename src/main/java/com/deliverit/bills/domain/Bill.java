package com.deliverit.bills.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.deliverit.bills.dto.BillDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "bills")
@SequenceGenerator(name = "seqBills", sequenceName = "SEQBILLS", allocationSize = 1)
public class Bill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "seqBills", strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private BigDecimal value;
    private BigDecimal correctedValue;
    private LocalDate paymentDate;
    private LocalDate dueDate;
    private Long daysOverdue = 0l;

    public BillDTO toDTO() {
        return new BillDTO(name, value, correctedValue, paymentDate, daysOverdue);
    }
}