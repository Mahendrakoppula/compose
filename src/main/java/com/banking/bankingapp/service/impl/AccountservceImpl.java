package com.banking.bankingapp.service.impl;

import com.banking.bankingapp.DTO.AccountDTO;
import com.banking.bankingapp.entity.Account;
import com.banking.bankingapp.mapper.AccountMapper;
import com.banking.bankingapp.repository.AccountRepository;
import com.banking.bankingapp.service.Accountservice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountservceImpl implements Accountservice {

    private AccountRepository accountRepository;

    public AccountservceImpl(AccountRepository accountRepository){
        this.accountRepository= accountRepository;
    }
    @Override
    public AccountDTO createAccount(AccountDTO accountDTO) {
        Account account = AccountMapper.mapToAccount(accountDTO);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDTO(savedAccount);
    }

    @Override
    public AccountDTO getAccountById(long id) {

            Account account =accountRepository.findById(id).orElseThrow(()->new RuntimeException("Account not found"));
            return AccountMapper.mapToAccountDTO(account);
    }

    @Override
    public AccountDTO deposite(Long id, double amount) {
        Account account =accountRepository.findById(id).orElseThrow(()->new RuntimeException("Account not found"));
      double total=  account.getBalance()+amount;
      account.setBalance(total);
      Account saveaccount=accountRepository.save(account);
        return AccountMapper.mapToAccountDTO(saveaccount);
    }

    @Override
    public AccountDTO withdraw(Long id, double amount) {
        Account account =accountRepository.findById(id).orElseThrow(()->new RuntimeException("Account not found"));
        if(account.getBalance()<amount){
            throw new RuntimeException("Insufficient amount");
        }
        double total = account.getBalance()-amount;
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);

        return AccountMapper.mapToAccountDTO(savedAccount);
    }

    @Override
    public List<AccountDTO> getallAccounts() {
        List<Account>allaccounts=accountRepository.findAll();
        return allaccounts.stream().map((account)->AccountMapper.mapToAccountDTO(account))
               .collect(Collectors.toList());
    }
}
