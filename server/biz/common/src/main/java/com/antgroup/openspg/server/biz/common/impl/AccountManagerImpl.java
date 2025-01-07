package com.antgroup.openspg.server.biz.common.impl;

import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.biz.common.AccountManager;
import com.antgroup.openspg.server.biz.common.PermissionManager;
import com.antgroup.openspg.server.common.model.account.Account;
import com.antgroup.openspg.server.common.model.permission.Permission;
import com.antgroup.openspg.server.common.service.account.AccountRepository;
import com.antgroup.openspgapp.common.util.enums.PermissionEnum;
import com.antgroup.openspgapp.common.util.enums.ResourceTagEnum;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountManagerImpl implements AccountManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountManagerImpl.class);

  @Autowired private AccountRepository accountRepository;

  @Autowired private PermissionManager permissionManager;

  @Override
  public Integer create(Account account) {
    if (account == null || StringUtils.isBlank(account.getWorkNo())) {
      return 0;
    }
    return accountRepository.insert(account);
  }

  @Override
  public Account getByUserNo(String userNo) {
    return accountRepository.selectByUserNo(userNo);
  }

  @Override
  public Account getWithPrivateByUserNo(String userNo) {
    return accountRepository.selectWithPrivateByUserNo(userNo);
  }

  @Override
  public List<Account> query(String keyword) {
    return accountRepository.query(keyword);
  }

  @Override
  public Paged<Account> getAccountList(String loginAccount, Integer page, Integer size) {
    List<Permission> superUserList =
        permissionManager.getPermissionByUserRolesAndId(
            Lists.newArrayList(0L), null, null, ResourceTagEnum.PLATFORM.name());
    Map<String, List<String>> userRoleNamesMap = new HashMap<>();
    superUserList.forEach(
        permission -> {
          userRoleNamesMap
              .computeIfAbsent(permission.getUserNo(), k -> Lists.newArrayList())
              .add(PermissionEnum.getRoleTypeById(permission.getRoleId()).name());
        });
    Paged<Account> accountPaged = accountRepository.getAccountList(loginAccount, page, size);
    if (accountPaged != null && CollectionUtils.isNotEmpty(accountPaged.getResults())) {
      accountPaged
          .getResults()
          .forEach(account -> account.setRoleNames(userRoleNamesMap.get(account.getWorkNo())));
    }
    return accountPaged;
  }

  @Override
  public List<Account> getSimpleAccountByUserNoList(Collection<String> userNos) {
    return accountRepository.getSimpleAccountByUserNoList(userNos);
  }

  @Override
  public Integer updatePassword(Account account) {
    if (account == null
        || StringUtils.isBlank(account.getWorkNo())
        || StringUtils.isBlank(account.getPassword())) {
      return 0;
    }
    Account oldAccount = accountRepository.selectWithPrivateByUserNo(account.getWorkNo());
    Account record = new Account();
    record.setWorkNo(account.getWorkNo());
    record.setPassword(getSha256HexPassword(account.getPassword(), oldAccount.getSalt()));
    return accountRepository.updateByUserNo(record);
  }

  @Override
  public Integer deleteAccount(String userNo) {
    if (StringUtils.isBlank(userNo)) {
      return 0;
    }
    return accountRepository.deleteByUserNo(userNo);
  }

  @Override
  public String getSha256HexPassword(String password, String salt) {
    return DigestUtils.sha256Hex(password + salt);
  }

  /**
   * Create salt string.
   *
   * @return the string
   */
  @Override
  public String createSalt() {
    String charsBag = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    String randomStr = "";
    Random random = new Random();
    for (int i = 0; i < 5; i++) {
      randomStr += charsBag.charAt(random.nextInt(charsBag.length() - 1));
    }
    return randomStr;
  }

  @Override
  public String createToken(String str) {
    Locale defloc = Locale.getDefault();
    String token = "";
    try {
      String token32 = DigestUtils.md5Hex(str);
      String token16 = token32.substring(8, 24);
      for (int i = 0; i < token16.length(); i++) {
        String tokenIndex = token16.substring(i, i + 1);
        if (tokenIndex.compareTo("9") > 0 && Math.random() > 0.5) {
          tokenIndex = tokenIndex.toUpperCase(defloc);
        }
        token += tokenIndex;
      }
    } catch (Exception e) {
      LOGGER.warn("create token error: str=" + str, e);
    }
    return token;
  }

  @Override
  public int updateUserConfig(String userNo, String config) {
    return accountRepository.updateUserConfig(userNo, config);
  }
}
