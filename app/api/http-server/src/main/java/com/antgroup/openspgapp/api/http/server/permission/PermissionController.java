package com.antgroup.openspgapp.api.http.server.permission;

import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.common.util.enums.PermissionEnum;
import com.antgroup.openspg.common.util.enums.ResourceTagEnum;
import com.antgroup.openspg.common.util.exception.SpgException;
import com.antgroup.openspg.common.util.exception.message.SpgMessageEnum;
import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.api.facade.dto.common.request.PermissionRequest;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.api.http.server.HttpResult;
import com.antgroup.openspg.server.biz.common.PermissionManager;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspg.server.common.model.account.Account;
import com.antgroup.openspg.server.common.model.exception.IllegalParamsException;
import com.antgroup.openspg.server.common.model.permission.Permission;
import com.antgroup.openspg.server.common.model.project.AccountRoleInfo;
import com.antgroup.openspgapp.api.http.server.BaseController;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping({"v1/permissions"})
@RestController
/* loaded from: com.antgroup.openspgapp-api-http-server-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/server/permission/PermissionController.class */
public class PermissionController extends BaseController {

  @Autowired private PermissionManager permissionManager;

  @GetMapping({"/{resourceTag}/id/{resourceId}"})
  @ResponseBody
  public HttpResult<Paged<Permission>> getPermissionList(
      @PathVariable final String resourceTag,
      @PathVariable final Long resourceId,
      final String roleType,
      final String queryStr,
      final Integer page,
      final Integer size) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Paged<Permission>>() { // from class:
          // com.antgroup.openspgapp.api.http.server.permission.PermissionController.1
          public void check() {
            AssertUtils.assertParamStringIsNotBlank("resourceTag", resourceTag);
            AssertUtils.assertParamObjectIsNotNull("resourceId", resourceId);
            AssertUtils.assertParamIsTrue("page > 0", page.intValue() > 0);
            AssertUtils.assertParamIsTrue("size > 0", size.intValue() > 0);
            if (StringUtils.equals(resourceTag, ResourceTagEnum.PROJECT.name())) {
              String userNo = PermissionController.this.getLoginAccount().getWorkNo();
              boolean isSuper = PermissionController.this.permissionManager.isSuper(userNo);
              boolean isProjectRole =
                  PermissionController.this.permissionManager.isProjectRole(userNo, resourceId);
              if (!isSuper && !isProjectRole) {
                List<String> userNameList =
                    PermissionController.this.permissionManager.getOwnerUserNameByProjectId(
                        resourceId);
                throw new SpgException(
                    SpgMessageEnum.PROJECT_MEMBER_NOT_EXIST.getCode(),
                    StringUtils.join(new List[] {userNameList}));
              }
            }
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Paged<Permission> action() {
            Paged<Permission> result =
                PermissionController.this.permissionManager.query(
                    queryStr, roleType, resourceId, resourceTag, page, size);
            return result;
          }
        });
  }

  @PostMapping
  @ResponseBody
  public HttpResult<Integer> create(@RequestBody final PermissionRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Integer>() { // from class:
          // com.antgroup.openspgapp.api.http.server.permission.PermissionController.2
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamIsTrue(
                "roleType",
                Arrays.asList(PermissionEnum.values())
                    .contains(PermissionEnum.valueOf(request.getRoleType())));
            PermissionController.this.assertIsSuperOrIsOwner(request.getResourceIds());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Integer action() {
            return PermissionController.this.permissionManager.create(request);
          }
        });
  }

  @PutMapping({"/{id}", "/"})
  @ResponseBody
  public HttpResult<Integer> update(
      @PathVariable(value = "id", required = false) final Long id,
      @RequestBody final PermissionRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Integer>() { // from class:
          // com.antgroup.openspgapp.api.http.server.permission.PermissionController.3
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamIsTrue(
                "roleType",
                Arrays.asList(PermissionEnum.values())
                    .contains(PermissionEnum.valueOf(request.getRoleType())));
            PermissionController.this.assertIsSuperOrIsOwner(request.getResourceIds());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Integer action() {
            if (null != id) {
              request.setId(id);
            }
            return PermissionController.this.permissionManager.update(request);
          }
        });
  }

  @DeleteMapping({"/{id}", "/"})
  @ResponseBody
  public HttpResult<Integer> delete(
      @PathVariable(value = "id", required = false) final Long id,
      @RequestBody final PermissionRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Integer>() { // from class:
          // com.antgroup.openspgapp.api.http.server.permission.PermissionController.4
          public void check() {
            PermissionController.this.assertIsSuperOrIsOwner(request.getResourceIds());
            Permission permission =
                PermissionController.this.permissionManager.selectByPrimaryKey(id);
            AssertUtils.assertParamObjectIsNotNull("permission", permission);
            if (null != id
                && StringUtils.equals(ResourceTagEnum.PROJECT.name(), permission.getResourceTag())
                && permission.getRoleId().equals(Long.valueOf(PermissionEnum.OWNER.getId()))) {
              List<Permission> ownerLists =
                  PermissionController.this.permissionManager.getPermissionByUserRolesAndId(
                      Lists.newArrayList(new Long[] {permission.getResourceId()}),
                      (String) null,
                      PermissionEnum.OWNER.name(),
                      ResourceTagEnum.PROJECT.name());
              Stream<Permission> stream = ownerLists.stream();
              Long l = id;
              List<Permission> surplusOwnerList =
                  (List)
                      stream
                          .filter(
                              owner -> {
                                return !owner.getId().equals(l);
                              })
                          .collect(Collectors.toList());
              if (CollectionUtils.isEmpty(surplusOwnerList)) {
                throw new IllegalParamsException("not delete last owner", new Object[0]);
              }
            }
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Integer action() {
            if (null != id) {
              request.setId(id);
            }
            Integer count = PermissionController.this.permissionManager.removePermission(request);
            return count;
          }
        });
  }

  @GetMapping({"/getPermissionList"})
  @ResponseBody
  public HttpResult<Map<String, Object>> getPermissionList(
      final String resourceTag, final Long resourceId, final String roleType) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Map<String, Object>>() { // from class:
          // com.antgroup.openspgapp.api.http.server.permission.PermissionController.5
          public void check() {}

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Map<String, Object> action() {
            Account loginAccount = PermissionController.this.getLoginAccount();
            Map<String, Object> map = new HashMap<>();
            Collection<? extends Permission> superPermissions =
                PermissionController.this.permissionManager.getPermissionByUserRolesAndId(
                    Lists.newArrayList(new Long[] {0L}),
                    loginAccount.getWorkNo(),
                    PermissionEnum.SUPER.name(),
                    ResourceTagEnum.PLATFORM.name());
            List<Permission> permissionList = Lists.newArrayList();
            if (StringUtils.isNotBlank(resourceTag)
                && !StringUtils.equals(resourceTag, PermissionEnum.SUPER.name())
                && resourceId != null) {
              permissionList =
                  PermissionController.this.permissionManager.getPermissionByUserRolesAndId(
                      Lists.newArrayList(new Long[] {resourceId}),
                      loginAccount.getWorkNo(),
                      roleType,
                      resourceTag);
            }
            if (CollectionUtils.isNotEmpty(superPermissions)) {
              permissionList.addAll(superPermissions);
            }
            if (CollectionUtils.isNotEmpty(permissionList)) {
              permissionList.forEach(
                  permission -> {
                    permission.setAccountRoleInfo(
                        new AccountRoleInfo(
                            PermissionEnum.getRoleTypeById(permission.getRoleId().longValue())
                                .name()));
                  });
            }
            map.put("permissionList", permissionList);
            return map;
          }
        });
  }

  public void assertIsSuperOrIsOwner(List<Long> resourceIds) {
    String userNo = getLoginAccount().getWorkNo();
    List<Permission> ownerPermissions =
        this.permissionManager.getPermissionByUserRolesAndId(
            resourceIds, userNo, PermissionEnum.OWNER.name(), ResourceTagEnum.PROJECT.name());
    boolean isSuper = this.permissionManager.isSuper(userNo);
    boolean isOwner =
        CollectionUtils.isNotEmpty(ownerPermissions)
            && ownerPermissions.size() == resourceIds.size();
    if (!isSuper && !isOwner) {
      throw new IllegalParamsException("permission denied", new Object[0]);
    }
  }
}
