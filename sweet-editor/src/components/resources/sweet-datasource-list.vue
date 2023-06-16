<template>
  <div style="width: 100%;height: 100%;background: var(--toolbox-background)">
    <div class="ma-tree-wrapper">
      <div class="ma-tree-toolbar">
        <div class="ma-tree-toolbar-search">
          <i class="ma-icon ma-icon-search"></i>
          <input placeholder="输入关键字搜索" @input="e => doSearch(e.target.value)"/>
        </div>
        <div>
          <div class="ma-tree-toolbar-btn" title="新建数据源" @click="toogleDialog(true,true)">
            <i class="ma-icon ma-icon-group-add"></i>
          </div>
          <div class="ma-tree-toolbar-btn" title="刷新数据源" @click="initData()">
            <i class="ma-icon ma-icon-refresh"></i>
          </div>
          <div class="ma-tree-toolbar-btn" title="刷新缓存" @click="sync()">
            <i class="ma-icon ma-icon-history"></i>
          </div>
        </div>
      </div>
      <ul v-show="!showLoading">
        <template v-for="(item,index) in datasources" >
        <li v-if="item._searchShow === true || item._searchShow === undefined" :key="index" @contextmenu.prevent="e => datasourceContextMenu(e, item)">
          <i class="ma-icon ma-icon-datasource"/>
          <label>{{item.name || '主数据源'}}</label>
          <span>({{item.key || 'default'}})</span>
        </li>
        </template>
      </ul>
      <div class="loading" v-show="showLoading">
        <div class="icon">
          <i class="ma-icon ma-icon-refresh "></i>
        </div>
        加载中...
      </div>
      <div class="no-data" v-show="!showLoading && (!datasources || datasources.length === 0)">无数据</div>
    </div>
    <magic-dialog width="450px" :height="datasourceObj.type === 'mq' ? '555px' : '470px'" v-model="showDialog" :title="datasourceObj.id !== '-1' ? '修改数据源:' + datasourceObj.name : '创建数据源'" align="right" @onClose="toogleDialog(false)">
      <template #content>
        <div class="ds-form">
          <label>类型</label>
          <sweet-select :options="types" :value.sync="datasourceObj.type" default-value="String" style="width: 100%"/>
        </div>
        <div class="ds-form">
          <label>名称</label>
          <sweet-input :value.sync="datasourceObj.name" placeholder="数据源名称，仅做展示使用"/>
        </div>
        <div class="ds-form">
          <label>Key</label>
          <sweet-input :value.sync="datasourceObj.key" placeholder="数据库key，后续代码中使用"/>
        </div>

        <div class="ds-form" v-show="datasourceObj.type === 'redis' || datasourceObj.type === 'mq'">
          <label>Host</label>
          <sweet-input :value.sync="datasourceObj.host" placeholder="Redis主机地址"/>
        </div>
        <div class="ds-form" v-show="datasourceObj.type === 'redis' || datasourceObj.type === 'mq'">
          <label>端口</label>
          <sweet-input :value.sync="datasourceObj.port" placeholder="Redis端口"/>
        </div>
        <div class="ds-form" v-show="datasourceObj.type === 'redis'">
          <label>数据库</label>
          <sweet-input :value.sync="datasourceObj.database" placeholder="Redis数据库"/>
        </div>

        <div class="ds-form" v-show="datasourceObj.type === 'es'">
          <label>用户名</label>
          <sweet-input :value.sync="datasourceObj.elasticUser" placeholder="ES用户名"/>
        </div>
        <div class="ds-form" v-show="datasourceObj.type === 'es'">
          <label>密码</label>
          <sweet-input :value.sync="datasourceObj.elasticPassword" placeholder="ES密码"/>
        </div>
        <div class="ds-form" v-show="datasourceObj.type === 'es'">
          <label>主机名</label>
          <sweet-input :value.sync="datasourceObj.hostNames" placeholder="ES主机地址"/>
        </div>

        <div class="ds-form" v-show="datasourceObj.type === 'db'">
          <label>URL</label>
          <sweet-input :value.sync="datasourceObj.url" placeholder="请输入jdbcurl，如：jdbc:mysql://localhost"/>
        </div>
        <div class="ds-form" v-show="datasourceObj.type === 'db'">
          <label>用户名</label>
          <sweet-input :value.sync="datasourceObj.userName" placeholder="请输入数据库用户名"/>
        </div>
        <div class="ds-form" v-show="datasourceObj.type === 'mq'">
          <label>用户名</label>
          <sweet-input :value.sync="datasourceObj.username" placeholder="请输入MQ用户名"/>
        </div>
        <div class="ds-form" v-show="datasourceObj.type === 'db' || datasourceObj.type === 'redis' || datasourceObj.type === 'mq'">
          <label>密码</label>
          <sweet-input :value.sync="datasourceObj.password" type="password" placeholder="密码"/>
        </div>
        <div class="ds-form" v-show="datasourceObj.type === 'db'">
          <label>驱动类</label>
          <sweet-select :inputable="true" :border="true" :value.sync="datasourceObj.driverClassName" :options="drivers" :placeholder="'驱动类，可选，内部自动识别，也可以手动输入指定'"/>
        </div>
        <div class="ds-form" v-show="datasourceObj.type === 'db'">
          <label>类型</label>
          <sweet-select :inputable="true" :border="true" :value.sync="datasourceObj.dataSourceType" :options="datasourceTypes" :placeholder="'数据源类型，可选，也可以手动输入指定'"/>
        </div>
        <div class="ds-form" v-show="datasourceObj.type === 'db'">
          <label>maxRows</label>
          <sweet-input :value.sync="datasourceObj.maxRows" placeholder="最多返回条数，-1未不限制"/>
        </div>
        
        <div class="ds-form" v-show="datasourceObj.type === 'wechat'">
          <label>CorpId</label>
          <sweet-input :value.sync="datasourceObj.corpId" placeholder="企业微信的corpId"/>
        </div>
        <div class="ds-form" v-show="datasourceObj.type === 'wechat'">
          <label>AgentId</label>
          <sweet-input :value.sync="datasourceObj.agentId" placeholder="企业微信应用的AgentId"/>
        </div>
        <div class="ds-form" v-show="datasourceObj.type === 'wechat'">
          <label>Secret</label>
          <sweet-input :value.sync="datasourceObj.secret" placeholder="企业微信应用的Secret"/>
        </div>
        <div class="ds-form" v-show="datasourceObj.type === 'wechat'">
          <label>Token</label>
          <sweet-input :value.sync="datasourceObj.token" placeholder="应用中的 “接受消息” 部分的 “接收消息服务器配置” 里的Token值"/>
        </div>
        <div class="ds-form" v-show="datasourceObj.type === 'wechat'">
          <label>AesKey</label>
          <sweet-input :value.sync="datasourceObj.aesKey" placeholder="应用中的 “接受消息” 部分的 “接收消息服务器配置” 里的EncodingAESKey值"/>
        </div>

        <div class="ds-form" v-show="datasourceObj.type === 'mq'">
          <label>虚拟主机</label>
          <sweet-input :value.sync="datasourceObj.virtualHost" placeholder="请输入虚拟主机"/>
        </div>
        <div class="ds-form" v-show="datasourceObj.type === 'mq'">
          <label>消息配置</label>
          <sweet-select :inputable="false" :border="true" :value.sync="datasourceObj.publisherConfirmType" :options="publisherConfirmTypes"/>
        </div>
        <div class="ds-form" v-show="datasourceObj.type === 'mq'">
          <label>是否返回</label>
          <sweet-select :inputable="false" :border="true" :value.sync="datasourceObj.publisherReturns" :options="booleanTypes"/>
        </div>
        <div class="ds-form" v-show="datasourceObj.type === 'mq'">
          <label>强行退回</label>
          <sweet-select :inputable="false" :border="true" :value.sync="datasourceObj.mandatory" :options="booleanTypes"/>
        </div>
        <div class="ds-form" v-show="datasourceObj.type === 'mq'">
          <label>消息确认</label>
          <sweet-select :inputable="false" :border="true" :value.sync="datasourceObj.acknowledgeMode" :options="acknowledgeModes"/>
        </div>

        <div class="ds-form">
          <label>其它配置</label>
          <div ref="editor" class="ma-editor" style="width: 100%;height:150px"></div>
        </div>
      </template>
      <template #buttons>
        <button class="ma-button active" @click="doSave">{{ datasourceObj.id !== '-1' ? '修改' : '创建' }}</button>
        <button class="ma-button" @click="doTest">测试连接</button>
        <button class="ma-button" @click="toogleDialog(false)">取消</button>
      </template>
    </magic-dialog>
  </div>
