package com.antgroup.openspgapp.api.http.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.common.utils.StringUtil;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.api.http.client.account.AccountService;
import com.antgroup.openspg.server.biz.common.AccountManager;
import com.antgroup.openspg.server.common.model.account.Account;
import com.antgroup.openspg.server.common.model.exception.IllegalParamsException;
import com.antgroup.openspg.server.infra.dao.repository.common.convertor.AccountConvertor;
import com.antgroup.openspgapp.common.util.utils.LoginCacheHelper;
import com.antgroup.openspgapp.common.util.utils.LoginContextHelper;
import com.antgroup.openspgapp.common.util.utils.SimpleHttpClient;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(
    name = {"env"},
    havingValue = "ant")
@Service
/* loaded from: com.antgroup.openspgapp-api-http-client-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/client/AccountServiceAntImpl.class */
public class AccountServiceAntImpl implements AccountService {
  private static final Logger log = LoggerFactory.getLogger(AccountServiceAntImpl.class);

  @Value("${hrorg.masterdata.clientId:}")
  private String clientId;

  @Value("${hrorg.masterdata.secret:}")
  private String secret;

  @Value("${webgw.domain.url:}")
  private String webgwDomainUrl;

  @Value("${webgw.appId:}")
  private String webgwAppId;

  @Value("${webgw.webgwSecret:}")
  private String webgwSecret;

  @Value("${web_request_url:}")
  private String webRequestUrl;

  @Value("${spring.application.name:antspg}")
  private String appName;

  @Value("${ant_buservice_domain_url:}")
  private String domainUrl;

  @Value("${ant_buservice_domain_url:}")
  private String antbuserviceUrl;

  @Autowired private AccountManager accountManager;
  private static final String USER_SEARCH_URL =
      "/hrorg/com.alibaba.masterdata.client.service.Employee360Service/search";
  private static final String HEADER_WEBGW_APP_ID = "x-webgw-appId";
  private static final String HEADER_WEBGW_VERSION = "x-webgw-version";
  private static final String HEADER_WEBGW_SECRET = "x-webgw-secret";
  private static final String HR_SEARCHKEY = "searchKey";
  private static final String HR_CLIENTID = "clientId";
  private static final String HR_SECRET = "secret";
  private static final String WEBGW_VERSION = "2.0";

  public Account getLoginUser() {
    Account account;
    Object object = LoginContextHelper.getUserFromCtx();
    if (!(object instanceof Account) || (account = (Account) object) == null) {
      return null;
    }
    Account kgAccount = getByUserNo(account.getWorkNo());
    if (null == kgAccount && null != account && StringUtils.isNotBlank(account.getWorkNo())) {
      String salt = this.accountManager.createSalt();
      account.setSalt(salt);
      Long stamp = Long.valueOf(System.currentTimeMillis());
      String str = account.getWorkNo() + salt + stamp;
      String token = this.accountManager.createToken(str);
      account.setToken(token);
      this.accountManager.create(account);
    }
    return account;
  }

  public List<Account> getAccountByKeyword(String keyword) {
    if (StringUtils.isBlank(keyword)) {
      return null;
    }
    try {
      return getUserListByHrOrg(keyword);
    } catch (Exception e) {
      log.error("getAccountByKeyword exception: {}", e.getMessage());
      return null;
    }
  }

  public Account getByUserNo(String userNo) {
    return this.accountManager.getByUserNo(userNo);
  }

  public Account getWithPrivateByUserNo(String userNo) {
    return this.accountManager.getWithPrivateByUserNo(userNo);
  }

  public Integer create(Account account) {
    throw new IllegalParamsException("only support public cloud", new Object[0]);
  }

  public Integer updatePassword(Account account) {
    throw new IllegalParamsException("only support public cloud", new Object[0]);
  }

  public Integer deleteAccount(String workNo) {
    throw new IllegalParamsException("only support public cloud", new Object[0]);
  }

  public Paged<Account> getAccountList(String account, Integer page, Integer size) {
    throw new IllegalParamsException("only support public cloud", new Object[0]);
  }

  public String getSha256HexPassword(String password, String salt) {
    return this.accountManager.getSha256HexPassword(password, salt);
  }

