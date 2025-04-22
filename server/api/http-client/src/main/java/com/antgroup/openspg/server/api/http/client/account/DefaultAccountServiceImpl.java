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

package com.antgroup.openspg.server.api.http.client.account;

import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.common.model.account.Account;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnMissingBean(value = {AccountService.class})
public class DefaultAccountServiceImpl implements AccountService {

  @Override
  public Account getLoginUser() {
    return null;
  }

  @Override
  public List<Account> getAccountByKeyword(String keyword) {
    throw new IllegalArgumentException("not implemented");
  }

  @Override
  public Account getByUserNo(String userNo) {
    throw new IllegalArgumentException("not implemented");
  }

  @Override
  public Account getWithPrivateByUserNo(String userNo) {
    throw new IllegalArgumentException("not implemented");
  }

  @Override
  public Integer create(Account account) {
    throw new IllegalArgumentException("not implemented");
  }

  @Override
  public Integer updatePassword(Account account) {
    throw new IllegalArgumentException("not implemented");
  }

  @Override
  public Integer deleteAccount(String workNo) {
    throw new IllegalArgumentException("not implemented");
  }

  @Override
  public Paged<Account> getAccountList(String account, Integer page, Integer size) {
    throw new IllegalArgumentException("not implemented");
  }

  @Override
  public String getSha256HexPassword(String password, String salt) {
    throw new IllegalArgumentException("not implemented");
  }

  @Override
  public Account getCurrentAccount(Cookie[] cookies) {
    throw new IllegalArgumentException("not implemented");
  }

  @Override
  public boolean login(Account account, HttpServletResponse response) {
    throw new IllegalArgumentException("not implemented");
  }

  @Override
  public String logout(String workNo, String redirectUrl) {
    throw new IllegalArgumentException("not implemented");
  }

  @Override
  public int updateUserConfig(Account account, Cookie[] cookies) {
    throw new IllegalArgumentException("not implemented");
  }
}
