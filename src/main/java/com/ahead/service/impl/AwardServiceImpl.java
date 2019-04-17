package com.ahead.service.impl;

import com.ahead.dto.ImgWrap;
import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.enums.ProductStateEnum;
import com.ahead.exceptions.ServiceRuntimeException;
import com.ahead.mapper.AwardMapper;
import com.ahead.pojo.Award;
import com.ahead.service.AwardService;
import com.ahead.util.ImageUtil;
import com.ahead.util.PathUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/19
 */
@Service
public class AwardServiceImpl implements AwardService {

    @Autowired
    private AwardMapper awardMapper;

    @Override
    public O2oExecution<Award> soldOutOrInAward(Award award) {

        award.setLastEditTime(new Date());
        int effectNum = awardMapper.updateAwardById(award);
        //传过来状态为0的话说明就是要修改为不可用，即下架，反之亦然
        if (effectNum <= 0 && award.getEnableStatus() == 0) {
            throw new ServiceRuntimeException("下架商品失败！");
        }
        if (effectNum <= 0 && award.getEnableStatus() == 1) {
            throw new ServiceRuntimeException("上架商品失败！");
        }
        return new O2oExecution<>(CommonStateEnum.SUCCESS);
    }

    /**
     * 1、保存奖品缩略图
     * 2、保存奖品
     * 3、保存奖品详情图片
     *
     * @param award
     * @param imgWrap
     * @return
     */
    @Override
    public O2oExecution<Award> addAward(Award award, ImgWrap imgWrap) {
        //添缩略图到对应的店铺文件夹中
        addAwardImg(award, imgWrap);
        //给奖品赋默认属性
        award.setLastEditTime(new Date());
        award.setCreateTime(new Date());
        award.setEnableStatus(1);

        //插入奖品到数据库
        int effectNum = awardMapper.insertAward(award);
        if (effectNum <= 0) {
            throw new ServiceRuntimeException("添加奖品失败！");
        }

        return new O2oExecution<>(CommonStateEnum.SUCCESS);
    }

    @Override
    public O2oExecution<Award> getAwardById(Long awardId) {
        O2oExecution<Award> o2oExecution = null;
        Award award = awardMapper.selectAwardById(awardId);
        o2oExecution = O2oExecution.isEmpty(o2oExecution, award);

        return o2oExecution;
    }

    /**
     * 1、如果缩略图有值就删除之前的缩略图文件，再把新增的缩略图的值持久化到磁盘中并赋值给award
     * 2、更新Award信息
     *
     * @param award
     * @param imgWrap
     * @return
     */
    @Override
    public O2oExecution<Award> modifyAward(Award award, ImgWrap imgWrap) {
        if (imgWrap != null) {
            //先获取原先缩略图相对地址
            Award tempAward = awardMapper.selectAwardById(award.getAwardId());
            //删除磁盘上对应的文件
            if (tempAward.getAwardImg() != null) {
                ImageUtil.deleteFileOrPath(tempAward.getAwardImg());
            }
            //把新增缩略图的值持久化到磁盘并赋值给award
            addAwardImg(award, imgWrap);
        }

        award.setLastEditTime(new Date());
        //更新product
        int effectNum = awardMapper.updateAwardById(award);
        if (effectNum <= 0) {
            throw new ServiceRuntimeException("修改奖品失败！");
        }
        return new O2oExecution<>(ProductStateEnum.SUCCESS);
    }


    /**
     * 添加奖品缩略图
     *
     * @param award
     * @param imgWrap
     */
    private void addAwardImg(Award award, ImgWrap imgWrap) {
        if (award.getShop() != null && award.getShop().getShopId() != null) {
            //获得店铺的相对路径
            String shopImgPath = PathUtil.getShopImgPath(award.getShop().getShopId());
            //保存缩略图
            String productImg = ImageUtil.generateThumbnail(imgWrap, shopImgPath, 200, 200, 0.8f);
            award.setAwardImg(productImg);
        }
    }

    @Override
    public O2oExecution<Award> getAwardList(Award awardWhere, Integer page, Integer pageSize) {
        if (page == null || page <= 0) {
            page = 1;
        }
        O2oExecution<Award> o2oExecution = null;

        PageHelper.startPage(page, pageSize);
        List<Award> awardList = awardMapper.selectAwardListByWhere(awardWhere);
        if (awardList == null || awardList.size() <= 0) {
            o2oExecution = new O2oExecution<>(CommonStateEnum.EMPTY);
        } else {
            PageInfo<Award> pageInfo = new PageInfo<>(awardList);
            o2oExecution = new O2oExecution<>(CommonStateEnum.SUCCESS);
            o2oExecution.setPageInfo(pageInfo);
        }
        return o2oExecution;
    }
}
