package com.meession.etm.module.system.service.permission;

import cn.hutool.extra.spring.SpringUtil;
import com.meession.etm.framework.common.enums.CommonStatusEnum;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.test.core.ut.BaseDbUnitTest;
import com.meession.etm.module.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.meession.etm.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.meession.etm.module.system.dal.dataobject.permission.RoleDO;
import com.meession.etm.module.system.dal.mysql.permission.RoleMapper;
import com.meession.etm.module.system.enums.permission.DataScopeEnum;
import com.meession.etm.module.system.enums.permission.RoleTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.meession.etm.framework.common.util.date.LocalDateTimeUtils.buildBetweenTime;
import static com.meession.etm.framework.common.util.date.LocalDateTimeUtils.buildTime;
import static com.meession.etm.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static com.meession.etm.framework.test.core.util.AssertUtils.assertPojoEquals;
import static com.meession.etm.framework.test.core.util.AssertUtils.assertServiceException;
import static com.meession.etm.framework.test.core.util.RandomUtils.*;
import static com.meession.etm.module.system.enums.ErrorCodeConstants.*;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@Import(RoleServiceImpl.class)
public class RoleServiceImplTest extends BaseDbUnitTest {

    @Resource
    private RoleServiceImpl roleService;

    @Resource
    private RoleMapper roleMapper;

    @MockitoBean
    private PermissionService permissionService;

    @Test
    public void testCreateRole() {
        // 准备参数
        RoleSaveReqVO reqVO = randomPojo(RoleSaveReqVO.class)
                .setId(null)  // 防止 id 被赋值
                .setStatus(randomCommonStatus());

        // 调用
        Long roleId = roleService.createRole(reqVO, null);
        // 断言
        RoleDO roleDO = roleMapper.selectById(roleId);
        assertPojoEquals(reqVO, roleDO, "id");
        assertEquals(RoleTypeEnum.CUSTOM.getType(), roleDO.getType());
        assertEquals(DataScopeEnum.ALL.getScope(), roleDO.getDataScope());
    }

    @Test
    public void testCreateRole_superAdminForbidden() {
        RoleSaveReqVO reqVO = randomPojo(RoleSaveReqVO.class)
                .setId(null)
                .setCode("super_admin")
                .setStatus(randomCommonStatus());

        assertServiceException(() -> roleService.createRole(reqVO, null), ROLE_ADMIN_CODE_ERROR, "super_admin");
    }

    @Test
    public void testUpdateRole() {
        // mock 数据
        RoleDO roleDO = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO);
        // 准备参数
        Long id = roleDO.getId();
        RoleSaveReqVO reqVO = randomPojo(RoleSaveReqVO.class, o -> o.setId(id)
                .setStatus(randomCommonStatus()));

