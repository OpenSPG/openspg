package com.antgroup.openspgapp.api.http.server.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.exception.message.SpgMessageEnum;
import com.antgroup.openspg.server.api.http.client.account.AccountService;
import com.antgroup.openspg.server.biz.common.PermissionManager;
import com.antgroup.openspg.server.common.model.account.Account;
import com.antgroup.openspgapp.common.util.enums.CloudSiteEnum;
import com.antgroup.openspgapp.common.util.utils.LoginContextHelper;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Component
/* loaded from: com.antgroup.openspgapp-api-http-server-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/server/filter/AclFilter.class */
public class AclFilter implements Filter, Ordered {

  @Autowired private AccountService accountService;

  @Autowired private PermissionManager permissionManager;

  @Value("${web_request_url:http://antspg-gz00b-006001056016.sa128-sqa.alipay.net:8887}")
  private String webRequestUrl;

  @Value("${spring.application.name:antspg}")
  private String appName;

  @Value("${ant_buservice_pub_url:http://pubbuservice.stable.alipay.net}")
  private String pubUrl;

  @Value("${env:}")
  private String env;

  @Value("${noAuthCheckWhitelist:}")
  private String noAuthCheckWhitelist;

  private static final String SUCCESS = "success";
  private static final String MESSAGE = "errorMsg";
  private static final String CODE = "errorCode";
  private static final String URL = "url";
  private static final String CORS_ORIGIN = "Access-Control-Allow-Origin";
  private static final String CORS_METHODS = "Access-Control-Allow-Methods";
  private static final String CORS_ALLOW_METHODS = "POST, PUT, GET, DELETE";
  private static final String CORS_HEADERS = "Access-Control-Allow-Headers";
  private static final String CORS_ALLOW_HEADERS =
      "Origin, X-Requested-With, Content-Type, Accept, client_id, uuid, Authorization, credentials";
  private static final String CORS_CREDENTIALS = "Access-Control-Allow-Credentials";
  private static final String CORS_MAX_AGE = "Access-Control-Max-Age";
  private static final String CORS_ORIGIN_HEADER = "Origin";
  private static final String CORS_ALLOW_ALL = "*";
  private static final String CORS_ALLOW_CREDENTIALS = "true";
  private static final String CORS_MAX_AGE_VALUE = "3600";
  private static final String SUPER_PASSWORD_CHANGER = "/v1/accounts/updatePassword";
  private static final Logger log = LoggerFactory.getLogger(AclFilter.class);
  private static List<String> noAuthCheckPatterns = new ArrayList();

