package com.banking.bankingapp.controller;

import com.banking.bankingapp.DTO.AccountDTO;
import com.banking.bankingapp.service.Accountservice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private Accountservice accountservice;

    public AccountController (Accountservice accountservice){
        this.accountservice=accountservice ;
    }

    @PostMapping
    public ResponseEntity<AccountDTO> addAccount(@RequestBody AccountDTO accountDTO){
        return new ResponseEntity<>(accountservice.createAccount(accountDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccpontByid(@PathVariable Long id){

        AccountDTO accountDTO = accountservice.getAccountById(id);
        return ResponseEntity.ok(accountDTO);
    }

    @PutMapping("/{id}/deposite")
    public ResponseEntity<AccountDTO> depositeAmount(@PathVariable Long id,@RequestBody Map<String,Double> request){
       Double amount=request.get("amount");
        AccountDTO accountDTO=accountservice.deposite(id,request.get("amount"));
        return ResponseEntity.ok(accountDTO);
    }

    @PutMapping("/{id}/withdaw")
    public ResponseEntity<AccountDTO> withDrawAmount(@PathVariable Long id, @RequestBody Map<String,Double> request){
        double amount = request.get("amount");
        AccountDTO accountDTO = accountservice.withdraw(id,amount);
        return ResponseEntity.ok(accountDTO);
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getallAccounts(){
        List<AccountDTO> accounts = accountservice.getallAccounts();
        return ResponseEntity.ok(accounts);
    }
}