</template>

<script>
import bus from '@/scripts/bus.js'
import request from '@/api/request.js'
import contants from "@/scripts/contants.js"
import MagicDialog from '@/components/common/modal/magic-dialog.vue'
import SweetInput from '@/components/common/sweet-input.vue'
import {formatJson, isVisible, replaceURL} from '@/scripts/utils.js'
import * as monaco from 'monaco-editor/esm/vs/editor/editor.api'
import store from "@/scripts/store";
import SweetSelect from "@/components/common/sweet-select";

export default {
  name: 'SweetDatasourceList',
  components: {
    SweetSelect,
    MagicDialog,
    SweetInput
  },
  data() {
    return {
      bus: bus,
      datasources: [],
      showDialog:false,
      datasourceObj: {
        id: "-1",
        type: "",
        name: "",
        key: "",
        // redis属性
        host: "",
        port: "",
        database: "",
        // es属性
        elasticUser: "",
        elasticPassword: "",
        hostNames: "",
        // db属性
        url: "",
        userName: "",
        password: "",
        driverClassName: "",
        maxRows: "-1",
        dataSourceType: "",
        // wechat属性
        corpId: "",
        agentId: "",
        secret: "",
        token: "",
        aesKey: "",
        // mq属性
        username: "",
        virtualHost: "",
        publisherConfirmType: "CORRELATED",
        publisherReturns: 1,
        mandatory: 1,
        acknowledgeMode: "MANUAL",
      },
      drivers: [
          'com.mysql.jdbc.Driver',
          'com.mysql.cj.jdbc.Driver',
          'oracle.jdbc.driver.OracleDriver',
          'org.postgresql.Driver',
          'com.microsoft.sqlserver.jdbc.SQLServerDriver',
          'com.ibm.db2.jcc.DB2Driver',
          ...contants.JDBC_DRIVERS
      ].map(it => { return {text: it, value: it} }),
      datasourceTypes: [
          'com.zaxxer.hikari.HikariDataSource',
          'com.alibaba.druid.pool.DruidDataSource',
          'org.apache.tomcat.jdbc.pool.DataSource',
          'org.apache.commons.dbcp2.BasicDataSource',
          ...contants.DATASOURCE_TYPES
      ].map(it => { return {text: it, value: it} }),
      publisherConfirmTypes: [
        {text: "禁用发布确认模式", value: "NONE"},
        {text: "消息成功到达Broker后触发ConfirmCalllBack回调", value: "CORRELATED"},
        {text: "如果消息成功到达Broker后一样会触发", value: "SIMPLE"},
      ].map(it => { return {text: it.text, value: it.value} }),
      booleanTypes: [
        {text: "是", value: 1},
        {text: "否", value: 0},
      ].map(it => {return {text: it.text, value: it.value} }),
      acknowledgeModes: [
        {text: "无应答模式", value: "NONE"},
        {text: "自动应答模式", value: "AUTO"},
        {text: "手动模式", value: "MANUAL"},
      ].map(it => {return {text: it.text, value: it.value} }),
      types: [
        {text: '数据库', value: 'db'},
        {text: 'Redis', value: 'redis'},
        {text: 'Elasticsearch', value: 'es'},
        {text: '企业微信', value: 'wechat'},
        {text: 'RabbitMQ', value: 'mq'},
      ],
      editor: null,
      // 是否展示loading
      showLoading: true
    }
  },
  methods: {
    layout() {
      this.$nextTick(() => {
        if (this.editor && isVisible(this.$refs.editor)) {
          this.editor.layout()
        }
      })
    },
    doSearch(keyword) {
      keyword = keyword.toLowerCase()
      this.datasources.forEach(it => {
        it._searchShow = keyword ? (it.name&&it.name.toLowerCase().indexOf(keyword) > -1) || (it.key && it.key.toLowerCase().indexOf(keyword) > -1) : true;
      })
      this.$forceUpdate();
    },
    datasourceContextMenu(event,item){
      if(item.id){
        this.$magicContextmenu({
          menus: [{
            label : '修改数据源',
            icon: 'ma-icon-update',
            onClick: ()=>this.showDetail(item)
          },{
            label : '删除数据源',
            icon: 'ma-icon-delete',
            divided: true,
            onClick: ()=>this.deleteDataSource(item)
          },
          {
            label: '复制KEY',
            icon: 'ma-icon-copy',
            onClick: () => {
              this.copyKeyToClipboard(item)
            }
          }],
          event,
          zIndex: 9999
        });
      }
      return false;
    },
    // 初始化数据
    initData() {
      this.showLoading = true
      this.datasources = []
      bus.$emit('status', '正在初始化数据源列表')
      return new Promise((resolve) => {
        request.send(`/${contants.ESB_SERVICE_NAME}/connection/list`).success(data => {
          this.datasources = data || []
          setTimeout(() => {
            this.showLoading = false
          }, 500)
          bus.$emit('status', '数据源初始化完毕')
          resolve()
        })
      })
    },
    sync() {
      bus.$emit('status', '正在刷新数据源缓存')
      return new Promise((resolve) => {
        request.send(`/${contants.ESB_SERVICE_NAME}/connection/sync`, [], {method: 'GET'}).success(data => {
          bus.$emit('status', '数据源缓存刷新完毕')
        })
      })
    },
    showDetail(item){
      if(!item.id){
        this.$magicAlert({
          content : '该数据源不能被修改'
        })
      }else{
        bus.$emit('status', `加载数据源「${item.name}」详情`)
        this.datasources.forEach(it => {
          if (it.id === item.id) {
            this.datasourceObj = it
            this.toogleDialog(true)
            bus.$emit('status', `数据源「${item.name}」详情加载完毕`)
          }
        })
      }
    },
    getDataSourceObj(){
      let temp = {
        id: this.datasourceObj.id,
        type: this.datasourceObj.type,
        name: this.datasourceObj.name,
        key: this.datasourceObj.key,
        host: this.datasourceObj.host,
        port: this.datasourceObj.port,
        database: this.datasourceObj.database,
        elasticUser: this.datasourceObj.elasticUser,
        elasticPassword: this.datasourceObj.elasticPassword,
        hostNames: this.datasourceObj.hostNames,
        maxRows: this.datasourceObj.maxRows,
        dataSourceType: this.datasourceObj.dataSourceType,
        driverClassName: this.datasourceObj.driverClassName,
        userName: this.datasourceObj.userName,
        password: this.datasourceObj.password,
        url: this.datasourceObj.url,
        corpId: this.datasourceObj.corpId,
        agentId: this.datasourceObj.agentId,
        secret: this.datasourceObj.secret,
        token: this.datasourceObj.token,
        aesKey: this.datasourceObj.aesKey,
        username: this.datasourceObj.username,
        virtualHost: this.datasourceObj.virtualHost,
        publisherConfirmType: this.datasourceObj.publisherConfirmType,
        publisherReturns: this.datasourceObj.publisherReturns,
        mandatory: this.datasourceObj.mandatory,
        acknowledgeMode: this.datasourceObj.acknowledgeMode,
      }
      let value = this.editor.getValue();
      let json = {};
      try{
        json = JSON.parse(value)
      }catch(e){
      }
      for(let key in json){
        if(!temp[key]){
          temp[key] = json[key]
        }
      }
      return temp;
    },
    doTest(){
      bus.$emit('status', `测试数据源连接...`)
      request.send(`/${contants.ESB_SERVICE_NAME}/connection/test`,JSON.stringify(this.getDataSourceObj()),{
        method: 'post',
        headers: {
          'Content-Type': 'application/json'
        },
        transformRequest: []
      }).success(msg => {
        if(!msg){
          this.$magicAlert({
            content : '连接成功'
          })
          bus.$emit('status', `数据源连接成功`)
        }else{
          bus.$emit('status', `数据源连接失败：${msg}`)
          this.$magicAlert({
            title: '测试连接失败',
            content : msg
          })
        }
      })
    },
    doSave(){
      if(!this.datasourceObj.name){
        this.$magicAlert({
          content : '数据源名称不能为空'
        })
      }else if(!this.datasourceObj.key){
        this.$magicAlert({
          content : '数据源key不能为空'
        })
      }else{
        bus.$emit('status', `保存数据源「${this.datasourceObj.name}」...`)
        request.send(`/${contants.ESB_SERVICE_NAME}/connection/save`,JSON.stringify(this.getDataSourceObj()),{
          method: 'post',
          headers: {
            'Content-Type': 'application/json'
          },
          transformRequest: []
        }).success(dsId => {
          bus.$emit('status', `数据源「${this.datasourceObj.name}」保存成功`)
          this.showDialog = false;
          this.initDataSourceObj()
          this.initData();

        })
      }
    },
    initDataSourceObj(){
      this.datasourceObj = {
        id: "-1",
        type: "db",
        name: "",
        key: "",
        host: "",
        port: "",
        database: "",
        elasticUser: "",
        elasticPassword: "",
        hostNames: "",
        url: "",
        userName: "",
        password: "",
        maxRows: "-1",
        dataSourceType: "",
        corpId: "",
        agentId: "",
        secret: "",
        token: "",
        aesKey: "",
        username: "",
        virtualHost: "",
        publisherConfirmType: "CORRELATED",
        publisherReturns: 1,
        mandatory: 1,
        acknowledgeMode: "MANUAL",
      }
    },
    toogleDialog(show,clear){
      this.showDialog = show;
      if(show){
        if(clear){
          this.initDataSourceObj()
        }
        bus.$emit('status', `准备编辑数据源`)
        let temp = {...this.datasourceObj}
        delete temp.id
        delete temp.type
        delete temp.name
        delete temp.key
        delete temp.host
        delete temp.port
        delete temp.database
        delete temp.elasticUser
        delete temp.elasticPassword
        delete temp.hostNames
        delete temp.maxRows
        delete temp.dataSourceType
        delete temp.driverClassName
        delete temp.userName
        delete temp.password
        delete temp.url
        delete temp.corpId
        delete temp.agentId
        delete temp.secret
        delete temp.token
        delete temp.aesKey
        delete temp.username
        delete temp.virtualHost
        delete temp.publisherConfirmType
        delete temp.publisherReturns
        delete temp.mandatory
        delete temp.acknowledgeMode
        if(!this.editor){
          this.editor = monaco.editor.create(this.$refs.editor, {
            minimap: {
              enabled: false
            },
            language: 'json',
            fixedOverflowWidgets: true,
            folding: true,
            wordWrap: 'on',
            fontFamily: contants.EDITOR_FONT_FAMILY,
            fontSize: contants.EDITOR_FONT_SIZE,
            fontLigatures: true,
            renderWhitespace: 'none',
            theme: store.get('skin') || 'default',
            value: formatJson(temp) || '{\r\n\t\r\n}'
          })
        }else{
          bus.$emit('status', `编辑数据源「${this.datasourceObj.name}」`)
          this.editor.setValue(formatJson(temp))
        }
        this.layout();
      }
    },
    // 删除接口
    deleteDataSource(item) {
      bus.$emit('status', `准备删除数据源「${item.name}(${item.key})」`)
      this.$magicConfirm({
        title: '删除数据源',
        content: `是否要删除数据源「${item.name}(${item.key})」`,
        onOk: () => {
          request.send(`/${contants.ESB_SERVICE_NAME}/connection/delete`, {id: item.id}).success(data => {
            if (data) {
              bus.$emit('status', `数据源「${item.name}(${item.key})」已删除`)
              this.initData();
            } else {
              this.$magicAlert({content: '删除失败'})
            }
          })
        }
      })
    },
    // 复制关键字到剪贴板
    copyKeyToClipboard(item) {
      try {
        var copyText = document.createElement('textarea')
        copyText.style = 'position:absolute;left:-99999999px'
        document.body.appendChild(copyText)
        copyText.innerHTML = item.key
        copyText.readOnly = false
        copyText.select()
        document.execCommand('copy')
        bus.$emit('status', `关键字「${item.key}」复制成功`)
      } catch (e) {
        this.$magicAlert({title: '复制关键字失败，请手动复制', content: item.key})
        console.error(e)
      }
    }
  },
  mounted() {
    this.bus.$on('logout', () => this.datasources = []);
    this.bus.$on('refresh-resource',() => {
      this.initData()
    })
  }
}
</script>

<style>
@import './sweet-resource.css';
</style>
<style scoped>

ul li {
  line-height: 20px;
  padding-left: 10px;
}
ul li:hover{
  background: var(--toolbox-list-hover-background);
}
.ds-form{
  margin-bottom: 5px;
  display: flex;
}
.ds-form label{
  margin-right: 5px;
  display: inline-block;
  width: 60px;
  text-align: right;
  height: 22px;
  line-height: 22px;
}
.ds-form > div{
  flex: 1;
}
.ds-form label:nth-of-type(2){
  margin: 0 5px;
}
.ma-editor span{
  color: unset;
}
.ma-tree-wrapper{
  width: 100%;
  height: 100%;
}
.ma-tree-wrapper .loading i {
  color: var(--color);
  font-size: 20px;
}
.ma-tree-wrapper .loading .icon {
  width: 20px;
  margin: 0 auto;
  animation: rotate 1s linear infinite;
}
.ma-tree-wrapper .loading {
  color: var(--color);
  position: absolute;
  text-align: center;
  width: 100%;
  top: 50%;
  margin-top: -20px;
}
.ma-tree-wrapper .no-data {
  color: var(--color);
  position: absolute;
  top: 50%;
  left: 50%;
  margin-left: -20px;
}
</style>
