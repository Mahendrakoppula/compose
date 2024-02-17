package com.banking.bankingapp.service;

import com.banking.bankingapp.DTO.AccountDTO;
import com.banking.bankingapp.entity.Account;

import java.util.List;

public interface Accountservice {
    AccountDTO createAccount(AccountDTO accountDTO);
    AccountDTO getAccountById(long id);
    AccountDTO deposite(Long id, double amount);
    AccountDTO withdraw(Long id, double amount);

    List<AccountDTO> getallAccounts();

}
