## 简介
Sweet-API-Web采用`Spring Boot`技术开发，提供以下功能：

* 为`Sweet-Api`提供`数据存储`，默认采用的`数据库`方式，后续将提供`文件`、`Redis`等多种实现方案。

* 为`Sweet-Editor`提供`RESTful API`接口服务；

## 开发技术

采用Spring Boot 2.2.9.RELEASE、mybatis-plus 3.4.0等技术实现。

## 数据库结构

创建表MySQL语句在[db/mysql.sql](../db/mysql.sql)中。

![数据库模型](../_media/sweet-api-db.jpg ':size=60%')

## 插件安装

pom.xml中可以预先安装所需要的插件：

```xml
<!-- =====================Sweet-API插件 begin====================== -->
<dependencies>
    ...
    <!-- RESTFul插件 -->
    <dependency>
        <groupId>com.egrand.sweetapi.plugin</groupId>
        <artifactId>sweet-api-plugin-restful</artifactId>
        <version>1.2.1-SNAPSHOT</version>
        <type>zip</type>
    </dependency>
    <!-- EasyExcel插件 -->
    <dependency>
        <groupId>com.egrand.sweetapi.plugin</groupId>
        <artifactId>sweet-api-plugin-excel</artifactId>
        <version>1.2.1-SNAPSHOT</version>
        <type>zip</type>
    </dependency>
    <!-- RabbitMQ插件 -->
    <dependency>
        <groupId>com.egrand.sweetapi.plugin</groupId>
        <artifactId>sweet-api-plugin-mq</artifactId>
        <version>1.2.1-SNAPSHOT</version>
        <type>zip</type>
    </dependency>
    <!-- SpringTask定时任务插件 -->
    <dependency>
        <groupId>com.egrand.sweetapi.plugin</groupId>
        <artifactId>sweet-api-plugin-task</artifactId>
        <version>1.2.1-SNAPSHOT</version>
        <type>zip</type>
    </dependency>
    <!-- 企业微信插件 -->
    <dependency>
        <groupId>com.egrand.sweetapi.plugin</groupId>
        <artifactId>sweet-api-plugin-wechat</artifactId>
        <version>1.2.1-SNAPSHOT</version>
        <type>zip</type>
    </dependency>
    <!-- ElasticSearch插件-->
    <dependency>
        <groupId>com.egrand.sweetapi.plugin</groupId>
        <artifactId>sweet-api-plugin-es</artifactId>
        <version>1.2.1-SNAPSHOT</version>
        <type>zip</type>
    </dependency>
    <!-- Redis插件 -->
    <dependency>
        <groupId>com.egrand.sweetapi.plugin</groupId>
        <artifactId>sweet-api-plugin-redis</artifactId>
        <version>1.2.1-SNAPSHOT</version>
        <type>zip</type>
    </dependency>
    ...
</dependencies>
<!-- =====================Sweet-API插件 end====================== -->
```

## 插件运行

运行时`插件`目录放在`classes/plugins`中的，需要将`插件`拷贝到该文件夹下。

pom.xml中已经声明了一个插件：

```xml
<build>
    <plugins>
        ...
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-dependency-plugin</artifactId>
            <executions>
                <!--导出所有的 jar 包-->
                <execution>
                    <id>plugin-process-resources</id>
                    <goals>
                        <goal>copy-dependencies</goal>
                    </goals>
                    <!-- (打包阶段):在实际打包中，执行任何的必要的操作。 -->
                    <phase>process-resources</phase>
                    <configuration>
                        <outputDirectory>${project.build.directory}/classes/plugins</outputDirectory>
                        <stripVersion>false</stripVersion>
                        <includeTypes>zip</includeTypes>
                    </configuration>
                </execution>
            </executions>
        </plugin>
        ...
    </plugins>
</build>
```
在`打包`或`发布`时会自动运行。

如果您需要在开发工具中例如：idea，直接运行，则需要配置下运行`process-resources`命令，如下：

* 第一步

![开发模式](../_media/0-dev.png ':size=60%')

* 第二步

![开发模式](../_media/1-dev.png ':size=60%')

* 第三步

![开发模式](../_media/3-dev.png ':size=60%')

## 编译和发布

* 打包

```text
mvn clean install
```

* 发布

在发布前，需要在`pom.xml`将仓库地址修改为自己仓库地址：

```xml
<distributionManagement>
    <repository>
        <id>nexus-releases</id>
        <url>http://10.1.10.212:8081/repository/maven-releases/</url>
    </repository>
    <snapshotRepository>
        <id>nexus-snapshots</id>
        <url>http://10.1.10.212:8081/repository/maven-snapshots/</url>
    </snapshotRepository>
</distributionManagement>
```

```text
mvn clean deploy
```

* 生成镜像

镜像生成集成了`google`的`jib`插件，在pom.xml中已经声明，在生成镜像前需要修改pom文件，如下配置：

```xml
<properties>
    ...
    <!-- 发布镜像地址 -->
    <docker.image.prefix>10.1.10.212:8082</docker.image.prefix>
    <!-- 基础镜像的项目-->
    <docker.image.project.library>library</docker.image.project.library>
    <!-- 发布镜像的项目 -->
    <docker.image.project.prefix>egrand-cloud</docker.image.project.prefix>
    <!-- 启动主程序类 -->
    <docker.image.project.main.class>com.egrand.SweetApiWebApplication</docker.image.project.main.class>
    ...
</properties>
```

运行命令：

```text
mvn clean compile jib:build -DsendCredentialsOverHttp=true
```
生成镜像。

## 注册中心

### 注册到consul

先自行安装`consul 1.9.0`，做如下配置修改：

* `pom.xml`添加consul依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
    <version>2.2.4.RELEASE</version>
</dependency>
```

* 修改`application.xml`配置：

```yml
# 放开端口
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
sprng:
  cloud:
    consul:
      # consul注册中心的ip地址
      host: localhost
      # consul注册中心端口
      port: 8500
      discovery:
        # 实例id(唯一标志)
        instance-id: ${spring.application.name}:${server.port}:${random.value}
        # 服务的名称
        service-name: ${spring.application.name}
        # 开启ip地址注册
        prefer-ip-address: true
        # 当前服务的请求ip
        ip-address: ${spring.cloud.client.ip-address}
        # 服务的请求端口
        port: ${server.port}
        # critical状态下线时间限定（Consul服务用）
        health-check-critical-timeout: 30s
        # 如果配置了context-path就配置，没有就不配
        health-check-path: ${server.servlet.context-path}/actuator/health
```