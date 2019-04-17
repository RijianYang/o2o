package com.ahead.mapper;

import com.ahead.BaseTest;
import com.ahead.pojo.Award;
import com.ahead.pojo.Shop;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/10
 */
public class AwardMapperTest extends BaseTest {

    @Autowired
    private AwardMapper awardMapper;

    @Test
    public void testInsertAward() {
        Award award = new Award();
        award.setAwardDesc("test2");
        award.setAwardImg("test2");
        award.setAwardName("测试2");
        award.setCreateTime(new Date());
        award.setEnableStatus(1);
        award.setLastEditTime(new Date());
        award.setPoint(15);
        award.setPriority(110);
        Shop shop = new Shop();
        shop.setShopId(15L);
        award.setShop(shop);
        awardMapper.insertAward(award);
        System.out.println(award);
    }

    @Test
    public void testUpdateAwardById() {
        Award award = new Award();
        award.setAwardId(1L);
        award.setAwardName("正式2");
        award.setLastEditTime(new Date());
        awardMapper.updateAwardById(award);
    }

    @Test
    public void testSelectAwardById() throws Exception {
        Award award = awardMapper.selectAwardById(1L);
        System.out.println(award);
    }

    @Test
    public void testSelectAwardList() {

        Award awardWhere = new Award();
        awardWhere.setAwardName("测试");
        List<Award> awards = awardMapper.selectAwardListByWhere(null);
        for (Award award : awards) {
            System.out.println(award);
        }
    }

    @Test
    public void testDeleteAwardById() {
        int effectNum = awardMapper.deleteAwardById(3L);
        System.out.println(effectNum);
    }
}
