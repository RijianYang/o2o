package com.ahead.service.impl;

import com.ahead.dto.ImgWrap;
import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.enums.ProductStateEnum;
import com.ahead.exceptions.ServiceRuntimeException;
import com.ahead.mapper.ProductImgMapper;
import com.ahead.mapper.ProductMapper;
import com.ahead.pojo.Product;
import com.ahead.pojo.ProductImg;
import com.ahead.service.ProductService;
import com.ahead.util.ImageUtil;
import com.ahead.util.PathUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/27
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductImgMapper productImgMapper;

    @Override
    public O2oExecution<Product> soldOutOrInProduct(Product product) {
        product.setLastEditTime(new Date());
        int effectNum = productMapper.updateProductById(product);
        //传过来状态为0的话说明就是要修改为不可用，即下架，反之亦然
        if (effectNum <= 0 && product.getEnableStatus() == 0) {
            throw new ServiceRuntimeException("下架商品失败！");
        }
        if (effectNum <= 0 && product.getEnableStatus() == 1) {
            throw new ServiceRuntimeException("上架商品失败！");
        }
        return new O2oExecution<>(CommonStateEnum.SUCCESS);
    }

    /**
     * 1、保存商品缩略图
     * 2、保存商品
     * 3、保存商品详情图片
     *
     * @param product
     * @param imgWrap
     * @param imgWrapList
     * @return
     */
    @Override
    public O2oExecution<Product> addProduct(Product product, ImgWrap imgWrap, List<ImgWrap> imgWrapList) throws ServiceRuntimeException {
        //添缩略图到对应的店铺文件夹中
        addProductImg(product, imgWrap);
        //给商品赋默认属性
        product.setLastEditTime(new Date());
        product.setCreateTime(new Date());
        product.setEnableStatus(1);

        //插入商品到数据库
        int effectNum = productMapper.insertProduct(product);
        if (effectNum <= 0) {
            throw new ServiceRuntimeException("添加商品失败！");
        }
        //插入商品成功后就添加商品详情图片
        addProductDetailImg(imgWrapList, product);

        return new O2oExecution<>(CommonStateEnum.SUCCESS);
    }

    @Override
    public O2oExecution<Product> getProductById(Long productId) {
        O2oExecution<Product> o2oExecution = null;
        Product product = productMapper.selectProductById(productId);
        o2oExecution = O2oExecution.isEmpty(o2oExecution, product);

        return o2oExecution;
    }

    /**
     * 1、如果缩略图有值就删除之前的缩略图文件，再把新增的缩略图的值持久化到磁盘中并赋值给product
     * 2、如果商品详情图有值就删除该商品之前所有的详情图，然后再删除数据库中对应的数据，把新增的详情图添加到数据库
     * 3、更新Product信息
     *
     * @param product
     * @param imgWrap
     * @param imgWrapList
     * @return
     * @throws ServiceRuntimeException
     */
    @Override
    public O2oExecution<Product> modifyProduct(Product product, ImgWrap imgWrap, List<ImgWrap> imgWrapList) throws ServiceRuntimeException {
        if (imgWrap != null) {
            //先获取原先缩略图相对地址
            Product tempProduct = productMapper.selectProductById(product.getProductId());
            //删除磁盘上对应的文件
            if (tempProduct.getImgAddr() != null) {
                ImageUtil.deleteFileOrPath(tempProduct.getImgAddr());
            }
            //把新增缩略图的值持久化到磁盘并赋值给product
            addProductImg(product, imgWrap);
        }
        if (imgWrapList != null && imgWrapList.size() > 0) {
            //获得该商品所有的详情图
            List<ProductImg> productImgList = productImgMapper.selectProductImgListByProductId(product.getProductId());
            for (ProductImg productImg : productImgList) {
                if (productImg.getImgAddr() != null) {
                    //遍历删除磁盘中该商品每个详情图
                    ImageUtil.deleteFileOrPath(productImg.getImgAddr());
                }
            }
            //删除该商品数据中原有的详情图片
            int effectNum = productImgMapper.deleteProductImgByProductId(product.getProductId());
            if (effectNum <= 0) {
                throw new ServiceRuntimeException("删除原有的商品详情图片失败！");
            }
            //把新增的详情图片添加到磁盘和数据库中去
            addProductDetailImg(imgWrapList, product);
        }
        product.setLastEditTime(new Date());
        //更新product
        int effectNum = productMapper.updateProductById(product);
        if (effectNum <= 0) {
            throw new ServiceRuntimeException("修改商品失败！");
        }
        return new O2oExecution<>(ProductStateEnum.SUCCESS);
    }

    @Override
    public O2oExecution<Product> getProductListByWhere(Long shopId, Product product, Integer page) throws ServiceRuntimeException {
        O2oExecution<Product> o2oExecution = null;
        o2oExecution = null;
        if (page == null) {
            page = 1;
        }
        PageHelper.startPage(page, 3);
        List<Product> productList = productMapper.selectProductListByWhere(shopId, product);
        if (productList != null) {
            PageInfo<Product> pageInfo = new PageInfo<>(productList);
            o2oExecution = new O2oExecution<>(CommonStateEnum.SUCCESS);
            o2oExecution.setPageInfo(pageInfo);
        } else {
            o2oExecution = new O2oExecution<>(CommonStateEnum.EMPTY);
        }
        return o2oExecution;
    }

    /**
     * 插入商品详情图片
     *
     * @param imgWrapList
     */
    private void addProductDetailImg(List<ImgWrap> imgWrapList, Product product) {
        //获取商品详情图片路径，这里直接使用所属店铺的路径
        String shopImgFolder = PathUtil.getShopImgPath(product.getShop().getShopId());
        //创建一个ProductImg集合，把详情图片封装进去
        List<ProductImg> productImgList = new ArrayList<>();
        for (ImgWrap imgWrap : imgWrapList) {
            //把商品详情图片保存到磁盘中
            String imgRelativePath = ImageUtil.generateThumbnail(imgWrap, shopImgFolder,
                    337, 640, 0.9f);
            ProductImg productImg = new ProductImg();
            productImg.setImgAddr(imgRelativePath);
            productImg.setProduct(product);
            productImg.setCreateTime(new Date());
            productImgList.add(productImg);
        }
        //如果图片是需要添加的就批量插入
        if (productImgList.size() > 0) {
            //批量插入商品详情图片到数据库中
            int effectNum = productImgMapper.batchInsertProductImg(productImgList);
            if (effectNum <= 0) {
                throw new ServiceRuntimeException("添加商品描述图片失败！");
            }
        }
    }

    /**
     * 添加商品缩略图
     *
     * @param product
     * @param imgWrap
     */
    private void addProductImg(Product product, ImgWrap imgWrap) {
        if (product.getShop() != null && product.getShop().getShopId() != null) {
            //获得店铺的相对路径
            String shopImgPath = PathUtil.getShopImgPath(product.getShop().getShopId());
            //保存缩略图
            String productImg = ImageUtil.generateThumbnail(imgWrap, shopImgPath, 200, 200, 0.8f);
            product.setImgAddr(productImg);
        }
    }
}
