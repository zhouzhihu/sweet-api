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

## 模块介绍

* sweet-core：`sweet-api`向外提供的服务接口，包括：API执行器、数据源扩展、动态插件扩展、资源管理和多租户管理等接口。

* sweet-api：实现`sweet-api`核心框架，包括动态API实现、GraalJS实现和内置插件实现等；

* sweet-api-starters：`sweet-api`启动器模块，可以理解为第三方接口的集成模块。包括多数据源管理以及扩展插件Excel、ElasticSearch、RabbitMQ、Redis、Wechat等插件的启动器实现；

* sweet-api-plugins：利用`sweet-core`提供的服务接口扩展插件，同时提供操作API给`sweet-editor`使用。包括：ElasticSearch插件、Excel插件、RabbitMQ插件、Redis插件、Task插件和Wechat微信插件等；

* sweet-api-spring-boot-starter：`sweet-api`提供的spring boot启动器；

* [sweet-api-web](./sweet-api-web/README.md)：采用`Spring Boot`技术为`Sweet-Api`提供`数据存储`,为`Sweet-Editor`提供`RESTful API`接口服务;

* sweet-editor：前端在线编辑器，提供在线书写`JavaScirpt`脚本，线上完成接口开发、调试和发布；