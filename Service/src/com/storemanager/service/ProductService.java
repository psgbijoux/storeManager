package com.storemanager.service;

import com.storemanager.dao.ProductDAO;
import com.storemanager.dao.impl.ProductDAOImpl;
import com.storemanager.models.Product;
import com.storemanager.models.ProductUpdate;
import com.storemanager.models.SaleDetail;
import com.storemanager.util.DAOException;
import com.storemanager.util.ImageUtil;
import com.storemanager.util.ServiceException;
import com.storemanager.util.SettingsEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService implements StoreService {
    private ProductDAO daoImpl;
    int index = 0;

    @Autowired
    public ProductService(ProductDAOImpl daoImpl) {
        this.daoImpl = daoImpl;
    }

    public List<Product> getProductsByCategoryId(int categoryId) throws ServiceException {
        try {
            return daoImpl.getProductByCategoryId(categoryId);
        } catch (DAOException e) {
            throw new ServiceException("Error loading products with category id:" + categoryId);
        }
    }

    public List<Product> getOutOfStockProductsByCategoryId(int categoryId) throws ServiceException {
        try {
            return daoImpl.getOutOfStockProductByCategoryId(categoryId);
        } catch (DAOException e) {
            throw new ServiceException("Error loading out of stock products with category id:" + categoryId);
        }
    }

    public boolean add(Product product, BufferedImage image) throws ServiceException {
        Dimension imgSize = new Dimension(image.getWidth(), image.getHeight());
        SettingsService settingsService = ServiceLocator.getService(ServiceName.SETTINGS_SERVICE);
        Dimension boundary;
        try {
            int panelWidth = settingsService.loadSettings(SettingsEnum.SS_IMAGE_WIDTH).getIntValue();
            int panelHeight = settingsService.loadSettings(SettingsEnum.SS_IMAGE_HEIGHT).getIntValue();
            boundary = new Dimension(panelWidth, panelHeight);
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
        Dimension newImageSize = ImageUtil.getScaledDimension(imgSize, boundary);
        int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
        try {
            BufferedImage resizedImage = ImageUtil.resizeImage(image, type, (int) newImageSize.getWidth(), (int) newImageSize.getWidth());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            product.setImage(imageInByte);
            baos.close();
        } catch (Exception ex) {
            throw new ServiceException("Error adding product to database.");
        }
        try {
            return daoImpl.store(product);
        } catch (DAOException e) {
            throw new ServiceException("Error adding product: " + product.getName());
        }
    }

    public boolean update(Product product, BufferedImage image) throws ServiceException {
        if (image != null) {
            Dimension imgSize = new Dimension(image.getWidth(), image.getHeight());
            SettingsService settingsService = ServiceLocator.getService(ServiceName.SETTINGS_SERVICE);
            Dimension boundary;
            try {
                int panelWidth = settingsService.loadSettings(SettingsEnum.SS_IMAGE_WIDTH).getIntValue();
                int panelHeight = settingsService.loadSettings(SettingsEnum.SS_IMAGE_HEIGHT).getIntValue();
                boundary = new Dimension(panelWidth, panelHeight);
            } catch (DAOException e) {
                throw new ServiceException(e.getMessage());
            }
            Dimension newImageSize = ImageUtil.getScaledDimension(imgSize, boundary);
            int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
            try {
                BufferedImage resizedImage = ImageUtil.resizeImage(image, type, (int) newImageSize.getWidth(), (int) newImageSize.getHeight());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, "jpg", byteArrayOutputStream);
                byteArrayOutputStream.flush();
                byte[] imageInByte = byteArrayOutputStream.toByteArray();
                product.setImage(imageInByte);
                byteArrayOutputStream.close();
            } catch (Exception ex) {
                throw new ServiceException("Error adding product to database.");
            }
        }
        try {
            return daoImpl.store(product);
        } catch (DAOException e) {
            throw new ServiceException("Error updating product");
        }
    }

    public boolean storeProductUpdate(ProductUpdate product) throws ServiceException {
        try {
            return daoImpl.storeProductUpdate(product);
        } catch (DAOException e) {
            throw new ServiceException("Error storing productUpdate: " + product.getProductId());
        }
    }

    public boolean delete(Product product) throws ServiceException {
        try {
            return daoImpl.delete(product);
        } catch (DAOException e) {
            throw new ServiceException("Error deleting product: " + product.getName());
        }
    }

    public Product load(int id) throws ServiceException {
        try {
            return daoImpl.loadProduct(id);
        } catch (DAOException e) {
            throw new ServiceException("Error loading product with id: " + id);
        }
    }

    public Product getProductByBarCode(String barCode) throws ServiceException {
        try {
            return daoImpl.getProductByBarCode(barCode);
        } catch (DAOException e) {
            throw new ServiceException("Error loading product with bare code " + barCode);
        }
    }

    public Product getProductByCode(String code) throws ServiceException {
        try {
            return daoImpl.getProductByCode(code);
        } catch (DAOException e) {
            throw new ServiceException("Error loading product with code " + code);
        }
    }


    public int getBareCodeCount() throws ServiceException {
        Integer count = null;
        try {
            count = daoImpl.getBareCodeCount();
        } catch (DAOException e) {
            throw new ServiceException("Error getting bare code count.");
        }
        if (count == null) {
            count = new Integer(0);
        }
        return count + 1;
    }

    public boolean updateProductQuantityByBarCode(String barCode, int quantity) throws ServiceException {
        try {
            Product product = daoImpl.getProductByBarCode(barCode);
            if(product != null) {
                product.setQuantity(product.getQuantity()+quantity);
                daoImpl.store(product);
            } else {
                return false;
            }
        } catch (DAOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        }
        return true;
    }

    public List<ProductUpdate> getProductUpdateData(Product product) throws ServiceException{
        try {
            List<ProductUpdate> list = daoImpl.getProductUpdates(product);
            return list;
        } catch (DAOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public void updateProductsDescription(ArrayList<String> barCodes, ArrayList<String> supplyCodes) {

        daoImpl.updateProductDescription(barCodes, supplyCodes);
    }



}
