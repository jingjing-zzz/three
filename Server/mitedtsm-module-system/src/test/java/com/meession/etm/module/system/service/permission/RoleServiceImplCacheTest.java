package com.meession.etm.module.system.service.permission;

import com.meession.etm.framework.common.enums.CommonStatusEnum;
import com.meession.etm.framework.test.core.ut.BaseDbAndRedisUnitTest;
import com.meession.etm.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.meession.etm.module.system.dal.dataobject.permission.RoleDO;
import com.meession.etm.module.system.dal.mysql.permission.RoleMapper;
import com.meession.etm.module.system.enums.permission.RoleTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.meession.etm.framework.test.core.util.RandomUtils.randomLongId;
import static com.meession.etm.framework.test.core.util.RandomUtils.randomPojo;
import static com.meession.etm.framework.test.core.util.RandomUtils.randomString;
import static org.junit.jupiter.api.Assertions.*;

@Import(RoleServiceImpl.class)
public class RoleServiceImplCacheTest extends BaseDbAndRedisUnitTest {

    @Resource
    private RoleServiceImpl roleService;

    @Resource
    private RoleMapper roleMapper;

    @MockitoBean
    private PermissionService permissionService;

    @Test
    public void testGetRoleFromCache() {
        RoleDO roleDO = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO);

        RoleDO result = roleService.getRoleFromCache(roleDO.getId());
        assertNotNull(result);
        assertEquals(roleDO.getId(), result.getId());
    }

    @Test
    public void testCacheEvictAfterUpdate() {
        RoleDO roleDO = randomPojo(RoleDO.class, o -> {
            o.setName("original_name");
            o.setType(RoleTypeEnum.CUSTOM.getType());
        });
        roleMapper.insert(roleDO);

        RoleDO cachedRole = roleService.getRoleFromCache(roleDO.getId());
        assertEquals("original_name", cachedRole.getName());

        RoleSaveReqVO updateReqVO = randomPojo(RoleSaveReqVO.class, o -> {
            o.setId(roleDO.getId());
            o.setName("updated_name");
        });
        roleService.updateRole(updateReqVO);

        RoleDO updatedRole = roleService.getRoleFromCache(roleDO.getId());
        assertEquals("updated_name", updatedRole.getName());
    }

    @Test
    public void testCacheEvictAfterDelete() {
        RoleDO roleDO = randomPojo(RoleDO.class, o -> o.setType(RoleTypeEnum.CUSTOM.getType()));
        roleMapper.insert(roleDO);

        RoleDO cachedRole = roleService.getRoleFromCache(roleDO.getId());
        assertNotNull(cachedRole);

        roleService.deleteRole(roleDO.getId());

        assertNull(roleService.getRole(roleDO.getId()));
    }
}