package com.deliverit.bills.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillDTO {

    @NotEmpty(message = "Nome deve ser informado")
    private String name;
    @NotNull(message = "Valor deve ser informado")
    private BigDecimal value;
    private BigDecimal correctedValue;
    @NotNull(message = "Data de pagamento deve ser informada")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate paymentDate;
    @NotNull(message = "Data de vencimento deve ser informada")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dueDate;
    private Long daysOverdue = 0l;

    public BillDTO(String name, BigDecimal value, BigDecimal correctedValue, LocalDate paymentDate, Long daysOverdue) {
        this.name = name;
        this.value = value;
        this.correctedValue = correctedValue;
        this.paymentDate = paymentDate;
        this.daysOverdue = daysOverdue;
    }

    public BillDTO(String name, BigDecimal value, LocalDate paymentDate, LocalDate dueDate) {
        this.name = name;
        this.value = value;
        this.paymentDate = paymentDate;
        this.dueDate = dueDate;
    }

}