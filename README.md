### 基于湖南科技大学生产实习课设题目二

商城秒杀



后端方面大致完成了，准备编写前端，预计使用vue-admin-template来实现。



### 技术栈

springboot、mybatis、MySQL、redis、zookeeper、RabbitMQ



### 实现功能

1. 使用了分布式锁（redis、redisson、zookeeper三种实现）优化秒杀逻辑，使其在高并发下可用。
2. 使用RabbitMQ异步发送秒杀成功的订单的邮件，以及死信队列和定时任务来取消超时未支付的订单。
3. 商品的增删改查；新增、删除秒杀商品。
4. 用户管理（增删改查）、用户登录



### 未来实现

1. 完成前端页面
2. 加入支付模块、购物车模块
3. 添加管理员模块（管理员添加商品等）
4. 加入权限认证模块（shiro或springSecurity）

