package com.antgroup.openspg.server.infra.dao.repository.common.convertor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.server.common.model.account.Account;
import com.antgroup.openspg.server.infra.dao.dataobject.AccountDO;
import com.antgroup.openspgapp.common.util.enums.LanguageEnum;
import com.antgroup.openspgapp.common.util.utils.SpgAppConstant;

public class AccountConvertor {

  public static AccountDO toDO(Account account) {
    if (null == account) {
      return null;
    }
    AccountDO accountDO = new AccountDO();
    accountDO.setId(account.getId());
    accountDO.setUserNo(account.getWorkNo());
    accountDO.setToken(account.getToken());
    accountDO.setDomainAccount(account.getAccount());
    accountDO.setSalt(account.getSalt());
    accountDO.setRealName(account.getRealName());
    accountDO.setNickName(account.getNickName());
    accountDO.setDwAccessKey(account.getPassword());
    accountDO.setEmail(account.getEmail());
    return accountDO;
  }

  public static Account toModel(AccountDO accountDO) {
    return new Account(
        accountDO.getId(),
        accountDO.getUserNo(),
        accountDO.getRealName(),
        accountDO.getNickName(),
        accountDO.getDomainAccount(),
        accountDO.getEmail(),
        accountDO.getGmtCreate(),
        accountDO.getGmtModified(),
        accountDO.getConfig(),
        getUseCurrentLanguage(accountDO.getConfig()));
  }

  public static Account toModelWithPrivate(AccountDO accountDO) {
    Account account = new Account();
    account.setId(accountDO.getId());
    account.setWorkNo(accountDO.getUserNo());
    account.setToken(accountDO.getToken());
    account.setSalt(accountDO.getSalt());
    account.setRealName(accountDO.getRealName());
    account.setNickName(accountDO.getNickName());
    account.setAccount(accountDO.getDomainAccount());
    account.setPassword(accountDO.getDwAccessKey());
    account.setEmail(accountDO.getEmail());
    account.setGmtCreate(accountDO.getGmtCreate());
    account.setGmtModified(accountDO.getGmtModified());
    account.setUseCurrentLanguage(getUseCurrentLanguage(accountDO.getConfig()));
    return account;
  }

  public static String getUseCurrentLanguage(String config) {
    if (StringUtils.isBlank(config)) {
      return LanguageEnum.ZH.getCode();
    }
    JSONObject jsonObject = JSON.parseObject(config);
    String useCurrentLanguage = jsonObject.getString(SpgAppConstant.USE_CURRENT_LANGUAGE);
    if (StringUtils.isNotBlank(useCurrentLanguage)) {
      return useCurrentLanguage;
    } else {
      return LanguageEnum.ZH.getCode();
    }
  }
}
