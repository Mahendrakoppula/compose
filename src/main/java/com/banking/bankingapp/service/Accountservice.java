package com.banking.bankingapp.service;

import com.banking.bankingapp.DTO.AccountDTO;
import com.banking.bankingapp.entity.Account;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Accountservice {
    AccountDTO createAccount(AccountDTO accountDTO);
    List<AccountDTO> createAccounts(List<AccountDTO> accountDTOs);
    AccountDTO getAccountById(long id);
    CompletableFuture<List<AccountDTO>> getallAccountsAsync();
    AccountDTO deposite(Long id, double amount);
    AccountDTO withdraw(Long id, double amount);

    List<AccountDTO> getallAccounts() throws InterruptedException;

    List<Account> sortByString(String field);

    Page<Account> withpagination(int offset, int pageSize);
    Page<Account> PaginationwithSorting(int offset, int pageSize,String field);
    List<AccountDTO> findAccountsWithBalanceInRange(double minBalance, double maxBalance);
}
