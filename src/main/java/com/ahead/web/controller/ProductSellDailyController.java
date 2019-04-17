package com.ahead.web.controller;

import com.ahead.dto.EchartsLegend;
import com.ahead.dto.EchartsSerie;
import com.ahead.dto.EchartsXaxis;
import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.pojo.*;
import com.ahead.service.ProductSellDailyService;
import com.ahead.service.UserProductMapService;
import com.ahead.util.ModelMapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/17
 */
@Controller
@RequestMapping("/productSellDaily")
public class ProductSellDailyController {

    @Autowired
    private UserProductMapService userProductMapService;
    @Autowired
    private ProductSellDailyService productSellDailyService;

    @RequestMapping("/userProductMapList")
    @ResponseBody
    public Map<String, Object> userProductMapList(Integer page, Long shopId, String productName,
                                                  String buyerName) {
        Map<String, Object> modelMap = new HashMap<>();
        UserProductMap userProductMapWhere = new UserProductMap();
        Product product = new Product();
        product.setProductName(productName);

        userProductMapWhere.setProduct(product);

        User buyer = new User();
        buyer.setName(buyerName);

        userProductMapWhere.setBuyer(buyer);

        Shop shop = new Shop();
        shop.setShopId(shopId);

        userProductMapWhere.setShop(shop);

        O2oExecution<UserProductMap> o2oExecution = userProductMapService.getUserProductListByWhere(userProductMapWhere, page, 2);
        if (o2oExecution.getState() == CommonStateEnum.EMPTY.getState()) {
            return ModelMapUtil.errorMsg(o2oExecution.getStateInfo(), modelMap);
        } else {
            modelMap.put("pageInfo", o2oExecution.getPageInfo());
            modelMap.put("success", true);
            return modelMap;
        }
    }

    @RequestMapping("/histogram")
    @ResponseBody
    public Map<String, Object> histogram(Long shopId) {
        Map<String, Object> modelMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        //获取前一天的时间
        Date endTime = calendar.getTime();
        //这里只需要减6就行，上面已经减1了，下面获得就是7天之前的了
        calendar.add(Calendar.DATE, -6);
        Date beginTime = calendar.getTime();
        O2oExecution<ProductSellDaily> o2oExecution = productSellDailyService.getProductSellDailyListByShopId(shopId, beginTime, endTime);
        //不需要进行判断，Service如果抛异常就会被异常处理器捕获到，返回的值也不会是EMPTY，因为就算商品没有销量也会有一条销量为0的记录存着
        List<ProductSellDaily> productSellDailyList = o2oExecution.getList();
        //创建图例数组对象
        EchartsLegend legend = new EchartsLegend();
        //直角坐标系中横轴数组，数组中每一项代表一条横轴坐标轴
        EchartsXaxis xaxis = new EchartsXaxis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //动图标生成的数据内容数组，数组中每一项为一个系列的选项及数组
        List<EchartsSerie> serieList = new ArrayList<>();
        //每个商品一周的的销量集合
        List<Integer> totalList = new ArrayList<>();
        //数据库中查出来的是按商品id升序，id一样就按创建时间升序
        //所以用preProductName作标识
        String preProductName = "";
        for (int i = 0; i < productSellDailyList.size(); i++) {
            ProductSellDaily productSellDaily = productSellDailyList.get(i);
            //把前面7次的创建时间当作X轴，因为后面的时间都是前面7次重复的
            if (i < 7) {
                xaxis.getData().add(sdf.format(productSellDaily.getCreateTime()));
            }
            //只要上一次的商品与这次循环的商品不一样说明就是到临界点了
            //且不能为空，第一次就进入不了
            if (!preProductName.equals(productSellDaily.getProduct().getProductName())
                    && !preProductName.isEmpty()) {
                //如果到了临界点就得初始化一个EchartsSerie对象
                EchartsSerie serie = new EchartsSerie();
                serie.setName(preProductName);
                serie.setData(totalList);
                serieList.add(serie);
                //设置给图例数组
                legend.getData().add(preProductName);
                //重置totalList
                totalList = new ArrayList<>();
//                //设置这次循环的销量
//                totalList.add(productSellDaily.getTotal());
//                //赋值上一次的productName
//                preProductName = productSellDaily.getProduct().getProductName();
            }
            //设置这次循环的销量
            totalList.add(productSellDaily.getTotal());
            //赋值上一次的productName
            preProductName = productSellDaily.getProduct().getProductName();
//            } else {
//                //设置销量
//                totalList.add(productSellDaily.getTotal());
//                //赋值上一次的productName
//                preProductName = productSellDaily.getProduct().getProductName();
//            }
            //因为每次都是前面一个商品遍历完之后，第二个商品才会初始化第一个商品对应的EchartsSerie对象
            //所以最后一个商品对应的EchartsSerie对象就不会被初始化，这里需要在最后一条记录中手动初始化
            if (productSellDailyList.size() - 1 == i) {
                legend.getData().add(preProductName);
                EchartsSerie serie = new EchartsSerie();
                serie.setName(preProductName);
                serie.setData(totalList);
                serieList.add(serie);
            }
        }
        modelMap.put("serieList", serieList);
        modelMap.put("xaxis", xaxis);
        modelMap.put("legendData", legend);
        modelMap.put("success", true);
        return modelMap;
    }

    /**
     * 转发到销售记录页面
     *
     * @return
     */
    @RequestMapping("/consumeRecordPage")
    public String consumeRecordPage() {
        return "productselldaily/consumerecord";
    }
}
