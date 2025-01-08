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

package com.antgroup.openspg.server.biz.common;

import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.common.model.account.Account;
import java.util.Collection;
import java.util.List;

/** account manager */
public interface AccountManager {

  /**
   * create a new account
   *
   * @param account
   * @return
   */
  Integer create(Account account);

  /**
   * get account info by userNo
   *
   * @param userNo
   * @return
   */
  Account getByUserNo(String userNo);

  /**
   * get account info by userNo with private info
   *
   * @param userNo
   * @return
   */
  Account getWithPrivateByUserNo(String userNo);

  /**
   * get account by part of userNO or nickName or realName
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
   * update password
   *
   * @param account
   * @return
   */
  Integer updatePassword(Account account);

  /**
   * delete account
   *
   * @param workNo
   * @return
   */
  Integer deleteAccount(String workNo);

  /**
   * get sha256Hex password
   *
   * @param password
   * @param salt
   * @return
   */
  String getSha256HexPassword(String password, String salt);

  String createSalt();

  String createToken(String str);

  /**
   * update user config
   *
   * @param userNo
   * @param config
   * @return
   */
  int updateUserConfig(String userNo, String config);
}
