package com.antgroup.openspg.server.infra.dao.repository.common;

import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.common.model.account.Account;
import com.antgroup.openspg.server.common.service.account.AccountRepository;
import com.antgroup.openspg.server.infra.dao.dataobject.AccountDO;
import com.antgroup.openspg.server.infra.dao.mapper.AccountMapper;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.AccountConvertor;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

  @Autowired private AccountMapper accountMapper;

  @Override
  public Integer insert(Account account) {
    AccountDO accountDO = AccountConvertor.toDO(account);
    accountDO.setGmtCreate(new Date());
    accountDO.setGmtModified(new Date());
    return accountMapper.insert(accountDO);
  }

  @Override
  public Integer update(Account account) {

    return null;
  }

  @Override
  public Integer updateByUserNo(Account account) {
    AccountDO accountDO = AccountConvertor.toDO(account);
    accountDO.setGmtModified(new Date());
    return accountMapper.updateByUserNo(accountDO);
  }

  @Override
  public Integer deleteByUserNo(String userNo) {
    return accountMapper.deleteByUserNo(userNo);
  }

  @Override
  public Account selectByUserNo(String userNo) {
    AccountDO accountDO = accountMapper.getByUserId(userNo);
    if (accountDO == null) {
      return null;
    }
    return AccountConvertor.toModel(accountDO);
  }

  @Override
  public Account selectWithPrivateByUserNo(String userNo) {
    AccountDO accountDO = accountMapper.getByUserId(userNo);
    if (accountDO == null) {
      return null;
    }
    return AccountConvertor.toModelWithPrivate(accountDO);
  }

  @Override
  public List<Account> query(String keyword) {
    List<AccountDO> list = accountMapper.getUserLikeUserNoOrDomainNoOrName(keyword);
    if (CollectionUtils.isEmpty(list)) {
      return Lists.newArrayList();
    }
    return list.stream()
        .map(accountDO -> AccountConvertor.toModel(accountDO))
        .collect(Collectors.toList());
  }

  @Override
  public Paged<Account> getAccountList(String loginAccount, Integer start, Integer size) {
    AccountDO accountDO = new AccountDO();
    accountDO.setDomainAccount(loginAccount);
    Paged<Account> result = new Paged<>();
    result.setPageIdx(start);
    result.setPageSize(size);
    long count = accountMapper.selectCountByCondition(accountDO);
    result.setTotal(count);
    List<Account> list = new ArrayList<>();
    start = start > 0 ? start : 1;
    int startPage = (start - 1) * size;
    List<AccountDO> accountDOS = accountMapper.selectByCondition(accountDO, startPage, size);
    if (CollectionUtils.isNotEmpty(accountDOS)) {
      list = accountDOS.stream().map(AccountConvertor::toModel).collect(Collectors.toList());
    }
    result.setResults(list);
    return result;
  }

  @Override
  public List<Account> getSimpleAccountByUserNoList(Collection<String> userNos) {
    List<AccountDO> list = accountMapper.getSimpleAccountByUserNoList(userNos);
    if (CollectionUtils.isEmpty(list)) {
      return Lists.newArrayList();
    }
    return list.stream()
        .map(accountDO -> AccountConvertor.toModel(accountDO))
        .collect(Collectors.toList());
  }

  @Override
  public int updateUserConfig(String userNo, String config) {
    return accountMapper.updateUserConfig(userNo, config);
  }
}