  @PostConstruct
  public void init() {
    if (StringUtils.isNotBlank(this.noAuthCheckWhitelist)) {
      noAuthCheckPatterns = Arrays.asList(this.noAuthCheckWhitelist.split(";"));
    }
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException {
    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      log.info(
          "HTTP Request - URL: {}, Method: {}, Params: {}",
          new Object[] {
            httpRequest.getRequestURI(),
            httpRequest.getMethod(),
            JSON.toJSONString(request.getParameterMap())
          });
    }
    Long start = Long.valueOf(System.currentTimeMillis());
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    String origin = httpServletRequest.getHeader(CORS_ORIGIN_HEADER);
    if (StringUtils.isNotBlank(origin)) {
      this.webRequestUrl = origin;
    }
    setCORS(httpServletRequest, httpServletResponse);
    Account currentAccount = this.accountService.getCurrentAccount(httpServletRequest.getCookies());
    String gotoUrl =
        String.format(
            "%s/pub/userNotLogin.htm?appName=%s&sourceUrl=%s",
            this.pubUrl,
            this.appName,
            URLEncoder.encode(this.webRequestUrl, StandardCharsets.UTF_8.name()));
    if (StringUtils.equals(CloudSiteEnum.PUBLIC.getValue(), this.env)) {
      gotoUrl = "/#/login";
    }
    log.info("aclFilter.gotoUrl:" + gotoUrl);
    try {
      if (HttpMethod.OPTIONS.name().equalsIgnoreCase(httpServletRequest.getMethod())) {
        httpServletResponse.setStatus(200);
        return;
      }
      if (currentAccount == null) {
        if (noNeedAuthCheck(httpServletRequest)) {
          filterChain.doFilter(httpServletRequest, httpServletResponse);
          return;
        } else {
          doStanderResponse(
              httpServletRequest,
              httpServletResponse,
              null,
              SpgMessageEnum.LOGIN_USER_NOT_LOGIN.getCode(),
              gotoUrl);
          return;
        }
      }
      if (StringUtils.equals(CloudSiteEnum.PUBLIC.getValue(), this.env)
          && superPasswordChanger(httpServletRequest, currentAccount)) {
        doStanderResponse(
            httpServletRequest,
            httpServletResponse,
            SpgMessageEnum.LOGIN_SUPER_PASSWORD_NOT_CHANGE.getMessage(),
            SpgMessageEnum.LOGIN_SUPER_PASSWORD_NOT_CHANGE.getCode(),
            gotoUrl + "?needResetPwd=true");
        return;
      }
      LoginContextHelper.putUserToCtx(currentAccount);
      filterChain.doFilter(request, response);
      log.info(
          "[{}:{}] cost:{}",
          new Object[] {
            httpServletRequest.getMethod(),
            httpServletRequest.getRequestURL(),
            Long.valueOf(System.currentTimeMillis() - start.longValue())
          });
    } catch (Exception e) {
      log.error("aclFilter Exception:{}", e.getMessage(), e);
      doStanderResponse(
          httpServletRequest,
          httpServletResponse,
          "aclFilter error" + e.getMessage(),
          SpgMessageEnum.LOGIN_USER_NOT_LOGIN.getCode(),
          gotoUrl);
    } finally {
      LoginContextHelper.clearUserInCtx();
    }
  }

  public int getOrder() {
    return 55000;
  }

  private boolean superPasswordChanger(HttpServletRequest request, Account account) {
    String uri = request.getRequestURI();
    AntPathMatcher matcher = new AntPathMatcher();
    if (matcher.match(SUPER_PASSWORD_CHANGER, uri)) {
      return false;
    }
    boolean isSuper = this.permissionManager.isSuper(account.getWorkNo());
    return isSuper && account.getGmtCreate().getTime() == account.getGmtModified().getTime();
  }

  private void doStanderResponse(
      HttpServletRequest request, HttpServletResponse response, String msg, String code, String url)
      throws IOException {
    if (response.isCommitted()) {
      log.warn("response is committed, skip response");
      return;
    }
    String outputCharset = request.getParameter("_output_charset");
    if (StringUtils.isNotBlank(outputCharset)) {
      response.setCharacterEncoding(outputCharset);
    } else {
      response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
    }
    response.setStatus(200);
    response.setContentType("application/json;charset=UTF-8");
    PrintWriter writer = response.getWriter();
    JSONObject tmpData = new JSONObject();
    tmpData.put(SUCCESS, false);
    tmpData.put(MESSAGE, msg);
    tmpData.put(CODE, code);
    tmpData.put(URL, url);
    String retStr = tmpData.toJSONString();
    writer.println(retStr);
    writer.flush();
  }

  private void setCORS(HttpServletRequest request, HttpServletResponse response) {
    String origin = request.getHeader(CORS_ORIGIN_HEADER);
    if (StringUtils.isNotBlank(origin)) {
      response.setHeader(CORS_ORIGIN, origin);
    } else {
      response.setHeader(CORS_ORIGIN, CORS_ALLOW_ALL);
    }
    response.setHeader(CORS_METHODS, CORS_ALLOW_METHODS);
    response.setHeader(CORS_HEADERS, CORS_ALLOW_HEADERS);
    response.setHeader(CORS_CREDENTIALS, CORS_ALLOW_CREDENTIALS);
    response.setHeader(CORS_MAX_AGE, CORS_MAX_AGE_VALUE);
  }

  private boolean noNeedAuthCheck(HttpServletRequest request) {
    String uri = request.getRequestURI();
    AntPathMatcher matcher = new AntPathMatcher();
    for (String pattern : noAuthCheckPatterns) {
      if (matcher.match(pattern, uri)) {
        log.info("noNeedAuthCheck [{}] match rule [{}]", uri, pattern);
        return true;
      }
    }
    return false;
  }
}
