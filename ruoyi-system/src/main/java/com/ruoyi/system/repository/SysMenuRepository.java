package com.ruoyi.system.repository;

import com.ruoyi.common.base.BaseRepository;
import com.ruoyi.system.domain.SysMenu;
import com.ruoyi.system.domain.SysRole;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SysMenuRepository extends BaseRepository<SysMenu, Long> {

    List<SysMenu> findAllByMenuTypeInAndVisibleOrderByOrderNum(Collection<String> types, String visiable);

    List<SysMenu> findAllByRolesContaining(SysRole sysRole);

    int countByParent(SysMenu sysMenu);

    SysMenu findFirstByMenuNameAndParent(String menuName, SysMenu parent);
}
