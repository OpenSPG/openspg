package com.antgroup.openspgapp.api.http.server.account;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.common.util.enums.LanguageEnum;
import com.antgroup.openspg.common.util.exception.SpgException;
import com.antgroup.openspg.common.util.exception.message.SpgMessageEnum;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.api.http.server.HttpResult;
import com.antgroup.openspg.server.biz.common.PermissionManager;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspg.server.common.model.account.Account;
import com.antgroup.openspg.server.common.model.exception.IllegalParamsException;
import com.antgroup.openspgapp.api.http.server.BaseController;
import com.antgroup.openspgapp.common.util.utils.LoginCacheHelper;
import com.antgroup.openspgapp.common.util.utils.LoginContextHelper;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping({"v1/accounts"})
@RestController
/* loaded from: com.antgroup.openspgapp-api-http-server-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/server/account/AccountController.class */
public class AccountController extends BaseController {

  @Autowired private PermissionManager permissionManager;
  private static final Pattern ACCOUNT_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{6,20}$");

  @GetMapping({"/"})
  @ResponseBody
  public HttpResult<Account> getAccount() {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Account>() { // from class:
          // com.antgroup.openspgapp.api.http.server.account.AccountController.1
          public void check() {}

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Account action() {
            Account account = AccountController.this.getLoginAccount();
            if (null == account || StringUtils.isBlank(account.getWorkNo())) {
              AssertUtils.assertParamStringIsNotBlank("account", (String) null);
            }
            return account;
          }
        });
  }

  @GetMapping({"/{queryStr}"})
  @ResponseBody
  public HttpResult<List<Account>> fuzzySearchAccounts(@PathVariable final String queryStr) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<List<Account>>() { // from class:
          // com.antgroup.openspgapp.api.http.server.account.AccountController.2
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("queryStr", queryStr);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public List<Account> action() {
            return AccountController.this.accountService.getAccountByKeyword(queryStr);
          }
        });
  }

  @GetMapping({"/list"})
  public HttpResult<Paged<Account>> getAccountList(
      final String account, final Integer page, final Integer size) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Paged<Account>>() { // from class:
          // com.antgroup.openspgapp.api.http.server.account.AccountController.3
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("page", page);
            AssertUtils.assertParamObjectIsNotNull("size", size);
            AssertUtils.assertParamIsTrue("page > 0", page.intValue() > 0);
            AssertUtils.assertParamIsTrue("size > 0", size.intValue() > 0);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Paged<Account> action() {
            return AccountController.this.accountService.getAccountList(account, page, size);
          }
        });
  }

  @PostMapping
  @ResponseBody
  public HttpResult<Integer> create(@RequestBody final Account request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Integer>() { // from class:
          // com.antgroup.openspgapp.api.http.server.account.AccountController.4
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("account", request);
            AssertUtils.assertParamStringIsNotBlank("account", request.getAccount());
            AssertUtils.assertParamStringIsNotBlank("password", request.getPassword());
            if (AccountController.ACCOUNT_PATTERN.matcher(request.getAccount()).matches()) {
              AccountController.this.assertCurrentUserIsSuper();
              Account account =
                  AccountController.this.accountService.getByUserNo(request.getAccount());
              if (account != null) {
                throw new IllegalParamsException("workNo already exists", new Object[0]);
              }
              return;
            }
            throw new IllegalParamsException(
                "account length is 6-20, only support letters,numbers and underscores",
                new Object[0]);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Integer action() {
            return AccountController.this.accountService.create(request);
          }
        });
  }

  @PostMapping({"/updatePassword"})
  @ResponseBody
  public HttpResult<Integer> updatePassword(@RequestBody final Account request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Integer>() { // from class:
          // com.antgroup.openspgapp.api.http.server.account.AccountController.5
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("account", request);
            if (StringUtils.isBlank(request.getWorkNo())) {
              request.setWorkNo(AccountController.this.getLoginAccount().getWorkNo());
            }
            AssertUtils.assertParamStringIsNotBlank("password", request.getPassword());
            AssertUtils.assertParamStringIsNotBlank(
                "confirmPassword", request.getConfirmPassword());
            if (!StringUtils.equals(request.getPassword(), request.getConfirmPassword())) {
              throw new IllegalParamsException("Inconsistent password input", new Object[0]);
            }
            Account account =
                AccountController.this.accountService.getWithPrivateByUserNo(request.getWorkNo());
            if (account == null) {
              throw new IllegalParamsException("account not exist", new Object[0]);
            }
            String password =
                AccountController.this.accountService.getSha256HexPassword(
                    request.getPassword(), account.getSalt());
            if (!StringUtils.equals(password, account.getPassword())) {
              AccountController.this.assertCurrentUserIsSuper();
              return;
            }
            throw new IllegalParamsException(
                "The new password cannot be the same as the old password", new Object[0]);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Integer action() {
            return AccountController.this.accountService.updatePassword(request);
          }
        });
  }

  @DeleteMapping({"/{workNo}"})
  @ResponseBody
  public HttpResult<Integer> delete(@PathVariable final String workNo) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Integer>() { // from class:
          // com.antgroup.openspgapp.api.http.server.account.AccountController.6
          public void check() {
            AssertUtils.assertParamStringIsNotBlank("workNo", workNo);
            AccountController.this.assertCurrentUserIsSuper();
            if (AccountController.this.permissionManager.isSuper(workNo)) {
              throw new IllegalParamsException(
                  "super administrator cannot be deleted", new Object[0]);
            }
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Integer action() {
            return AccountController.this.accountService.deleteAccount(workNo);
          }
        });
  }

  @GetMapping({"/getAccountByWorkNo"})
  public HttpResult<Account> getAccountByWorkNo(final String workNo) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Account>() { // from class:
          // com.antgroup.openspgapp.api.http.server.account.AccountController.7
          public void check() {
            AssertUtils.assertParamStringIsNotBlank("workNo", workNo);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Account action() {
            return AccountController.this.accountService.getByUserNo(workNo);
          }
        });
  }

  /* JADX INFO: Access modifiers changed from: private */
  public void assertCurrentUserIsSuper() {
    String userNo = getLoginAccount().getWorkNo();
    if (!this.permissionManager.isSuper(userNo)) {
      throw new IllegalParamsException("only super administrator operation", new Object[0]);
    }
  }

  @PostMapping({"/login"})
  public HttpResult<Boolean> login(
      final HttpServletRequest request,
      final HttpServletResponse response,
      @RequestBody final Account account) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() { // from class:
          // com.antgroup.openspgapp.api.http.server.account.AccountController.8
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("account", account);
            AssertUtils.assertParamStringIsNotBlank("account", account.getAccount());
            AssertUtils.assertParamStringIsNotBlank("password", account.getPassword());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Boolean action() {
            AccountController.this.cleanLoginInfo(request, response, account.getAccount());
            return Boolean.valueOf(AccountController.this.accountService.login(account, response));
          }
        });
  }

  @GetMapping({"/logout"})
  public HttpResult<String> logout(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final String redirectUrl) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<String>() { // from class:
          // com.antgroup.openspgapp.api.http.server.account.AccountController.9
          public void check() {}

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public String action() {
            String workNo = AccountController.this.getLoginAccount().getWorkNo();
            AccountController.this.cleanLoginInfo(request, response, workNo);
            return AccountController.this.accountService.logout(workNo, redirectUrl);
          }
        });
  }

  @PostMapping({"/updateUserConfig"})
  @ResponseBody
  public HttpResult<Integer> updateUserConfig(
      @RequestBody final Account account, final HttpServletRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Integer>() { // from class:
          // com.antgroup.openspgapp.api.http.server.account.AccountController.10
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("account", account);
            AssertUtils.assertParamStringIsNotBlank("config", account.getConfig());
            JSONObject configJSON = JSON.parseObject(account.getConfig());
            String useCurrentLanguage = configJSON.getString("useCurrentLanguage");
            if (StringUtils.isNotBlank(useCurrentLanguage)
                && LanguageEnum.getByCode(useCurrentLanguage) == null) {
              throw new IllegalParamsException("language is not supported", new Object[0]);
            }
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Integer action() {
            try {
              Account accountCache =
                  AccountController.this.accountService.getCurrentAccount(request.getCookies());
              AssertUtils.assertParamObjectIsNotNull("account", accountCache);
              accountCache.setConfig(account.getConfig());
              return Integer.valueOf(
                  AccountController.this.accountService.updateUserConfig(
                      accountCache, request.getCookies()));
            } catch (IOException e) {
              throw new SpgException(e, SpgMessageEnum.LOGIN_USER_NOT_LOGIN);
            }
          }
        });
  }

  /* JADX INFO: Access modifiers changed from: private */
  public void cleanLoginInfo(
      HttpServletRequest request, HttpServletResponse response, String workNo) {
    LoginContextHelper.clearUserInCtx();
    LoginCacheHelper.removeLocalLoginAccount(workNo);
    Cookie[] cookies = request.getCookies();
    if (ObjectUtils.isEmpty(cookies)) {
      return;
    }
    for (Cookie cookie : cookies) {
      cookie.setMaxAge(0);
      cookie.setPath("/");
      response.addCookie(cookie);
    }
  }
}
