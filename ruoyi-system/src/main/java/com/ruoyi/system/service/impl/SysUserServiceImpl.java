package com.ruoyi.system.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.ruoyi.common.base.BaseService;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.security.Md5Utils;
import com.ruoyi.system.domain.QSysUser;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.repository.SysRoleRepository;
import com.ruoyi.system.repository.SysUserRepository;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户 业务层处理
 *
 * @author ruoyi
 */
@Service
@CacheConfig(cacheNames = "sys_user")
public class SysUserServiceImpl extends BaseService implements ISysUserService {
    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysUserRepository sysUserRepository;
    @Autowired
    private ISysConfigService configService;
    @Autowired
    private SysRoleRepository sysRoleRepository;

    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @param pageRequest
     * @return 用户信息集合信息
     */
    @Cacheable
    @Override
    public Page<SysUser> selectUserList(SysUser user, Pageable pageRequest) {
        return sysUserRepository.findAll(getPredicate(user), pageRequest);
    }

    private com.querydsl.core.types.Predicate getPredicate(SysUser sysUser){
        QSysUser qSysUser = QSysUser.sysUser;
        List<com.querydsl.core.types.Predicate> predicates = new ArrayList<>();
        if(StringUtils.isNotEmpty(sysUser.getDelFlag())){
            predicates.add(qSysUser.delFlag.eq(sysUser.getDelFlag()));
        }
        if(StringUtils.isNotEmpty(sysUser.getLoginName())){
            predicates.add(buildLike(qSysUser.loginName, sysUser.getLoginName()));
        }
        if(StringUtils.isNotEmpty(sysUser.getStatus())){
            predicates.add(buildEqual(qSysUser.status, sysUser.getStatus()));
        }
        if(StringUtils.isNotEmpty(sysUser.getPhonenumber())){
            predicates.add(buildLike(qSysUser.phonenumber, sysUser.getPhonenumber()));
        }
        if(sysUser.getStartTime() != null){
            predicates.add(buildGreaterThanOrEqualTo(qSysUser.createTime, sysUser.getStartTime()));
        }
        if(sysUser.getEndTime() != null){
            predicates.add(buildLessThanOrEqualTo(qSysUser.createTime, sysUser.getEndTime()));
        }
        if(sysUser.getDept() != null && sysUser.getDept().getDeptId() != null){
            predicates.add(buildEqual(qSysUser.dept.deptId, sysUser.getDept().getDeptId()));
        }
        if(sysUser.getDept() != null && StringUtils.isNotEmpty(sysUser.getDept().getCode())){
            predicates.add(buildLike(qSysUser.dept.code, sysUser.getDept().getCode()));
        }
        return ExpressionUtils.allOf(predicates);
    }

