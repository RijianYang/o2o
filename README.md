# O2O

### 一、介绍

该项目是一个o2o平台，前端使用SUI Mobile搭建整体样式，Jquery操作。后端使用到的技术SSM，Redis，MySQL8.0，Quartz。

店家可以在该平台入驻，建立自己的店铺（需要超级管理员进行审核），使其在前台系统进行显示自己店铺中的商品，用户可以在其平台上观看有什么商品，然后看中哪个商品就能去实体店购买。该平台分为三个系统：店家管理系统，前台展示系统，超级管理员系统。

### 二、业务介绍

#### 1、店家管理系统

1.1关注测试微信公众号然后使用微信登录或者使用微信登录后绑定微信的平台账号登录店家管理系统（需要超级管理员审核），就能看到自己所有店铺的列表集合。能进行添加店铺，绑定微信，退出系统，修改密码操作。

![AYPHeS.png](https://s2.ax1x.com/2019/03/24/AYPHeS.png)

1.2、进入某一个店铺就能对该店铺进行管理。

**店铺信息：**就是编辑店铺信息

**商品管理：**该店铺中商品的CRUD

**类别管理：**该店铺中的商品分类的CRUD

**奖品管理：**该店铺中奖品的CRUD

**消费记录：**该店铺中商品最近一周的销量**（使用百度的Echarts样式柱状图显示）**，商品出售是哪个店员操作的列		   表显示

**奖品记录：**该店铺中用户已领取的奖品列表显示

**顾客积分：**该店铺中所有用户的积分

**授权管理：**列表显示该店铺中的所有员工，并且店家出示二维码给某一个人扫，那个人就会成为该店铺中的店员，		   就就能在前台系统中帮顾客扫码增加积分了

![AYiPwF.png](https://s2.ax1x.com/2019/03/24/AYiPwF.png)

消费记录页面：

![AYFq8s.png](https://s2.ax1x.com/2019/03/24/AYFq8s.png)

授权管理：

![AYFvrV.png](https://s2.ax1x.com/2019/03/24/AYFvrV.png)

#### 2、前台展示系统

2.1、首页轮播图用来发布公告（比如上了新店或者平台所有店铺打折），首页轮播图和店铺分类都使用了Redis缓存。

![AYkNdS.png](https://s2.ax1x.com/2019/03/24/AYkNdS.png)

2.2、点击全部店铺显示店铺类别和区域下拉列表（都使用了Redis），店铺的列表显示，使用了

SUI Mobile中的无限下滑事件，只要下滑屏幕就会分页去后台查询数据。

![AYkWi4.png](https://s2.ax1x.com/2019/03/24/AYkWi4.png)

2.3、进入某个店铺就会列表显示所有的商品，也是无限下滑的。进入某个商品，如果想购买需要去实体店购买，付款给店员后，出示商品详情中的二维码给店员扫，就能增加该店的积分。**（进入商品详情页面需要登录）**

![AYkosx.png](https://s2.ax1x.com/2019/03/24/AYkosx.png)

![AYkzQI.png](https://s2.ax1x.com/2019/03/24/AYkzQI.png)

2.3、进入奖品兑换就能兑换该店铺中的奖品，并减少对应的积分。**（需要登录）**

![AYAPw8.png](https://s2.ax1x.com/2019/03/24/AYAPw8.png)

2.4、登录后就会有以下功能

**绑定微信：**通过关注微信测试公众号进行登录的就可以通过该功能创建一个账号，即使不使用微信也能使用该账号		   登录

**退出系统：**使用别的账号登录

**修改密码：**不用介绍了

**消费记录：**在每个店铺中消费的哪个商品得到了多少积分

**我的积分：**在每个店铺中的所拥有的总积分

**兑换记录：**显示所有的已兑换的奖品，如果未领取就可以点进去出示其二维码让店员扫描，领取对应的奖品

![AYAiTS.png](https://s2.ax1x.com/2019/03/24/AYAiTS.png)

兑换记录列表：

![AYAf78.png](https://s2.ax1x.com/2019/03/24/AYAf78.png)

领取页面：

![AYA5tg.png](https://s2.ax1x.com/2019/03/24/AYA5tg.png)

#### **3、超级管理员系统**

（不能注册，我是直接在数据库中创建了一个账号，并使我的用户类型为超级管理员类型）

**店铺管理：**就是对该平台上所有的店铺都能进行关闭该店铺或者开张该店铺

**账号管理**：对该平台上所有的店家的账号进行管理，可以对该店家账号进行封号和解封操作

[![AYEAHK.png](https://s2.ax1x.com/2019/03/24/AYEAHK.png)

![AYEJUS.png](https://s2.ax1x.com/2019/03/24/AYEJUS.png)
![AYEY4g.png](https://s2.ax1x.com/2019/03/24/AYEY4g.png)
#### **4、微信功能**
**1、微信登录**

我当时是申请了一个测试公众号来测试微信登录

1、首先我要接入微信公众平台开发，填写服务器的配置还有一个自定义的token字符串，在我服务器端有个响应对应这个配置的URL，然后测试号后台点击测试就会发送由时间戳和随机数还有token生成的微信加密签名还会带上生成该加密签名的时间戳和随机数，还有一个随机字符串，我服务器端获取到这些信息，根据获取到的时间戳和随机数还有自定义的token字符串生成一个加密后的字符串与微信加密签名对比，如果一致就返回前面接收到的随机字符串，表示接入成功，否则接入失败。

2、接入成功后，用户只要关注这个测试号，点击我提供好的链接（这个链接的格式是微信规定好的，只不过请求的url是可以自定义的，有个state参数可以设置一些可能会用到的数据），请求到指定的服务器上，我就可以获取到一个code字符串，再通过code请求微信指定好的格式链接获取到一个字符串（通过HttpsURLConnection在Java中请求的），从该字符串中我可以获取到access_token和open_id，根据access_token和open_id获取到用户的信息，此时就可以在我这个平台上为该用户注册一个微信账号

**2、二维码**

1、用户点击某个商品，跳转到商品页面，页面加载完成会请求到后台，根据商品的id和用户的id还有微信指定好的格式链接加上自己要处理增加积分逻辑的地址拼装成的url，因为url太长生成二维码会失败，所以这里通过HttpsURLConnection请求到百度的一个平台，可以将很长的链接转换成短链接，最后再生成一个二维码以图片流的形式响应给前端显示成一个二维码图片

2、该店员工扫描该二维码，就相当于请求刚刚那个拼装的url，请求到后台中，后台对该扫码的微信用户进行校验，如果是该店的员工，就往下处理对应的逻辑，比如增加顾客的积分或者添加一条兑换奖品的记录等


数据库模型就是o2o.ndm文件，使用Navicat软件打开
