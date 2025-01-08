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

package com.antgroup.openspg.server.common.service.account;

import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.common.model.account.Account;
import java.util.Collection;
import java.util.List;

/** account repository */
public interface AccountRepository {

  /**
   * insert a account into db
   *
   * @param account
   * @return
   */
  Integer insert(Account account);

  /**
   * update account
   *
   * @param account
   * @return
   */
  Integer update(Account account);

  /**
   * update account by userNo
   *
   * @param record
   * @return
   */
  Integer updateByUserNo(Account record);

  /**
   * delete by userNo
   *
   * @param userNo java.lang.String
   * @return int
   * @param userNo
   * @return
   */
  Integer deleteByUserNo(String userNo);

  /**
   * select account by userNo
   *
   * @param userNo
   * @return
   */
  Account selectByUserNo(String userNo);

  /**
   * select account by userNo with private info
   *
   * @param userNo
   * @return
   */
  Account selectWithPrivateByUserNo(String userNo);

  /**
   * query account by part of userNo or nickName or realName
   *
   * @param keyword
   * @return
   */
  List<Account> query(String keyword);

  /**
   * get account list
   *
   * @param loginAccount
   * @param page
   * @param size
   * @return
   */
  Paged<Account> getAccountList(String loginAccount, Integer page, Integer size);

  /**
   * batch get simple user by userNo list
   *
   * @param userNos
   * @return
   */
  List<Account> getSimpleAccountByUserNoList(Collection<String> userNos);

  /**
   * update user config
   *
   * @param userNo
   * @param config
   * @return
   */
  int updateUserConfig(String userNo, String config);
}
