package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 菜单权限表 sys_menu
 *
 * @author ruoyi
 */
@Entity
@Table(name = "sys_menu")
public class SysMenu extends BaseEntity {
    private static final long serialVersionUID = 1L;

    public static final String MENU_VISIABLE = "0";
    public static final String MENU_INVISIABLE = "1";
    public static final String MENU_TYPE_PRIMARY = "M";
    public static final String MENU_TYPE_SECONDARY = "C";
    public static final String MENU_TYPE_PERMS = "F";
    public static final Long ROOT_ID = null;

    /**
     * 菜单ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 显示顺序
     */
    private String orderNum;

    /**
     * 菜单URL
     */
    private String url;

    /**
     * 打开方式：menuItem页签 menuBlank新窗口
     */
    private String target;

    /**
     * 类型:0目录,1菜单,2按钮
     */
    private String menuType;

    /**
     * 菜单状态:0显示,1隐藏
     */
    private String visible;

    /**
     * 权限字符串
     */
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    @ManyToOne
    @JoinColumn(name = "parentId", referencedColumnName = "menuId")
    @org.hibernate.annotations.ForeignKey(name = "none")
    private SysMenu parent;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "menus")
    @org.hibernate.annotations.ForeignKey(name = "none")
    private Set<SysRole> roles;

    /**
     * 子菜单
     */
    @Transient
    private List<SysMenu> children = new ArrayList<SysMenu>();

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    @NotBlank(message = "菜单名称不能为空")
    @Size(min = 0, max = 50, message = "菜单名称长度不能超过50个字符")
    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    @Transient
    public Long getParentId() {
        if(parent != null){
            return parent.getMenuId();
        }
        return null;
    }

    @NotBlank(message = "显示顺序不能为空")
    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    @Size(min = 0, max = 200, message = "请求地址不能超过200个字符")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @NotBlank(message = "菜单类型不能为空")
    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    @Size(min = 0, max = 100, message = "权限标识长度不能超过100个字符")
    public String getPerms() {
        return perms;
    }

    public void setPerms(String perms) {
        this.perms = perms;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<SysMenu> getChildren() {
        return children;
    }

    public void setChildren(List<SysMenu> children) {
        this.children = children;
    }

    public SysMenu getParent() {
        return parent;
    }

    public void setParent(SysMenu parent) {
        this.parent = parent;
    }

    public Set<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<SysRole> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("menuId", getMenuId())
                .append("menuName", getMenuName())
                .append("parentId", getParentId())
                .append("orderNum", getOrderNum())
                .append("url", getUrl())
                .append("target", getTarget())
                .append("menuType", getMenuType())
                .append("visible", getVisible())
                .append("perms", getPerms())
                .append("icon", getIcon())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }

    public SysMenu(Long menuId) {
        this.menuId = menuId;
    }

    public SysMenu() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SysMenu sysMenu = (SysMenu) o;
        return Objects.equals(menuId, sysMenu.menuId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuId);
    }
}