    private Specification<SysUser> getSpecification(SysUser sysUser){
        return new Specification<SysUser>() {
            @Override
            public Predicate toPredicate(Root<SysUser> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(StringUtils.isNotEmpty(sysUser.getDelFlag())){
                    predicates.add(criteriaBuilder.equal(root.get("delFlag").as(String.class), sysUser.getDelFlag()));
                }
                if(StringUtils.isNotEmpty(sysUser.getLoginName())){
                    predicates.add(criteriaBuilder.like(root.get("loginName").as(String.class), "%" + sysUser.getLoginName() + "%"));
                }
                if(StringUtils.isNotEmpty(sysUser.getStatus())){
                    predicates.add(criteriaBuilder.equal(root.get("status").as(String.class), sysUser.getStatus()));
                }
                if(StringUtils.isNotEmpty(sysUser.getPhonenumber())){
                    predicates.add(criteriaBuilder.like(root.get("phonenumber").as(String.class), "%" + sysUser.getPhonenumber() + "%"));
                }
                if(sysUser.getStartTime() != null){
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime").as(Date.class), sysUser.getStartTime()));
                }
                if(sysUser.getEndTime() != null){
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endTime").as(Date.class), sysUser.getEndTime()));
                }
                if(sysUser.getDept() != null && sysUser.getDept().getDeptId() != null){
                    predicates.add(criteriaBuilder.equal(root.get("dept").get("deptId").as(Long.class), sysUser.getDept().getDeptId()));
                }
                if(sysUser.getDept() != null && StringUtils.isNotEmpty(sysUser.getDept().getCode())){
                    predicates.add(criteriaBuilder.equal(root.get("dept").get("code").as(String.class), "%" + sysUser.getDept().getCode() + "%"));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @param pageRequest
     * @return 用户信息集合信息
     */
    public Page<SysUser> selectAllocatedList(SysUser user, Pageable pageRequest) {
        return sysUserRepository.findAll(getSpecification(user), pageRequest);
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param sysUser 用户信息
     * @return 用户信息集合信息
     */
    public Page<SysUser> selectUnallocatedList(SysUser sysUser, Pageable pageable) {
        return sysUserRepository.findAll(new Specification<SysUser>() {
            @Override
            public Predicate toPredicate(Root<SysUser> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(StringUtils.isNotEmpty(sysUser.getLoginName())){
                    predicates.add(criteriaBuilder.like(root.get("loginName").as(String.class), "%" + sysUser.getLoginName() + "%"));
                }
                if(StringUtils.isNotEmpty(sysUser.getPhonenumber())){
                    predicates.add(criteriaBuilder.like(root.get("phonenumber").as(String.class), "%" + sysUser.getPhonenumber() + "%"));
                }
                if(sysUser.getRoles() != null && sysUser.getRoles().size() == 1){
                    SysRole role = sysUser.getRoles().iterator().next();
                    Predicate notMember = criteriaBuilder.isNotMember(role, root.get("roles").as(Set.class));
                    predicates.add(notMember);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Cacheable
    @Override
    public SysUser selectUserByLoginName(String userName) {
        return sysUserRepository.findFirstByDelFlagAndLoginName(BaseEntity.NOT_DELETED, userName);
    }

    /**
     * 通过手机号码查询用户
     *
     * @param phoneNumber 手机号码
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByPhoneNumber(String phoneNumber) {
        return sysUserRepository.findFirstByDelFlagAndPhonenumber(BaseEntity.NOT_DELETED, phoneNumber);
    }

    /**
     * 通过邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByEmail(String email) {
        return sysUserRepository.findFirstByDelFlagAndEmail(BaseEntity.NOT_DELETED, email);
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId) {
        return sysUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("无效的数据"));
    }

    @Override
    public SysUser selectUserWithRolesAndPostsById(Long userId) {
        return sysUserRepository.findSysUserByDelFlagAndUserId(BaseEntity.NOT_DELETED, userId);
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteUserById(Long userId) {
        sysUserRepository.deleteById(userId);
        return 1;
    }

    /**
     * 批量删除用户信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @CacheEvict(allEntries = true)
    @Transactional
    @Override
    public int deleteUserByIds(String ids) throws BusinessException {
        Long[] userIds = Convert.toLongArray(ids);
        for (Long userId : userIds) {
            checkUserAllowed(new SysUser(userId));
            deleteUserById(userId);
        }
        return userIds.length;
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public SysUser insertUser(SysUser user) {
        return sysUserRepository.save(user);
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public SysUser updateUser(SysUser user) {
        SysUser db = sysUserRepository.findById(user.getUserId()).get();
        BeanUtils.copyProperties(user, db, "delFlag", "loginDate", "loginIp", "salt", "password", "avatar");
        return sysUserRepository.save(db);
    }

    /**
     * 修改用户个人详细信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public SysUser updateUserInfo(SysUser user) {
        return sysUserRepository.save(user);
    }

    /**
     * 修改用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    @Transactional
    @Override
    public SysUser resetUserPwd(SysUser user) {
        SysUser db = sysUserRepository.findById(user.getUserId()).get();
        db.setSalt(user.getSalt());
        db.setPassword(user.getPassword());
        return db;
    }

    /**
     * 校验登录名称是否唯一
     *
     * @param loginName 用户名
     * @return
     */
    @Override
    public String checkLoginNameUnique(String loginName) {
        int count = sysUserRepository.countByLoginName(loginName);
        if (count > 0) {
            return UserConstants.USER_NAME_NOT_UNIQUE;
        }
        return UserConstants.USER_NAME_UNIQUE;
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkPhoneUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = sysUserRepository.findFirstByPhonenumber(user.getPhonenumber());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.USER_PHONE_NOT_UNIQUE;
        }
        return UserConstants.USER_PHONE_UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkEmailUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = sysUserRepository.findFirstByEmail(user.getEmail());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.USER_EMAIL_NOT_UNIQUE;
        }
        return UserConstants.USER_EMAIL_UNIQUE;
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    public void checkUserAllowed(SysUser user) {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw new BusinessException("不允许操作超级管理员用户");
        }
    }

    /**
     * 导入用户数据
     *
     * @param userList        用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(userList) || userList.size() == 0) {
            throw new BusinessException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        String password = configService.selectConfigByKey("sys.user.initPassword");
        for (SysUser user : userList) {
            try {
                // 验证是否存在这个用户
                SysUser u = sysUserRepository.findFirstByLoginName(user.getLoginName());
                if (StringUtils.isNull(u)) {
                    user.setPassword(Md5Utils.hash(user.getLoginName() + password));
                    user.setCreateBy(operName);
                    this.insertUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getLoginName() + " 导入成功");
                } else if (isUpdateSupport) {
                    user.setUpdateBy(operName);
                    this.updateUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getLoginName() + " 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、账号 " + user.getLoginName() + " 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + user.getLoginName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new BusinessException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    /**
     * 用户状态修改
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public void changeStatus(SysUser user) {
        sysUserRepository.changeStatus(user.getStatus(), user.getUserId());
    }

    public Set<SysDept> getUserRoleDepts(Long userId){
        SysUser user = sysUserRepository.findSysUserByDelFlagAndUserId(BaseEntity.NOT_DELETED, userId);
        Set<SysDept> depts = new HashSet<>();
        Set<SysRole> roles = user.getRoles();
        for(SysRole sysRole : roles){
            sysRole = sysRoleRepository.findByRoleId(sysRole.getRoleId());
            depts.addAll(sysRole.getDepts());
        }
        return depts;
    }

    @Cacheable(key = "#user.userId", unless = "#result == null ")
    @Override
    public SysUser registerUser(SysUser user) {
        user.setUserType(UserConstants.REGISTER_USER_TYPE);
        return sysUserRepository.save(user);
    }

    @Transactional
    @Override
    public void insertUserAuth(Long userId, Long[] roleIds) {
        SysUser sysUser = sysUserRepository.findById(userId).get();
        Set<SysRole> roles = Arrays.stream(roleIds)
                .map(roleId -> new SysRole(roleId))
                .collect(Collectors.toSet());
        sysUser.setRoles(roles);
        sysUserRepository.save(sysUser);
    }
}
