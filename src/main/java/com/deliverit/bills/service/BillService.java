package com.deliverit.bills.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.deliverit.bills.domain.Bill;
import com.deliverit.bills.repository.BillRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    public Bill save(Bill bill) {
        bill = checkIfBillIsOverdued(bill);
        billRepository.save(bill);
        return bill;
    }

    public List<Bill> getAll() {
        return billRepository.findAll(Sort.by(Direction.ASC, "id"));
    }

    private Bill checkIfBillIsOverdued(Bill bill) {
        if (bill.getPaymentDate().isAfter(bill.getDueDate())) {
            BigDecimal finePercentage = null;
            BigDecimal interestRate = null;
            BigDecimal fine = BigDecimal.ZERO;
            BigDecimal interest = BigDecimal.ZERO;
            long days = ChronoUnit.DAYS.between(bill.getDueDate(), bill.getPaymentDate());
            if (days > 3 && days < 5) {
                finePercentage = new BigDecimal(3l);
                interestRate = new BigDecimal(0.2).multiply(new BigDecimal(days));
            } else if (days > 5) {
                finePercentage = new BigDecimal(5l);
                interestRate = new BigDecimal(0.3).multiply(new BigDecimal(days));
            } else {
                finePercentage = new BigDecimal(2l);
                interestRate = new BigDecimal(0.1).multiply(new BigDecimal(days));
            }

            fine = bill.getValue().multiply(finePercentage).divide(new BigDecimal(100));
            interest = bill.getValue().add(fine).multiply(interestRate).divide(new BigDecimal(100));

            bill.setCorrectedValue(bill.getValue().add(fine).add(interest).setScale(2, RoundingMode.HALF_EVEN));
            bill.setDaysOverdue(days);

        } else {
            bill.setCorrectedValue(bill.getValue());
        }
        return bill;
    }

}