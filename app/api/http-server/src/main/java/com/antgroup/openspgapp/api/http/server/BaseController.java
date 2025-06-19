package com.antgroup.openspgapp.api.http.server;

import com.antgroup.openspg.server.api.http.client.account.AccountService;
import com.antgroup.openspg.server.common.model.account.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
/* loaded from: com.antgroup.openspgapp-api-http-server-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/server/BaseController.class */
public class BaseController {

  @Autowired public AccountService accountService;

  public Account getLoginAccount() {
    return this.accountService.getLoginUser();
  }
}
