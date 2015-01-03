package com.storemanager.models;

import com.storemanager.dao.impl.MenuDAOImpl;
import com.storemanager.dao.impl.RoleDAOImpl;
import com.storemanager.util.DAOException;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "xref_menu_role")
public class XrefMenuRole {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    @Column(name = "menu_id")
    private int menuId;
    @Column(name = "role_id")
    private int roleId;

    public XrefMenuRole() {
    }

    public XrefMenuRole(int id, int menuId, int roleId) {
        this.id = id;
        this.menuId = menuId;
        this.roleId = roleId;
    }

    public XrefMenuRole(int menuId, int roleId) {
        this.menuId = menuId;
        this.roleId = roleId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public List<Menu> getMenuList() throws DAOException {
        MenuDAOImpl daoImpl = new MenuDAOImpl();
        return daoImpl.getMenuList(this.roleId);
    }

    public Role getRole() throws DAOException {
        RoleDAOImpl daoImpl = new RoleDAOImpl();
        return daoImpl.loadRole(roleId);
    }

    public String toString() {
        return String.format("[menuId=%d, roleId=%d]", menuId, roleId);
    }
}
