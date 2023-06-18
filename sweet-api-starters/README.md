## 简介

`sweet-api`启动器模块，可以理解为第三方接口的集成模块。包括多数据源管理以及扩展插件Excel、ElasticSearch、RabbitMQ、Redis、Wechat等插件的启动器实现；

## 模块介绍

* sweet-api-starter-db：多数据源模块，支持MySQL、SQL Service、达梦等种类型的数据库；

* sweet-api-starter-easyexcel：Excel插件启动器，对接EasyExcel工具；

* sweet-api-starter-es： ElasticSearch插件启动器，对接bboss-elasticsearch工具，支持多数据源配置；

* sweet-api-starter-mq：RabbitMQ插件启动器，对接spring-boot-starter-amqp工具，支持多数据源配置；

* sweet-api-starter-redis：Redis插件启动器，对接spring-boot-starter-data-redis和jedis工具，支持多数据源配置；

* sweet-api-starter-wechat：Wechat插件启动器，对接企业微信接口，支持多数据源配置；

## 多数据源配置

多数据源配置支持两种方式：

* yml配置：在项目yml文件中设置数据源；

* sweet-editor界面配置：在`sweet-editor`界面`数据源`页签中动态添加；

以下介绍yml配置如何配置多数据源：

* db多数据源配置

```yml
spring:
  datasource:
    dynamic:
      # 默认数据库
      primary: master
      datasource:
        # 默认数据库
        master:
          url: jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/sweet-api?useUnicode=true&characterEncoding=utf8&characterSetResults=utf8&serverTimezone=GMT%2B8&connectTimeout=60000&socketTimeout=30000
          username: ${MYSQL_USERNAME:root}
          password: ${MYSQL_PASSWORD:123456}
          driverClassName: com.mysql.jdbc.Driver
          type: com.alibaba.druid.pool.DruidDataSource
        # 第一个数据库
        one:
          url: jdbc:mysql://${MYSQL_HOST:10.1.10.1}:${MYSQL_PORT:3306}/sweet-api?useUnicode=true&characterEncoding=utf8&characterSetResults=utf8&serverTimezone=GMT%2B8&connectTimeout=60000&socketTimeout=30000
          username: ${MYSQL_USERNAME:root}
          password: ${MYSQL_PASSWORD:123456}
          driverClassName: com.mysql.jdbc.Driver
          type: com.alibaba.druid.pool.DruidDataSource
        # 第二个数据库
        two:
          url: jdbc:mysql://${MYSQL_HOST:10.1.10.2}:${MYSQL_PORT:3306}/sweet-api?useUnicode=true&characterEncoding=utf8&characterSetResults=utf8&serverTimezone=GMT%2B8&connectTimeout=60000&socketTimeout=30000
          username: ${MYSQL_USERNAME:root}
          password: ${MYSQL_PASSWORD:123456}
          driverClassName: com.mysql.jdbc.Driver
          type: com.alibaba.druid.pool.DruidDataSource
```

* ElasticSearch插件多数据源

