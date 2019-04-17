$(function() {
    let shopId = getQueryString("shopId");
    getInitHistogramInfo(shopId);
})

/**
 * 去后台查询获取某个店铺的销量信息
 * @param shopId
 */
function getInitHistogramInfo(shopId) {
    jqxhr = $.ajax({
        url: "/o2o/productSellDaily/histogram",
        data: {"shopId": shopId},
        async: true,
        type: "POST",
        success: function(data) {
            ajaxRedirect(data);
            if (data.success) {
                var dom = document.getElementById("container");
                var myChart = echarts.init(dom);
                let option = initHistogram()
                option.legend = data.legendData;
                option.xAxis = data.xaxis;
                option.series = data.serieList;
                myChart.setOption(option, true);
                //当窗口变动大小时就会触发该事件，然后调用对应的函数
                window.onresize = function(){
                    myChart.resize();
                    //myChart1.resize();    //若有多个图表变动，可多写
                }
            } else {
                $.toast(data.errorMsg);
            }
        }
    })
}
function initHistogram() {

    var app = {};
    option = {
        //图例内容数组，数组项通常为string，每一项代表一个系列的name
        // legend: {data: [] },
        //直角坐标系内绘图网格
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        //提示框，鼠标悬浮交互时的信息提示
        tooltip: {
            trigger: 'axis',
            //坐标轴指示器，坐标轴触发有效
            axisPointer: {
                //鼠标移动到轴的时候，显示阴影
                type: 'shadow'
            }
        },
        //直角坐标系中横轴数组，数组中每一项代表一条横轴坐标轴
        // xAxis: {type: 'category',
        //     //类目型：需要指定类目列表，坐标轴内有且仅有这些指定类目坐标
        //     data: xaxis
        // },
        //直角坐标系中纵轴数组，数组中每一项代表一条纵轴坐标轴
        yAxis: {type: 'value'}

        //驱动图标生成的数据内容数组，数组中每一项为一个系列的选项及数组
        // series: [
        //     {type: 'bar', name: '烧仙草', data: [10, 20, 30, 20, 20, 20, 20]},
        //     {type: 'bar', name: '双皮奶', data: [10, 20, 30, 20, 20, 20, 20]},
        //     {type: 'bar', name: '红豆奶茶', data: [10, 20, 30, 20, 20, 20, 20]},
        //     {type: 'bar', name: '沙冰', data: [10, 20, 30, 40, 20, 20, 20]},
        // ]
    };


    return option;
}