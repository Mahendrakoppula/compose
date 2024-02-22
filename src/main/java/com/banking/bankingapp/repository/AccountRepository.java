package com.banking.bankingapp.repository;

import com.banking.bankingapp.DTO.AccountDTO;
import com.banking.bankingapp.entity.Account;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account,Long> {

    List<Account> findByBalanceBetween(double minBalance, double maxBalance);
}