```yml
spring:
  # 配置Elasticsearch bboss
  elasticsearch:
    enableMulti: true
    multi:
      # 默认连接
      default:
        name: default
        elasticUser: elastic
        elasticPassword: changeme
        elasticsearch:
          rest:
            hostNames: ${ES_HOST:10.1.10.178}:${ES_PORT:9200}
            ##hostNames: 10.1.10.209:9200,10.1.10.210:9200 ##集群地址配置
          dateFormat: yyyy.MM.dd
          timeZone: Asia/Shanghai
          # 在控制台输出脚本调试开关showTemplate,false关闭，true打开，同时log4j至少是info级别
          showTemplate: true
          discoverHost: false
        dslfile:
          # dsl配置文件热加载扫描时间间隔，毫秒为单位，默认5秒扫描一次，<= 0时关闭扫描机制
          refreshInterval: -1
        # es client http连接池配置
        http:
          timeoutConnection: 5000
          timeoutSocket: 5000
          connectionRequestTimeout: 5000
          retryTime: 1
          maxLineLength: -1
          maxHeaderCount: 200
          maxTotal: 400
          defaultMaxPerRoute: 200
          soReuseAddress: false
          soKeepAlive: false
          timeToLive: 3600000
          keepAlive: 3600000
          keystore:
          keyPassword:
          hostnameVerifier:
      spb:
        name: dpra-es
        elasticUser: elastic
        elasticPassword: changeme
        elasticsearch:
          rest:
            hostNames: ${ES_HOST:10.1.10.178}:${ES_PORT:5200}
            ##hostNames: 10.1.10.209:9200,10.1.10.210:9200 ##集群地址配置
          dateFormat: yyyy.MM.dd
          timeZone: Asia/Shanghai
          # 在控制台输出脚本调试开关showTemplate,false关闭，true打开，同时log4j至少是info级别
          showTemplate: true
          discoverHost: false
        dslfile:
          # dsl配置文件热加载扫描时间间隔，毫秒为单位，默认5秒扫描一次，<= 0时关闭扫描机制
          refreshInterval: -1
        # es client http连接池配置
        http:
          timeoutConnection: 5000
          timeoutSocket: 5000
          connectionRequestTimeout: 5000
          retryTime: 1
          maxLineLength: -1
          maxHeaderCount: 200
          maxTotal: 400
          defaultMaxPerRoute: 200
          soReuseAddress: false
          soKeepAlive: false
          timeToLive: 3600000
          keepAlive: 3600000
          keystore:
          keyPassword:
          hostnameVerifier:
      dpraprod:
        name: egd-es
        elasticUser: elastic
        elasticPassword: changeme
        elasticsearch:
          rest:
            hostNames: ${ES_HOST:10.1.10.178}:${ES_PORT:5201}
            ##hostNames: 10.1.10.209:9200,10.1.10.210:9200 ##集群地址配置
          dateFormat: yyyy.MM.dd
          timeZone: Asia/Shanghai
          # 在控制台输出脚本调试开关showTemplate,false关闭，true打开，同时log4j至少是info级别
          showTemplate: true
          discoverHost: false
        dslfile:
          # dsl配置文件热加载扫描时间间隔，毫秒为单位，默认5秒扫描一次，<= 0时关闭扫描机制
          refreshInterval: -1
        # es client http连接池配置
        http:
          timeoutConnection: 5000
          timeoutSocket: 5000
          connectionRequestTimeout: 5000
          retryTime: 1
          maxLineLength: -1
          maxHeaderCount: 200
          maxTotal: 400
          defaultMaxPerRoute: 200
          soReuseAddress: false
          soKeepAlive: false
          timeToLive: 3600000
          keepAlive: 3600000
          keystore:
          keyPassword:
          hostnameVerifier:
```

* RabbitMQ插件多数据源配置

```yml
spring:
  rabbitmq:
    enable-multi: true
    multi:
      default:
        virtual-host: /dwork-plus
        # 主机地址，默认localhost
        host: 10.1.10.178
        # 主机端口，默认5672
        port: 5672
        # 访问用户名
        username: wiki
        # 访问密码
        password: wiki2012
        # 是否启用【发布确认】，默认none
        publisher-confirm-type: correlated
        # 是否启用【发布返回】，默认false
        publisher-returns: true
        template:
          mandatory: true
        listener:
          simple:
            # 表示消息确认方式，其有三种配置方式，分别是none、manual和auto；默认auto
            acknowledge-mode: manual
      one:
        virtual-host: /dwork-prod
        # 主机地址，默认localhost
        host: 10.1.10.178
        # 主机端口，默认5672
        port: 5672
        # 访问用户名
        username: wiki
        # 访问密码
        password: wiki2012
        # 是否启用【发布确认】，默认none
        publisher-confirm-type: correlated
        # 是否启用【发布返回】，默认false
        publisher-returns: true
        template:
          mandatory: true
        listener:
          simple:
            # 表示消息确认方式，其有三种配置方式，分别是none、manual和auto；默认auto
            acknowledge-mode: manual
```

* Redis插件多数据源配置

```yml
spring:
  redis:
    enable-multi: true
    multi:
      default:
        database: 1
        host: 10.1.10.178
        password:
        port: 6364
        timeout: 60000
        lettuce:
          pool:
            max-active: -1
            max-idle: -1
            max-wait: -1
            min-idle: -1
        enable-multi: true
      two:
        database: 1
        host: 10.1.10.178
        port: 6370
        password:
        timeout: 60000
        lettuce:
          pool:
            max-active: -1
            max-idle: -1
            max-wait: -1
            min-idle: -1
```

* Wechat企业微信多数据源配置

```yml
spring:
  wechat:
    cp:
      enable-multi: true
      multi:
        default:
          agentId: 1000006
          corpId: xxxx
          secret: xxxxx
          token: 111
          aesKey: 111
        zcyj:
          agentId: 1000010
          corpId: xxxx
          secret: xxxxx
          token: 111
          aesKey: 111
```