  public Account getCurrentAccount(Cookie[] cookies) throws IOException {
    Account account;
    String iamToken = getCookieValue(cookies, "IAM_TOKEN");
    if (StringUtil.isNotBlank(iamToken)
        && (account = (Account) LoginCacheHelper.getLocalLoginAccount(iamToken)) != null) {
      return account;
    }
    List<String> requestCookie = getCookiesFromRequest(cookies);
    String cookie = getCookieParam(requestCookie);
    Map<String, String> headers = new HashMap<>();
    headers.put("Cookie", cookie);
    headers.put("Referer", this.webRequestUrl);
    headers.put("Content-Type", "application/json");
    String loginUrl = this.domainUrl + "/pub/getLoginUser.json?appName=" + this.appName;
    SimpleHttpClient.HttpResult httpResult =
        SimpleHttpClient.doGet(loginUrl, headers, (Map) null, StandardCharsets.UTF_8.name());
    if (httpResult.isOk()) {
      String data = httpResult.content;
      JSONObject jsonObject = JSONObject.parseObject(data);
      if (jsonObject.getBooleanValue("success")) {
        JSONObject userData = jsonObject.getJSONObject("data");
        Account account2 = new Account();
        account2.setWorkNo(userData.getString("outUserNo"));
        account2.setRealName(userData.getString("realName"));
        account2.setNickName(userData.getString("nickName"));
        account2.setAccount(userData.getString("operatorName"));
        account2.setEmail(userData.getString("email"));
        if (StringUtil.isNotBlank(iamToken)) {
          LoginCacheHelper.putLocalLoginAccount(iamToken, account2);
        }
        return account2;
      }
      if ("USER_NOT_LOGIN".equals(jsonObject.getString("buserviceErrorCode"))) {
        log.info("aclFilter account not login");
        return null;
      }
    }
    log.info("aclFilter.httpResult:{}", JSONObject.toJSONString(httpResult));
    return null;
  }

  public boolean login(Account account, HttpServletResponse response) {
    throw new IllegalParamsException("only support public cloud", new Object[0]);
  }

  public String logout(String workNo, String redirectUrl) {
    String originalUrl =
        this.antbuserviceUrl + "/pub/logout.htm?appName=" + this.appName + "&goto=gotoUrl";
    if (StringUtils.isNotBlank(redirectUrl)) {
      originalUrl = originalUrl.replace("gotoUrl", redirectUrl);
    }
    return originalUrl;
  }

  public int updateUserConfig(Account account, Cookie[] cookies) {
    String iamToken = getCookieValue(cookies, "IAM_TOKEN");
    if (StringUtil.isNotBlank(iamToken)) {
      LoginCacheHelper.putLocalLoginAccount(iamToken, account);
    }
    account.setUseCurrentLanguage(AccountConvertor.getUseCurrentLanguage(account.getConfig()));
    return this.accountManager.updateUserConfig(account.getWorkNo(), account.getConfig());
  }

  private List<Account> getUserListByHrOrg(String searchKey) throws Exception {
    List<Account> dataList = Lists.newArrayList();
    String url = this.webgwDomainUrl + USER_SEARCH_URL;
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json;charset=UTF-8");
    headers.put(HEADER_WEBGW_APP_ID, this.webgwAppId);
    headers.put(HEADER_WEBGW_VERSION, WEBGW_VERSION);
    headers.put(HEADER_WEBGW_SECRET, this.webgwSecret);
    Map<String, String> params = new HashMap<>();
    params.put(HR_SEARCHKEY, searchKey);
    params.put(HR_CLIENTID, this.clientId);
    params.put(HR_SECRET, this.secret);
    try {
      SimpleHttpClient.HttpResult result =
          SimpleHttpClient.doPost(
              url, headers, JSON.toJSONString(params), StandardCharsets.UTF_8.name());
      if (result.isOk()) {
        JSONObject jsonObject = JSON.parseObject(result.content);
        if (jsonObject.getBoolean("success").booleanValue()) {
          JSONArray data = jsonObject.getJSONArray("result");
          if (data == null || data.size() == 0) {
            log.info("getUserListByHrOrg:{}", result.content);
            return dataList;
          }
          for (int i = 0; i < data.size(); i++) {
            JSONObject userObj = data.getJSONObject(i);
            Account account = new Account();
            account.setRealName(userObj.getString("name"));
            account.setAccount(userObj.getString("loginAccount"));
            account.setNickName(userObj.getString("nickName"));
            account.setWorkNo(removeZeroPrefix(userObj.getString("workNo")));
            account.setEmail(userObj.getString("buMail"));
            dataList.add(account);
          }
        } else {
          log.info("getUserListByHrOrg fail:{}", result.content);
          throw new Exception("Hr search fail:" + result.content + "--" + searchKey);
        }
      }
      return dataList;
    } catch (Exception e) {
      log.info("getUserListByHrOrg exception:{}", e.getMessage(), e);
      throw new Exception("Hr search exception:", e);
    }
  }

  public static String removeZeroPrefix(String userNo) {
    if (StringUtils.isBlank(userNo)) {
      return userNo;
    }
    if (!userNo.startsWith("0")) {
      return userNo;
    }
    return userNo.replaceFirst("^0+", "");
  }

  private String getCookieValue(Cookie[] cookies, String key) {
    if (cookies != null && StringUtils.isNotBlank(key)) {
      for (Cookie cookie : cookies) {
        if (key.equalsIgnoreCase(cookie.getName())) {
          return cookie.getValue();
        }
      }
      return null;
    }
    return null;
  }

  private List<String> getCookiesFromRequest(Cookie[] cookies) {
    List<String> cookieList = new ArrayList<>();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        cookieList.add(cookie.getName() + "=" + cookie.getValue());
      }
    }
    return cookieList;
  }

  public static String getCookieParam(List<String> cookies) {
    StringBuilder cookieString = new StringBuilder();
    for (String cookie : cookies) {
      if (cookieString.length() > 0) {
        cookieString.append("; ");
      }
      cookieString.append(cookie);
    }
    return cookieString.toString();
  }
}
