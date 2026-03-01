package com.bank.management.app.serviceImpl;

import com.bank.management.app.dto.RequestDTO.TransactionRequestDTO;
import com.bank.management.app.dto.ResponseDTO.AccountStatementResponseDTO;
import com.bank.management.app.dto.ResponseDTO.TransactionResonseDTO;
import com.bank.management.app.entity.Account;
import com.bank.management.app.entity.Customer;
import com.bank.management.app.entity.Transaction;
import com.bank.management.app.exception.ResourceNotFoundException;
import com.bank.management.app.repository.AccountRepository;
import com.bank.management.app.repository.CustomerRepository;
import com.bank.management.app.repository.TransactionRepository;
import com.bank.management.app.security.SecurityUtils;
import com.bank.management.app.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;
    private final CustomerRepository customerRepository;

    // ================= DEPOSIT =================
    @Override
    @Transactional
    public TransactionResonseDTO deposite(TransactionRequestDTO depositeMoney) {
        String username = SecurityUtils.getCurrentUsername();
        Customer customer = customerRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Customer Not Found"));
        Account account = accountRepository.findByCustomerIdAndAccountType(customer.getId(), depositeMoney.getAccountType()).orElseThrow(() -> new ResourceNotFoundException(depositeMoney.getAccountType() + "Account Not Found"));
        account.setBalance(account.getBalance().add(depositeMoney.getAmount()));

        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setAmount(depositeMoney.getAmount());
        tx.setTransactionType("DEPOSIT");
        tx.setTransactionDate(LocalDateTime.now());
        tx.setBalanceAfter(account.getBalance());

        transactionRepository.save(tx);
        accountRepository.save(account);

        return modelMapper.map(tx, TransactionResonseDTO.class);

    }

    // ================= WITHDRAW =================
    @Override
    @Transactional
    public TransactionResonseDTO withdraw(TransactionRequestDTO withdrawMoney) {
        String username = SecurityUtils.getCurrentUsername();

        Customer customer = customerRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Account account = accountRepository.findByCustomerIdAndAccountType(customer.getId(), withdrawMoney.getAccountType()).orElseThrow(() -> new ResourceNotFoundException(withdrawMoney.getAccountType() + " account not found"));

        if (account.getBalance().compareTo(withdrawMoney.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(withdrawMoney.getAmount()));

        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setAmount(withdrawMoney.getAmount());
        tx.setTransactionType("WITHDRAW");
        tx.setTransactionDate(LocalDateTime.now());
        tx.setBalanceAfter(account.getBalance());

        transactionRepository.save(tx);
        accountRepository.save(account);

        return modelMapper.map(tx, TransactionResonseDTO.class);
    }

    // ================= ACCOUNT STATEMENT =================
    @Override
    @Transactional(readOnly = true)
    public AccountStatementResponseDTO getAccountStatement(String accountType, Pageable pageable) {
        String username = SecurityUtils.getCurrentUsername();
        Customer customer = customerRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Account account = accountRepository.findByCustomerIdAndAccountType(customer.getId(), accountType.toUpperCase()).orElseThrow(() -> new ResourceNotFoundException(accountType + " account not found"));

        Page<Transaction> page = transactionRepository.findByAccountId(account.getId(), pageable);
        if (page.isEmpty()) {
            throw new ResourceNotFoundException("No Transactions Found");
        }

        AccountStatementResponseDTO responseDTO = new AccountStatementResponseDTO();
        responseDTO.setAccountNumber(account.getAccountNumber());
        responseDTO.setCustomerName(customer.getFullName());
        List<TransactionResonseDTO> transactions = page.getContent().stream().map(tx -> modelMapper.map(tx, TransactionResonseDTO.class)).toList();

        responseDTO.setTransactions(transactions);
        return responseDTO;
    }

    // ================= PRIVATE HELPER =================
    private Account validateAccountOwnership(Long accountId) {
        String loggedInUsername = SecurityUtils.getCurrentUsername();

        Account account = accountRepository.findById(accountId).orElseThrow(() -> new ResourceNotFoundException("Account Not Found"));

        if (!account.getCustomer().getUsername().equals(loggedInUsername)) {
            throw new AccessDeniedException("You are not allowed to operate on this account");
        }

        return account;
    }
}
