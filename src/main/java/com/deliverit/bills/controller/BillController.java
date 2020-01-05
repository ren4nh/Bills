package com.deliverit.bills.controller;

import java.util.stream.Collectors;

import javax.validation.Valid;

import com.deliverit.bills.domain.Bill;
import com.deliverit.bills.dto.BillDTO;
import com.deliverit.bills.service.BillService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @PostMapping
    public ResponseEntity<Object> saveBill(@Valid @RequestBody BillDTO billDTO) {
        Bill bill = new Bill();
        BeanUtils.copyProperties(billDTO, bill);
        bill = billService.save(bill);

        return ResponseEntity.status(HttpStatus.CREATED).body(bill.toDTO());
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return new ResponseEntity<Object>(
                billService.getAll().stream().map(b -> b.toDTO()).collect(Collectors.toList()), HttpStatus.OK);
    }
}