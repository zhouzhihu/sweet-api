<template>
  <div class="ma-settings">
    <div class="ma-layout">
      <div class="not-select ma-sider">
        <div @click="addRow"><i class="ma-icon ma-icon-plus"/></div>
        <div @click="removeRow"><i class="ma-icon ma-icon-minus"/></div>
        <div @click="save"><i class="ma-icon ma-icon-save"/></div>
      </div>
      <div class="ma-layout-container">
        <div class="ma-header ma-table-row ma-table-request-row">
          <div style="width:400px">名称</div>
          <div style="width:135px">类型</div>
          <div style="width:120px">编码</div>
          <div style="width:120px">执行用户</div>
          <div style="flex:1">扩展配置</div>
        </div>
        <div class="ma-content">
          <div v-for="(item, key) in actuators" :key="'request_parameter_' + key" class="ma-table-row ma-table-request-row">
            <div style="width:400px" :class="{ focus: actuatorIndex === key && !item.name }">
              <sweet-input :focus="() => (actuatorIndex = key)" :value.sync="item.name" style="width: 100%"/>
            </div>
            <div style="width:135px">
              <sweet-select :border="false" :focus="() => (actuatorIndex = key)" :options="types" :value.sync="item.type" default-value="task" style="width: 100%"/>
            </div>
            <div style="width:120px">
              <sweet-input :focus="() => (actuatorIndex = key)" :value.sync="item.key" style="width: 100%"/>
            </div>
            <div style="width:120px">
              <sweet-input :focus="() => (actuatorIndex = key)" :value.sync="item.userName" style="width: 100%"/>
            </div>
            <div style="flex:1">
              <sweet-input :focus="() => (actuatorIndex = key)" :value.sync="item.config" style="width: 100%"/>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import bus from '@/scripts/bus.js'
import SweetInput from '@/components/common/sweet-input.vue'
import SweetSelect from '@/components/common/sweet-select.vue'
import request from '@/api/request.js'
import contants from "@/scripts/contants.js"

export default {
  name: 'SweetActuator',
  props: {
      info: Object
    },
  components: {
    SweetInput,
    SweetSelect
  },
  data() {
    return {
      actuators: [],
      actuatorIndex: 0,
      types: [
          {value: 'task', text: '定时任务'},
          {value: 'mq', text: 'RabbitMQ'}
        ],
    }
  },
  mounted() {
    this.initData()
  },
  methods: {
    initData() {
      let infoId = this.info?.id ?? -1
      console.log("infoId: " + this.infoId)
      if (-1 === infoId || "" === infoId)
        return
      bus.$emit('status', '正在初始化API执行器')
      return new Promise((resolve) => {
        request.send(`/${contants.ESB_SERVICE_NAME}/apiActuator/list/${infoId}`).success(data => {
          this.actuators = []
          let actuatorList = data || []
          for(let item of actuatorList) {
            let {id, name, type, key, userName, apiId, ...config} = item
            this.actuators.push({id: id, name: name, type: type, key: key, userName: userName, apiId: apiId, config: JSON.stringify(config)})
          }
          bus.$emit('status', 'API执行器初始化完毕')
          resolve()
        })
      })
    },
    save() {
      if (this.actuators.length === 0)
        return;
      for(let item of this.actuators) {
        try{
          JSON.parse(item.config)
        } catch(e) {
          this.$magicAlert({
            content : '扩展配置不是JSON格式，请修正！'
          })
        }
      }
      let entitys = []
      for(let item of this.actuators) {
        entitys.push(JSON.stringify({...item, ...JSON.parse(item.config)}))
      }
      if (entitys.length > 0) {
        request.send(`/${contants.ESB_SERVICE_NAME}/apiActuator/save`,JSON.stringify(entitys),{
          method: 'post',
          headers: {
            'Content-Type': 'application/json'
          },
          transformRequest: []
        }).success(dsId => {
          this.$magicAlert({content: '执行器保存成功'})
          bus.$emit('status', `执行器保存成功`)
          this.initData();
        })
      }
    },
    addRow() {
      this.actuators.push({id: "-1", name: '', type: 'task', key: '', userName: '', apiId: this.info?.id ?? -1, config: ''})
      this.actuatorIndex = this.actuators.length - 1
      this.$forceUpdate()
    },
    removeRow() {
      let item = this.actuators[this.actuatorIndex];
      if (null === item || undefined === item)
        return;
      if ("-1" === item.id) {
        return this._removeRow()
      }
      bus.$emit('status', `准备删除执行器「${item.name}(${item.key})」`)
      this.$magicConfirm({
        title: '删除执行器',
        content: `是否要删除执行器「${item.name}(${item.key})」`,
        onOk: () => {
          request.send(`/${contants.ESB_SERVICE_NAME}/apiActuator/delete`, {id: item.id}).success(data => {
            if (data) {
              bus.$emit('status', `执行器「${item.name}(${item.key})」已删除`)
              this._removeRow()
            } else {
              this.$magicAlert({content: '删除失败'})
            }
          })
        }
      })
      this.$forceUpdate()
    },
    _removeRow() {
      this.actuators.splice(this.actuatorIndex, 1)
      if (this.actuators.length == 0) {
        this.actuatorIndex = 0
        this.addRow()
      } else if (this.actuators.length <= this.actuatorIndex) {
        this.actuatorIndex = this.actuators.length - 1
      }
    }
  },
  watch: {
    info: {
      deep: true,
      handler() {
        this.initData()
      }
    }
  }
}
</script>

<style scoped>
.ma-settings {
  background: var(--background);
  height: 100%;
  width: 100%;
  position: relative;
  outline: 0;
}

.ma-settings .ma-layout {
  height: calc(100% - 25px);
}
</style>
