package com.example.easybank.service.implementation;

import com.example.easybank.domain.dto.request.BillRequestDTO;
import com.example.easybank.domain.dto.response.BillResponseDTO;
import com.example.easybank.domain.dto.response.PageResponse;
import com.example.easybank.domain.mapper.BillMapper;
import com.example.easybank.domain.mapper.PageMapper;
import com.example.easybank.exception.*;
import com.example.easybank.service.BillService;
import com.example.easybank.domain.entity.Account;
import com.example.easybank.domain.entity.Bill;
import com.example.easybank.domain.entity.UserData;
import com.example.easybank.repository.AccountRepository;
import com.example.easybank.repository.BillRepository;
import com.example.easybank.repository.UserRepository;
import com.example.easybank.util.BillSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {
    private final BillRepository billRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public void save(BillRequestDTO billRequestDTO) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserData user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new ModelNotFoundException("User not found"));

        if (billRequestDTO.getId() != null) {
            if (!billRequestDTO.getId().equals(user.getId())) {
                throw new AccessDeniedException("Insufficient funds");
            }
        }

        if (billRequestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        Bill bill = BillMapper.toEntity(billRequestDTO);
        bill.setUser(user);
        bill.setState("PENDING");

        billRepository.save(bill);

    }

    @Override
    public PageResponse<BillResponseDTO> getMyBillsPaged(Pageable pageable) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserData user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new ModelNotFoundException("User not found"));

        Specification<Bill> spec = Specification.allOf(
                BillSpecifications.hasState("PENDING"),
                BillSpecifications.hasUserId(user.getId())
        );

        Page<Bill> page = billRepository.findAll(spec, pageable);
        
        Page<BillResponseDTO> billPage = page.map(BillMapper::toDTO);

        return PageMapper.map(billPage);
    }

    @Override
    public void delete(UUID id) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Bill not found"));

        if (!bill.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You do not have permission to delete this bill.");
        }

        billRepository.delete(bill);
    }

    @Override
    @Transactional
    public void payBill(UUID id) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new BillNotFoundException("Bill not found"));

        Account account = bill.getUser().getAccounts().getFirst();

        BigDecimal billAmount = bill.getAmount();
        BigDecimal accountBalance = account.getBalance();

        if (billAmount.compareTo(accountBalance) > 0) {
            throw new InsufficientFundsException("You don't have enough money");
        }

        if (!bill.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("User is not the owner of this bill");
        }

        if (bill.getState().equals("PAID")) {
            throw new BillAlreadyPaidException("Bill is already paid");
        }
        account.setBalance(account.getBalance().subtract(bill.getAmount()));
        bill.setState("PAID");


        billRepository.save(bill);
        accountRepository.save(account);

        try{
            accountRepository.save(account);
        }
        catch (DataAccessException e){
            throw new StorageException("Failed to update account");
        }
    }
}
