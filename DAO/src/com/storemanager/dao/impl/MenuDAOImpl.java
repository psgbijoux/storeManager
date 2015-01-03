package com.storemanager.dao.impl;

import com.storemanager.dao.DAO;
import com.storemanager.dao.MenuDAO;
import com.storemanager.models.Menu;
import com.storemanager.util.DAOException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class MenuDAOImpl extends DAO implements MenuDAO {

    public MenuDAOImpl() {
    }

    public boolean store(Menu menu) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.saveOrUpdate(menu);
            tx.commit();
            //log event
            LOGGER.info(this.getClass().getName(), "Updated menu: " + menu.getName());
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public boolean delete(Menu menu) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.delete(menu);
            tx.commit();
            //log event
            LOGGER.info(this.getClass().getName(), "Deleted menuId: " + menu.getId());
            return true;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public Menu getFileMenu() throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Menu rez = (Menu) session.createCriteria(Menu.class).add(Restrictions.eq("name", "File")).uniqueResult();
            return rez;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public List<Menu> getMenuList(int roleId) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            String sql = "select M.id as id, M.name as name, M.image_path as image_path,M.command as command,M.parent_id as parent_id, M.mnemonic as mnemonic " +
                    "from menus M, roles R, xref_menu_role MR " +
                    "where MR.menu_id = M.id and mr.role_id=R.id and R.id=?";
            SQLQuery query = session.createSQLQuery(sql);
            query.setParameter(0, roleId);
            query.addEntity(Menu.class);
            List<Menu> result = (List<Menu>) query.list();
            if (result.size() < 1) {
                LOGGER.info(this.getClass().getName(), "Error loading menu list");
            }
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public List<Menu> getMenuList() throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            String sql = "select M.id as id, M.name as name, M.image_path as image_path,M.command as command,M.parent_id as parent_id, M.mnemonic as mnemonic " +
                    "from menus M where m.parent_Id = '0'";
            SQLQuery query = session.createSQLQuery(sql);
            query.addEntity(Menu.class);
            List<Menu> result = (List<Menu>) query.list();
            if (result.size() < 1) {
                LOGGER.info(this.getClass().getName(), "Error loading menu list");
            }
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public List<Menu> getSubMenuList(int menuId) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            List<Menu> result = (List<Menu>) session.createCriteria(Menu.class).add(Restrictions.eq("parentId", menuId)).list();
            if (result.size() < 1) {
                LOGGER.info(this.getClass().getName(), "Error loading sub menu list");
            }
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }

    public Menu loadMenu(int menuId) throws DAOException {
        final Session session = getSessionFactory().openSession();
        try {
            Menu result = (Menu) session.createCriteria(Menu.class).add(Restrictions.eq("id", menuId)).uniqueResult();
            return result;
        } catch (Exception e) {
            throw new DAOException(e.getMessage());
        } finally {
            session.close();
            closeSessionFactory();
        }
    }
}
