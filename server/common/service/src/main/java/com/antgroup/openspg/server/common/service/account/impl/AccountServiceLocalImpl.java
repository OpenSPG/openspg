/*
 * Copyright 2023 OpenSPG Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */
package com.antgroup.openspg.server.common.service.account.impl;

import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.api.http.client.account.AccountService;
import com.antgroup.openspg.server.common.model.account.Account;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "env", havingValue = "local")
public class AccountServiceLocalImpl implements AccountService {

  private static final String DEFAULT_USER_NO = "local_user";
  private static final String DEFAULT_USER_NAME = "Local Developer";

  @Override
  public Account getLoginUser() {
    Account account = new Account();
    account.setAccount(DEFAULT_USER_NO);
    account.setRealName(DEFAULT_USER_NAME);
    account.setWorkNo(DEFAULT_USER_NO);
    return account;
  }

  @Override
  public List<Account> getAccountByKeyword(String keyword) {
    return Collections.singletonList(getLoginUser());
  }

  @Override
  public Account getByUserNo(String userNo) {
    if (DEFAULT_USER_NO.equals(userNo)) {
      return getLoginUser();
    }
    return null;
  }

  @Override
  public Account getWithPrivateByUserNo(String userNo) {
    return getByUserNo(userNo);
  }

  @Override
  public Integer create(Account account) {
    return 1;
  }

  @Override
  public Integer updatePassword(Account account) {
    return 1;
  }

  @Override
  public Integer deleteAccount(String workNo) {
    return 1;
  }

  @Override
  public Paged<Account> getAccountList(String account, Integer page, Integer size) {
    List<Account> accounts = Collections.singletonList(getLoginUser());
    Paged<Account> paged = new Paged<Account>(size, page);
    paged.setResults(accounts);
    paged.setTotal(1L);
    return paged;
  }

  @Override
  public String getSha256HexPassword(String password, String salt) {
    return "hashed_password";
  }

  @Override
  public Account getCurrentAccount(Cookie[] cookies) throws IOException {
    return getLoginUser();
  }

  @Override
  public boolean login(Account account, HttpServletResponse response) {
    return true;
  }

  @Override
  public String logout(String workNo, String redirectUrl) {
    return redirectUrl;
  }

  @Override
  public int updateUserConfig(Account account, Cookie[] cookies) {
    return 1;
  }
}
