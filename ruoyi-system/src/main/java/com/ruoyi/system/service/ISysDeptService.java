package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.Ztree;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.domain.SysRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 部门管理 服务层
 *
 * @author ruoyi
 */
public interface ISysDeptService {
    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    public Page<SysDept> selectDeptList(SysDept dept, Pageable pageable);

    /**
     * 查询部门管理树
     *
     * @param dept 部门信息
     * @return 所有部门信息
     */
    public List<Ztree> selectDeptTree(SysDept dept);

    /**
     * 根据角色ID查询菜单
     *
     * @param role 角色对象
     * @return 菜单列表
     */
    public List<Ztree> roleDeptTreeData(SysRole role);

    /**
     * 查询下级部门数量
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public int countChildren(Long deptId);

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean checkDeptExistUser(Long deptId);

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public void deleteDeptById(Long deptId);

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    public SysDept insertDept(SysDept dept);

    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    public SysDept updateDept(SysDept dept);

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    public SysDept selectDeptById(Long deptId);

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    public String checkDeptNameUnique(SysDept dept);
}