        // 调用
        roleService.updateRole(reqVO);
        // 断言
        RoleDO newRoleDO = roleMapper.selectById(id);
        assertPojoEquals(reqVO, newRoleDO);
    }

    @Test
    public void testUpdateRoleDataScope() {
        // mock 数据
        RoleDO roleDO = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO);
        // 准备参数
        Long id = roleDO.getId();
        Integer dataScope = randomEle(DataScopeEnum.values()).getScope();
        Set<Long> dataScopeRoleIds = randomSet(Long.class);

        // 调用
        roleService.updateRoleDataScope(id, dataScope, dataScopeRoleIds);
        // 断言
        RoleDO dbRoleDO = roleMapper.selectById(id);
        assertEquals(dataScope, dbRoleDO.getDataScope());
        assertEquals(dataScopeRoleIds, dbRoleDO.getDataScopeDeptIds());
    }

    @Test
    public void testDeleteRole() {
        // mock 数据
        RoleDO roleDO = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO);
        // 参数准备
        Long id = roleDO.getId();

        // 调用
        roleService.deleteRole(id);
        // 断言
        assertNull(roleMapper.selectById(id));
        // verify 删除相关数据
        verify(permissionService).processRoleDeleted(id);
    }

    @Test
    public void testDeleteRole_notExist() {
        assertServiceException(() -> roleService.deleteRole(randomLongId()), ROLE_NOT_EXISTS);
    }

    @Test
    public void testDeleteRole_systemRoleForbidden() {
        RoleDO roleDO = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.SYSTEM.getType()));
        roleMapper.insert(roleDO);

        assertServiceException(() -> roleService.deleteRole(roleDO.getId()),
                ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE);
    }

    @Test
    public void testDeleteRoleList_normal() {
        RoleDO roleDO1 = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO1);
        RoleDO roleDO2 = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO2);

        roleService.deleteRoleList(List.of(roleDO1.getId(), roleDO2.getId()));

        assertNull(roleMapper.selectById(roleDO1.getId()));
        assertNull(roleMapper.selectById(roleDO2.getId()));
        verify(permissionService).processRoleDeleted(roleDO1.getId());
        verify(permissionService).processRoleDeleted(roleDO2.getId());
    }

    @Test
    public void testDeleteRoleList_containsNotExist() {
        RoleDO roleDO = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO);

        assertServiceException(() -> roleService.deleteRoleList(List.of(roleDO.getId(), randomLongId())),
                ROLE_NOT_EXISTS);
    }

    @Test
    public void testDeleteRoleList_containsSystemRole() {
        RoleDO customRole = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(customRole);
        RoleDO systemRole = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.SYSTEM.getType()));
        roleMapper.insert(systemRole);

        assertServiceException(() -> roleService.deleteRoleList(List.of(customRole.getId(), systemRole.getId())),
                ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE);
    }

    @Test
    public void testDeleteRoleList_emptyList() {
        roleService.deleteRoleList(Collections.emptyList());
    }

    @Test
    public void testValidateRoleDuplicate_success() {
        // 调用，不会抛异常
        roleService.validateRoleDuplicate(randomString(), randomString(), null);
    }

    @Test
    public void testValidateRoleDuplicate_nameDuplicate() {
        // mock 数据
        RoleDO roleDO = randomPojo(RoleDO.class, o -> o.setName("role_name"));
        roleMapper.insert(roleDO);
        // 准备参数
        String name = "role_name";

        // 调用，并断言异常
        assertServiceException(() -> roleService.validateRoleDuplicate(name, randomString(), null),
                ROLE_NAME_DUPLICATE, name);
    }

    @Test
    public void testValidateRoleDuplicate_codeDuplicate() {
        // mock 数据
        RoleDO roleDO = randomPojo(RoleDO.class, o -> o.setCode("code"));
        roleMapper.insert(roleDO);
        // 准备参数
        String code = "code";

        // 调用，并断言异常
        assertServiceException(() -> roleService.validateRoleDuplicate(randomString(), code, null),
                ROLE_CODE_DUPLICATE, code);
    }

    @Test
    public void testValidateUpdateRole_success() {
        RoleDO roleDO = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO);
        // 准备参数
        Long id = roleDO.getId();

        // 调用，无异常
        roleService.validateRoleForUpdate(id);
    }

    @Test
    public void testValidateUpdateRole_roleIdNotExist() {
        assertServiceException(() -> roleService.validateRoleForUpdate(randomLongId()), ROLE_NOT_EXISTS);
    }

    @Test
    public void testValidateUpdateRole_systemRoleCanNotBeUpdate() {
        RoleDO roleDO = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.SYSTEM.getType()));
        roleMapper.insert(roleDO);
        // 准备参数
        Long id = roleDO.getId();

        assertServiceException(() -> roleService.validateRoleForUpdate(id),
                ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE);
    }

    @Test
    public void testGetRole() {
        // mock 数据
        RoleDO roleDO = randomPojo(RoleDO.class);
        roleMapper.insert(roleDO);
        // 参数准备
        Long id = roleDO.getId();

        // 调用
        RoleDO dbRoleDO = roleService.getRole(id);
        // 断言
        assertPojoEquals(roleDO, dbRoleDO);
    }

    @Test
    public void testGetRoleFromCache() {
        // mock 数据（缓存）
        RoleDO roleDO = randomPojo(RoleDO.class);
        roleMapper.insert(roleDO);
        // 参数准备
        Long id = roleDO.getId();

        // 调用
        RoleDO dbRoleDO = roleService.getRoleFromCache(id);
        // 断言
        assertPojoEquals(roleDO, dbRoleDO);
    }

    @Test
    public void testGetRoleListByStatus() {
        // mock 数据
        RoleDO dbRole01 = randomPojo(RoleDO.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        roleMapper.insert(dbRole01);
        RoleDO dbRole02 = randomPojo(RoleDO.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus()));
        roleMapper.insert(dbRole02);

        // 调用
        List<RoleDO> list = roleService.getRoleListByStatus(
                singleton(CommonStatusEnum.ENABLE.getStatus()));
        // 断言
        assertEquals(1, list.size());
        assertPojoEquals(dbRole01, list.get(0));
    }

    @Test
    public void testGetRoleList() {
        // mock 数据
        RoleDO dbRole01 = randomPojo(RoleDO.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        roleMapper.insert(dbRole01);
        RoleDO dbRole02 = randomPojo(RoleDO.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus()));
        roleMapper.insert(dbRole02);

        // 调用
        List<RoleDO> list = roleService.getRoleList();
        // 断言
        assertEquals(2, list.size());
        assertPojoEquals(dbRole01, list.get(0));
        assertPojoEquals(dbRole02, list.get(1));
    }

    @Test
    public void testGetRoleList_ids() {
        // mock 数据
        RoleDO dbRole01 = randomPojo(RoleDO.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        roleMapper.insert(dbRole01);
        RoleDO dbRole02 = randomPojo(RoleDO.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus()));
        roleMapper.insert(dbRole02);
        // 准备参数
        Collection<Long> ids = singleton(dbRole01.getId());

        // 调用
        List<RoleDO> list = roleService.getRoleList(ids);
        // 断言
        assertEquals(1, list.size());
        assertPojoEquals(dbRole01, list.get(0));
    }

    @Test
    public void testGetRoleListFromCache() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(RoleServiceImpl.class)))
                    .thenReturn(roleService);

            // mock 数据
            RoleDO dbRole = randomPojo(RoleDO.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
            roleMapper.insert(dbRole);
            // 测试 id 不匹配
            roleMapper.insert(cloneIgnoreId(dbRole, o -> {}));
            // 准备参数
            Collection<Long> ids = singleton(dbRole.getId());

            // 调用
            List<RoleDO> list = roleService.getRoleListFromCache(ids);
            // 断言
            assertEquals(1, list.size());
            assertPojoEquals(dbRole, list.get(0));
        }
    }

    @Test
    public void testGetRolePage() {
        // mock 数据
        RoleDO dbRole = randomPojo(RoleDO.class, o -> { // 等会查询到
            o.setName("土豆");
            o.setCode("tudou");
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setCreateTime(buildTime(2022, 2, 8));
        });
        roleMapper.insert(dbRole);
        // 测试 name 不匹配
        roleMapper.insert(cloneIgnoreId(dbRole, o -> o.setName("红薯")));
        // 测试 code 不匹配
        roleMapper.insert(cloneIgnoreId(dbRole, o -> o.setCode("hong")));
        // 测试 createTime 不匹配
        roleMapper.insert(cloneIgnoreId(dbRole, o -> o.setCreateTime(buildTime(2022, 2, 16))));
        // 准备参数
        RolePageReqVO reqVO = new RolePageReqVO();
        reqVO.setName("土豆");
        reqVO.setCode("tu");
        reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        reqVO.setCreateTime(buildBetweenTime(2022, 2, 1, 2022, 2, 12));

        // 调用
        PageResult<RoleDO> pageResult = roleService.getRolePage(reqVO);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbRole, pageResult.getList().get(0));
    }

    @Test
    public void testHasAnySuperAdmin_true() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(RoleServiceImpl.class)))
                    .thenReturn(roleService);

            // mock 数据
            RoleDO dbRole = randomPojo(RoleDO.class).setCode("super_admin");
            roleMapper.insert(dbRole);
            // 准备参数
            Long id = dbRole.getId();

            // 调用，并调用
            assertTrue(roleService.hasAnySuperAdmin(singletonList(id)));
        }
    }

    @Test
    public void testHasAnySuperAdmin_false() {
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(RoleServiceImpl.class)))
                    .thenReturn(roleService);

            // mock 数据
            RoleDO dbRole = randomPojo(RoleDO.class).setCode("tenant_admin");
            roleMapper.insert(dbRole);
            // 准备参数
            Long id = dbRole.getId();

            // 调用，并调用
            assertFalse(roleService.hasAnySuperAdmin(singletonList(id)));
        }
    }

    @Test
    public void testValidateRoleList_success() {
        // mock 数据
        RoleDO roleDO = randomPojo(RoleDO.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        roleMapper.insert(roleDO);
        // 准备参数
        List<Long> ids = singletonList(roleDO.getId());

        // 调用，无需断言
        roleService.validateRoleList(ids);
    }

    @Test
    public void testValidateRoleList_notFound() {
        // 准备参数
        List<Long> ids = singletonList(randomLongId());

        // 调用, 并断言异常
        assertServiceException(() -> roleService.validateRoleList(ids), ROLE_NOT_EXISTS);
    }

    @Test
    public void testValidateRoleList_notEnable() {
        // mock 数据
        RoleDO RoleDO = randomPojo(RoleDO.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus()));
        roleMapper.insert(RoleDO);
        // 准备参数
        List<Long> ids = singletonList(RoleDO.getId());

        // 调用, 并断言异常
        assertServiceException(() -> roleService.validateRoleList(ids), ROLE_IS_DISABLE, RoleDO.getName());
    }

    @Test
    public void testUpdateRole_nameDuplicate() {
        RoleDO existingRole = randomPojo(RoleDO.class, o -> {
            o.setName("existing_role");
            o.setType(RoleTypeEnum.CUSTOM.getType());
        });
        roleMapper.insert(existingRole);

        RoleDO updateRole = randomPojo(RoleDO.class, o -> {
            o.setName("other_role");
            o.setType(RoleTypeEnum.CUSTOM.getType());
        });
        roleMapper.insert(updateRole);

        RoleSaveReqVO reqVO = randomPojo(RoleSaveReqVO.class, o -> {
            o.setId(updateRole.getId());
            o.setName("existing_role");
        });

        assertServiceException(() -> roleService.updateRole(reqVO), ROLE_NAME_DUPLICATE, "existing_role");
    }

    @Test
    public void testUpdateRole_codeDuplicate() {
        RoleDO existingRole = randomPojo(RoleDO.class, o -> {
            o.setCode("existing_code");
            o.setType(RoleTypeEnum.CUSTOM.getType());
        });
        roleMapper.insert(existingRole);

        RoleDO updateRole = randomPojo(RoleDO.class, o -> {
            o.setCode("other_code");
            o.setType(RoleTypeEnum.CUSTOM.getType());
        });
        roleMapper.insert(updateRole);

        RoleSaveReqVO reqVO = randomPojo(RoleSaveReqVO.class, o -> {
            o.setId(updateRole.getId());
            o.setCode("existing_code");
        });

        assertServiceException(() -> roleService.updateRole(reqVO), ROLE_CODE_DUPLICATE, "existing_code");
    }

    @Test
    public void testUpdateRoleDataScope_selfDept() {
        RoleDO roleDO = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO);

        roleService.updateRoleDataScope(roleDO.getId(), DataScopeEnum.SELF.getScope(), null);

        RoleDO dbRoleDO = roleMapper.selectById(roleDO.getId());
        assertEquals(DataScopeEnum.SELF.getScope(), dbRoleDO.getDataScope());
    }

    @Test
    public void testUpdateRoleDataScope_deptCustom() {
        RoleDO roleDO = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO);

        Set<Long> deptIds = Set.of(1L, 2L, 3L);
        roleService.updateRoleDataScope(roleDO.getId(), DataScopeEnum.DEPT_CUSTOM.getScope(), deptIds);

        RoleDO dbRoleDO = roleMapper.selectById(roleDO.getId());
        assertEquals(DataScopeEnum.DEPT_CUSTOM.getScope(), dbRoleDO.getDataScope());
        assertEquals(deptIds, dbRoleDO.getDataScopeDeptIds());
    }

    @Test
    public void testUpdateRoleDataScope_notExist() {
        assertServiceException(() -> roleService.updateRoleDataScope(randomLongId(),
                DataScopeEnum.ALL.getScope(), null), ROLE_NOT_EXISTS);
    }

    @Test
    public void testUpdateRoleDataScope_systemRoleForbidden() {
        RoleDO roleDO = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.SYSTEM.getType()));
        roleMapper.insert(roleDO);

        assertServiceException(() -> roleService.updateRoleDataScope(roleDO.getId(),
                DataScopeEnum.ALL.getScope(), null), ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE);
    }

    @Test
    public void testUpdateRole_notExist() {
        RoleSaveReqVO reqVO = randomPojo(RoleSaveReqVO.class, o -> o.setId(randomLongId()));
        assertServiceException(() -> roleService.updateRole(reqVO), ROLE_NOT_EXISTS);
    }

    @Test
    public void testUpdateRole_systemRoleForbidden() {
        RoleDO roleDO = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.SYSTEM.getType()));
        roleMapper.insert(roleDO);

        RoleSaveReqVO reqVO = randomPojo(RoleSaveReqVO.class, o -> o.setId(roleDO.getId()));
        assertServiceException(() -> roleService.updateRole(reqVO), ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE);
    }

    @Test
    public void testGetRolePage_filterByName() {
        RoleDO role1 = randomPojo(RoleDO.class, o -> o.setName("test_role_1"));
        roleMapper.insert(role1);
        RoleDO role2 = randomPojo(RoleDO.class, o -> o.setName("other_role"));
        roleMapper.insert(role2);

        RolePageReqVO reqVO = new RolePageReqVO();
        reqVO.setName("test_role");

        PageResult<RoleDO> pageResult = roleService.getRolePage(reqVO);
        assertEquals(1, pageResult.getTotal());
        assertEquals("test_role_1", pageResult.getList().get(0).getName());
    }

    @Test
    public void testGetRolePage_filterByStatus() {
        RoleDO role1 = randomPojo(RoleDO.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        roleMapper.insert(role1);
        RoleDO role2 = randomPojo(RoleDO.class, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus()));
        roleMapper.insert(role2);

        RolePageReqVO reqVO = new RolePageReqVO();
        reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());

        PageResult<RoleDO> pageResult = roleService.getRolePage(reqVO);
        assertEquals(1, pageResult.getTotal());
        assertEquals(CommonStatusEnum.ENABLE.getStatus(), pageResult.getList().get(0).getStatus());
    }
}
