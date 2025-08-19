package com.infrastructure.account_distributed.api;

import com.domain.account.model.Account;
import com.domain.account.port.query.AccountPageQuery;
import com.domain.account.usecase.GetAccount;
import com.domain.account.usecase.GetAccountPage;
import com.domain.account.usecase.request.AccountRequest;
import com.domain.account.usecase.request.PageResult;
import com.infrastructure.account_distributed.api.dto.AccountDTO;
import com.infrastructure.account_distributed.api.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final GetAccount getAccount;

    private final GetAccountPage getAccountPage;

    @GetMapping("/account/{ownerId}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable Long ownerId) {
        return ResponseEntity.ok(AccountMapper.INSTANCE.mapFromModelToDto(getAccount.execute(new AccountRequest(ownerId))));
    }

    @GetMapping("/accounts")
    public ResponseEntity<Page<AccountDTO>> getAllAccounts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        PageResult<Account> result = getAccountPage.execute(new AccountPageQuery(page, size));

        List<AccountDTO> content = result.content().stream()
                .map(AccountMapper.INSTANCE::mapFromModelToDto)
                .toList();

        Page<AccountDTO> dtoPage = new PageImpl<>(content, PageRequest.of(page, size), result.totalElements());
        return ResponseEntity.ok(dtoPage);
    }
}
