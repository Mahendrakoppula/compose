package com.banking.bankingapp.service.impl;

import com.banking.bankingapp.DTO.AccountDTO;
import com.banking.bankingapp.entity.Account;
import com.banking.bankingapp.mapper.AccountMapper;
import com.banking.bankingapp.repository.AccountRepository;
import com.banking.bankingapp.service.Accountservice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class AccountservceImpl implements Accountservice {

    private AccountRepository accountRepository;

    public AccountservceImpl(AccountRepository accountRepository){
        this.accountRepository= accountRepository;
    }

    public AccountDTO createAccount(AccountDTO accountDTO) {
        Account account = AccountMapper.mapToAccount(accountDTO);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDTO(savedAccount);
    }

    public List<AccountDTO> createAccounts(List<AccountDTO> accountDTOs) {
        List<AccountDTO> createdAccountDTOs = new ArrayList<>();
        for (AccountDTO accountDTO : accountDTOs) {
            Account account = AccountMapper.mapToAccount(accountDTO);
            Account savedAccount = accountRepository.save(account);
            createdAccountDTOs.add(AccountMapper.mapToAccountDTO(savedAccount));
        }
        return createdAccountDTOs;
    }
//public List<AccountDTO> createAccounts(List<AccountDTO> accountDTOs) {
//    List<CompletableFuture<AccountDTO>> futures = accountDTOs.stream()
//            .map(accountDTO -> CompletableFuture.supplyAsync(() -> createAccount(accountDTO)))
//            .collect(Collectors.toList());
//
//    CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//
//    return allFutures.thenApplyAsync(
//            v -> futures.stream()
//                    .map(CompletableFuture::join)
//                    .collect(Collectors.toList())
//    ).join();
//}



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

//    @Override
//    public List<AccountDTO> getallAccounts() {
//        List<Account>allaccounts=accountRepository.findAll();
//        return allaccounts.stream().map((account)->AccountMapper.mapToAccountDTO(account))
//               .collect(Collectors.toList());
//    }

    public List<AccountDTO> getallAccounts() throws InterruptedException {
        List<AccountDTO> accountDTOs = new CopyOnWriteArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(10); // Adjust the pool size as per your requirement

        List<Account> allAccounts = accountRepository.findAll();
        CountDownLatch latch = new CountDownLatch(allAccounts.size());

        for (Account account : allAccounts) {
            executor.submit(() -> {
                AccountDTO accountDTO = AccountMapper.mapToAccountDTO(account);
                accountDTOs.add(accountDTO);
                latch.countDown();
            });
        }

        latch.await(); // Wait for all tasks to finish
        executor.shutdown();

        return accountDTOs;
    }
    @Async
    @Override
    public CompletableFuture<List<AccountDTO>> getallAccountsAsync() {
        List<Account> allAccounts = accountRepository.findAll();
        return CompletableFuture.completedFuture(allAccounts.stream()
                .map(AccountMapper::mapToAccountDTO)
                .collect(Collectors.toList()));
    }

    @Override
    public List<Account> sortByString(String field) {
        return accountRepository.findAll(Sort.by(Sort.Direction.ASC,field));
    }

    @Override
    public Page<Account> withpagination(int offset, int pageSize) {
        return accountRepository.findAll(PageRequest.of(offset,pageSize));
    }

    @Override
    public Page<Account> PaginationwithSorting(int offset, int pageSize, String field) {
        return accountRepository.findAll(PageRequest.of(offset,pageSize).withSort(Sort.by(Sort.Direction.ASC,field)));
    }

    public List<AccountDTO> findAccountsWithBalanceInRange(double minBalance, double maxBalance) {

        List<Account>allaccounts=accountRepository.findByBalanceBetween(minBalance, maxBalance);
        return allaccounts.stream().map((account)->AccountMapper.mapToAccountDTO(account))
                .collect(Collectors.toList());
    }
}
