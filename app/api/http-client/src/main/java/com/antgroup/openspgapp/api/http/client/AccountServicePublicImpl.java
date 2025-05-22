package com.antgroup.openspgapp.api.http.client;

import com.alipay.sofa.common.utils.StringUtil;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.api.http.client.account.AccountService;
import com.antgroup.openspg.server.biz.common.AccountManager;
import com.antgroup.openspg.server.biz.common.PermissionManager;
import com.antgroup.openspg.server.common.model.account.Account;
import com.antgroup.openspg.server.common.model.exception.IllegalParamsException;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.AccountConvertor;
import com.antgroup.openspgapp.common.util.utils.AESUtils;
import com.antgroup.openspgapp.common.util.utils.LoginCacheHelper;
import com.antgroup.openspgapp.common.util.utils.LoginContextHelper;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(
    name = {"env"},
    havingValue = "public")
@Service
/* loaded from: com.antgroup.openspgapp-api-http-client-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/client/AccountServicePublicImpl.class */
public class AccountServicePublicImpl implements AccountService {

  @Autowired private AccountManager accountManager;

  @Autowired private PermissionManager permissionManager;

  public Account getLoginUser() {
    Object object = LoginContextHelper.getUserFromCtx();
    if (object instanceof Account) {
      return (Account) object;
    }
    return null;
  }

  public List<Account> getAccountByKeyword(String keyword) {
    if (StringUtils.isBlank(keyword)) {
      return null;
    }
    return this.accountManager.query(keyword);
  }

  public Account getByUserNo(String userNo) {
    return this.accountManager.getByUserNo(userNo);
  }

  public Account getWithPrivateByUserNo(String userNo) {
    return this.accountManager.getWithPrivateByUserNo(userNo);
  }

  public Integer create(Account account) {
    account.setWorkNo(account.getAccount());
    account.setRealName(account.getAccount());
    account.setNickName(account.getAccount());
    if (account == null
        || com.antgroup.openspg.common.util.StringUtils.isBlank(account.getWorkNo())) {
      return 0;
    }
    String salt = this.accountManager.createSalt();
    account.setSalt(salt);
    account.setPassword(getSha256HexPassword(account.getPassword(), salt));
    Long stamp = Long.valueOf(System.currentTimeMillis());
    String str = account.getWorkNo() + salt + stamp;
    String token = this.accountManager.createToken(str);
    account.setToken(token);
    return this.accountManager.create(account);
  }

  public Integer updatePassword(Account account) {
    LoginCacheHelper.removeLocalLoginAccount(account.getWorkNo());
    return this.accountManager.updatePassword(account);
  }

  public Integer deleteAccount(String workNo) {
    LoginCacheHelper.removeLocalLoginAccount(workNo);
    return this.accountManager.deleteAccount(workNo);
  }

  public Paged<Account> getAccountList(String account, Integer page, Integer size) {
    return this.accountManager.getAccountList(account, page, size);
  }

  public String getSha256HexPassword(String password, String salt) {
    return this.accountManager.getSha256HexPassword(password, salt);
  }

  public Account getCurrentAccount(Cookie[] cookies) throws IOException {
    Account account;
    String iamToken = getCookieValue(cookies, "OPEN_SPG_TOKEN");
    if (com.antgroup.openspg.common.util.StringUtils.isBlank(iamToken)) {
      return null;
    }
    String openSpgToken = AESUtils.decryptWithCTR(iamToken, "open_spg_token_secret");
    if (!openSpgToken.contains(":")) {
      return null;
    }
    String workNo = openSpgToken.split(":")[0];
    if (StringUtil.isNotBlank(openSpgToken)
        && (account = (Account) LoginCacheHelper.getLocalLoginAccount(workNo)) != null) {
      return account;
    }
    String accessKey = openSpgToken.split(":")[1];
    Account account2 = getWithPrivateByUserNo(workNo);
    if (account2 == null) {
      return null;
    }
    boolean isSuper = this.permissionManager.isSuper(account2.getWorkNo());
    if (com.antgroup.openspg.common.util.StringUtils.equals(account2.getPassword(), accessKey)
        || isSuper) {
      account2.setPassword((String) null);
      account2.setToken((String) null);
      LoginCacheHelper.putLocalLoginAccount(account2.getWorkNo(), account2);
      return account2;
    }
    return null;
  }

  public boolean login(Account account, HttpServletResponse response) {
    Account accountInfo = getWithPrivateByUserNo(account.getAccount());
    if (accountInfo == null) {
      throw new IllegalParamsException("user or password error", new Object[0]);
    }
    String passwordSalt = getSha256HexPassword(account.getPassword(), accountInfo.getSalt());
    if (!com.antgroup.openspg.common.util.StringUtils.equals(
        passwordSalt, accountInfo.getPassword())) {
      throw new IllegalParamsException("user or password error", new Object[0]);
    }
    String openSpgToken =
        AESUtils.encryptWithCTR(
            account.getAccount() + ":" + account.getPassword(), "open_spg_token_secret");
    Cookie cookie = new Cookie("OPEN_SPG_TOKEN", openSpgToken);
    cookie.setMaxAge(43200);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    response.addCookie(cookie);
    boolean isSuper = this.permissionManager.isSuper(accountInfo.getWorkNo());
    boolean isPass = true;
    if (accountInfo.getGmtCreate().getTime() == accountInfo.getGmtModified().getTime() && isSuper) {
      isPass = false;
    }
    account.setPassword((String) null);
    account.setToken((String) null);
    LoginCacheHelper.putLocalLoginAccount(accountInfo.getWorkNo(), accountInfo);
    return isPass;
  }

  public String logout(String workNo, String redirectUrl) {
    return redirectUrl + "/#/login";
  }

  public int updateUserConfig(Account account, Cookie[] cookies) {
    account.setUseCurrentLanguage(AccountConvertor.getUseCurrentLanguage(account.getConfig()));
    LoginCacheHelper.putLocalLoginAccount(account.getWorkNo(), account);
    return this.accountManager.updateUserConfig(account.getWorkNo(), account.getConfig());
  }

  private String getCookieValue(Cookie[] cookies, String key) {
    if (cookies != null && com.antgroup.openspg.common.util.StringUtils.isNotBlank(key)) {
      for (Cookie cookie : cookies) {
        if (key.equalsIgnoreCase(cookie.getName())) {
          return cookie.getValue();
        }
      }
      return null;
    }
    return null;
  }
}
