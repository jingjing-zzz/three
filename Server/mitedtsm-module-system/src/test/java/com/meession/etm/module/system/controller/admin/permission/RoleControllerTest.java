package com.meession.etm.module.system.controller.admin.permission;

import com.meession.etm.framework.common.enums.CommonStatusEnum;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.test.core.ut.BaseDbUnitTest;
import com.meession.etm.module.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.meession.etm.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.meession.etm.module.system.dal.dataobject.permission.RoleDO;
import com.meession.etm.module.system.dal.mysql.permission.RoleMapper;
import com.meession.etm.module.system.enums.permission.RoleTypeEnum;
import com.meession.etm.module.system.service.permission.PermissionService;
import com.meession.etm.module.system.service.permission.RoleService;
import com.meession.etm.module.system.service.permission.RoleServiceImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.meession.etm.framework.test.core.util.AssertUtils.assertPojoEquals;
import static com.meession.etm.framework.test.core.util.AssertUtils.assertServiceException;
import static com.meession.etm.framework.test.core.util.RandomUtils.*;
import static com.meession.etm.module.system.enums.ErrorCodeConstants.*;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@Import({RoleController.class, RoleServiceImpl.class})
public class RoleControllerTest extends BaseDbUnitTest {

    @Resource
    private RoleController roleController;

    @Resource
    private RoleService roleService;

    @Resource
    private RoleMapper roleMapper;

    @MockitoBean
    private PermissionService permissionService;

    @Test
    public void testCreateRole() {
        RoleSaveReqVO reqVO = randomPojo(RoleSaveReqVO.class)
                .setId(null)
                .setStatus(randomCommonStatus());

        Long roleId = roleController.createRole(reqVO).getData();

        RoleDO roleDO = roleMapper.selectById(roleId);
        assertPojoEquals(reqVO, roleDO, "id");
        assertEquals(RoleTypeEnum.CUSTOM.getType(), roleDO.getType());
    }

    @Test
    public void testUpdateRole() {
        RoleDO roleDO = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO);

        RoleSaveReqVO reqVO = randomPojo(RoleSaveReqVO.class, o -> o.setId(roleDO.getId()));

        roleController.updateRole(reqVO);

        RoleDO dbRoleDO = roleMapper.selectById(roleDO.getId());
        assertEquals(reqVO.getName(), dbRoleDO.getName());
    }

    @Test
    public void testDeleteRole() {
        RoleDO roleDO = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO);

        roleController.deleteRole(roleDO.getId());

        assertNull(roleMapper.selectById(roleDO.getId()));
        verify(permissionService).processRoleDeleted(roleDO.getId());
    }

    @Test
    public void testDeleteRoleList() {
        RoleDO roleDO1 = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO1);
        RoleDO roleDO2 = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO2);

        roleController.deleteRoleList(List.of(roleDO1.getId(), roleDO2.getId()));

        assertNull(roleMapper.selectById(roleDO1.getId()));
        assertNull(roleMapper.selectById(roleDO2.getId()));
    }

    @Test
    public void testGetRole() {
        RoleDO roleDO = randomPojo(RoleDO.class);
        roleMapper.insert(roleDO);

        var result = roleController.getRole(roleDO.getId());

        assertNotNull(result.getData());
        assertEquals(roleDO.getName(), result.getData().getName());
    }

    @Test
    public void testGetRolePage() {
        RoleDO roleDO = randomPojo(RoleDO.class);
        roleMapper.insert(roleDO);

        RolePageReqVO reqVO = new RolePageReqVO();

        var result = roleController.getRolePage(reqVO);

        assertNotNull(result.getData());
        assertTrue(result.getData().getTotal() >= 1);
    }

    @Test
    public void testGetSimpleRoleList() {
        RoleDO roleDO = randomPojo(RoleDO.class, o -> o.setStatus(CommonStatusEnum.ENABLE.getStatus()));
        roleMapper.insert(roleDO);

        var result = roleController.getSimpleRoleList();

        assertNotNull(result.getData());
        assertTrue(result.getData().size() >= 1);
    }
}