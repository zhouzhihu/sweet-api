## 简介
Sweet-API是基于Java的HTTP API快速接口开发框架，线上完成接口开发、调试和发布，自动映射为HTTP接口。 无需定义Controller、Service、Dao、Mapper、XML、VO等Java对象即可完成常见的HTTP API接口开发。

![技术架构](./_media/platform.png ':size=40%')

## 特性
* 支持MySQL、达梦、Oracle、SQLServer等支持jdbc规范的数据库；
* 支持非关系型数据库Redis、ElasticSearch；
* 支持第三方接口API和WebService；
* 支持多租户模式；
* 支持多数据源配置，支持在线配置数据源；
* 支持分页查询以及自定义分页查询；
* 支持自定义JSON结果、自定义分页结果；
* 支持运行时动态修改数据源；
* 支持可插拔式的插件机制；
* 支持数据库事务、SQL支持拼接，占位符，判断等语法；
* 支持文件上传、下载、输出图片；

![技术架构](./_media/editor.png ':size=60%')

## 鸣谢

框架采用的开源技术如下：

* [magic-api](https://gitee.com/ssssssss-team/magic-api)

* [spring-brick](https://gitee.com/starblues/springboot-plugin-framework-parent)