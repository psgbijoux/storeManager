package com.storemanager.dao;

import com.storemanager.models.Inventory;
import com.storemanager.models.InventoryReport;
import com.storemanager.util.DAOException;

import java.util.List;

/**
 * Author: Lucian Moldovan
 * Date: 2/2/13
 * Time: 4:34 PM
 */
public interface InventoryDAO {
    public boolean add(Inventory inventory) throws DAOException;

    public boolean clear() throws DAOException;

    public List<InventoryReport> getInventoryResult() throws DAOException;
}
