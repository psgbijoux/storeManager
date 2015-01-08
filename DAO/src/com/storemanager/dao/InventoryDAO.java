package com.storemanager.dao;

import com.storemanager.models.*;
import com.storemanager.util.DAOException;

import java.util.List;

public interface InventoryDAO {
    public boolean add(Inventory inventory) throws DAOException;

    public boolean addFirst(InventoryFirst inventoryFirst) throws DAOException;

    public boolean addSecond(InventorySec inventorySec) throws DAOException;

    public boolean clear(String type) throws DAOException;

    public List<InventoryReport> getInventoryResult(String type) throws DAOException;

    public List<InventoryReport> getCategoryInventoryResult(String type, Category category) throws DAOException;
}
