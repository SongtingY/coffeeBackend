# nowcoderBackend

## 介绍

一个基本功能完整的论坛项目。项目主要功能有：基于邮件激活的注册方式，基于 MD5 加密与加盐的密码存储方式，登陆功能加入了随机验证码的验证。实现登陆状态的检查、为游客和已登录用户展示不同界面与功能。实现不同用户的权限控制和网站数据统计(UV、DAU)，管理员可以查看网站数据统计和网站监控信息。支持用户上传头像，实现发布帖子、评论帖子、热帖排行、发送私信与敏感词过滤等功能。实现了点赞关注与系统通知功能。支持全局搜索帖子信息的功能。

### 核心功能具体实现

1. 通过对登录用户颁发登录凭证，将登陆凭证存进 Redis 中来记录登录用户登录状态，使用拦截器进行登录状态检查，使用 Spring Security 实现权限控制，解决了 http 无状态带来的缺陷，保护需登录或权限才能使用的特定资源。
2. 使用 ThreadLocal 在当前线程中存储用户数据，代替 session 的功能便于分布式部署。在拦截器的 preHandle 中存储用户数据并构建用户认证的结果存入 SecurityContext，在 postHandle 中将用户数据存入 Model，在 afterCompletion 中清理用户数据。
3. 使用 Redis 的集合数据类型来解决踩赞、相互关注功能，采用事务管理，保证数据的正确，采用“先更新数据库，再删除缓存”策略保证数据库与缓存数据的一致性。采用 Redis 存储验证码，解决性能问题和分布式部署时的验证码需求。采用 Redis 的 HyperLogLog 存储每日 UV、Bitmap 存储 DAU，实现网站数据统计的需求。
4. 使用 Kafka 作为消息队列，在用户被点赞、评论、关注后以系统通知的方式推送给用户，用户发布或删除帖子后向 elasticsearch 同步，wk 生成长图后将长图上传至云服务器，对系统进行解耦、削峰。
5. 使用 elasticsearch + ik 分词插件实现全局搜索功能，当用户发布、修改或删除帖子时，使用 Kafka 消息队列去异步将帖子给 elasticsearch 同步。
6. 使用分布式定时任务 Quartz 定时计算帖子分数，来实现热帖排行的业务功能。
7. 对频繁需要访问的数据，如用户信息、帖子总数、热帖的单页帖子列表，使用 Caffeine 本地缓存 + Redis 分布式缓存的多级缓存，提高服务器性能，实现系统的高可用。

### 核心技术

- Spring Boot、SSM
- Redis、Kafka、Elasticsearch
- Spring Security、Quartz、Caffeine
<img width="987" alt="Screenshot 2023-09-19 at 5 42 35 PM" src="https://github.com/SongtingY/nowcoderBackend/assets/94235734/1ca547f0-37c1-4512-a0fe-616d46d22ca8">
