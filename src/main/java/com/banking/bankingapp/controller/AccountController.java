package com.banking.bankingapp.controller;

import com.banking.bankingapp.DTO.AccountDTO;
import com.banking.bankingapp.entity.Account;
import com.banking.bankingapp.service.Accountservice;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private Accountservice accountservice;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10); // Adjust the number of threads as needed

    public AccountController (Accountservice accountservice){
        this.accountservice=accountservice ;
    }

    @PostMapping
    public ResponseEntity<String> createAccounts(@RequestBody List<AccountDTO> accountDTOs) {
//        if (accountDTOs == null || accountDTOs.isEmpty()) {
//            return new ResponseEntity<>("No accounts provided in the request body", HttpStatus.BAD_REQUEST);
//        }

        try {
            if (accountDTOs.size() == 1) {
                // Single account: use addAccount for efficiency
                AccountDTO accountDTO = accountDTOs.get(0);
                System.out.println(accountDTO);
                AccountDTO createdAccount = accountservice.createAccount(accountDTO);
                return new ResponseEntity<>("createdAccount", HttpStatus.CREATED);
            } else {
                // Multiple accounts: use asynchronous processing for scalability
                executorService.execute(() -> accountservice.createAccounts(accountDTOs));
                return new ResponseEntity<>("Accounts created asynchronously...", HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            // Handle exceptions gracefully
            return new ResponseEntity<>("Error processing accounts: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



//    @PostMapping
//    public ResponseEntity<AccountDTO> addAccount(@RequestBody AccountDTO accountDTO){
//        return new ResponseEntity<>(accountservice.createAccount(accountDTO), HttpStatus.CREATED);
//    }

//    @PostMapping("/listaccounts")
//    public ResponseEntity<List<AccountDTO>> addAccounts(@RequestBody List<AccountDTO> accountDTOs) {
//        List<AccountDTO> createdAccounts = accountservice.createAccounts(accountDTOs);
//        return new ResponseEntity<>(createdAccounts, HttpStatus.CREATED);
//    }

//    @PostMapping("/listaccounts")
//    public ResponseEntity<String> addAccounts(@RequestBody List<AccountDTO> accountDTOs) {
//        try {
//            executorService.execute(() -> accountservice.createAccounts(accountDTOs));
//            return new ResponseEntity<>(" accounts created...", HttpStatus.ACCEPTED);
//        } catch (Exception e) {
//            return new ResponseEntity<>("Error processing accounts: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

//@PostMapping("/listaccounts")
//public ResponseEntity<List<AccountDTO>> addAccounts(@RequestBody List<AccountDTO> accountDTOs) throws ExecutionException, InterruptedException {
//
//     executorService.execute(() -> accountservice.createAccounts(accountDTOs));
//
//    return new ResponseEntity<>(accountDTOs, HttpStatus.ACCEPTED);
//}
//@PostMapping("/listaccounts")
//public ResponseEntity<CompletableFuture<Void>> addAccounts(@RequestBody List<AccountDTO> accountDTOs) {
//    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> accountservice.createAccounts(accountDTOs), executorService);
//    return new ResponseEntity<>(future, HttpStatus.ACCEPTED);
//}

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

//    @GetMapping
//    public ResponseEntity<List<AccountDTO>> getallAccounts() throws InterruptedException {
//        List<AccountDTO> accounts = accountservice.getallAccounts();
//        return ResponseEntity.ok(accounts);
//    }
@GetMapping
public CompletableFuture<ResponseEntity<List<AccountDTO>>> getallAccounts() {
    return accountservice.getallAccountsAsync()
            .thenApply(ResponseEntity::ok)
            .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
}
    @GetMapping("/sortByString/{field}")
    public ResponseEntity<List<Account>> sortByString(@PathVariable String field){
        List<Account> accounts = accountservice.sortByString(field);
        return ResponseEntity.ok(accounts);
    }
    @GetMapping("/pagenation/{offset}/{pageSize}")
    public ResponseEntity<Page<Account>> sortByString(@PathVariable int offset, @PathVariable int pageSize){
        Page<Account> accounts = accountservice.withpagination(offset, pageSize);
        return ResponseEntity.ok(accounts);
    }
    @GetMapping("/pagenationandsort/{offset}/{pageSize}/{field}")
    public ResponseEntity<Page<Account>> paginationandsort(@PathVariable int offset, @PathVariable int pageSize,@PathVariable String field){
        Page<Account> accounts = accountservice.PaginationwithSorting(offset, pageSize, field);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/accounts/{minBalance}/{maxBalance}")
    public ResponseEntity<List<AccountDTO>> getAccountsInBalanceRange(
            @PathVariable double minBalance,
            @PathVariable double maxBalance) {
        List<AccountDTO> accounts = accountservice.findAccountsWithBalanceInRange(minBalance, maxBalance);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

}

