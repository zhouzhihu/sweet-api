<template>
  <div class="ma-status-container">
    <div v-html="message"></div>
    <!-- <div class="ma-icons">
      <span v-if="config.header.repo !== false" title="Gitee">
        <i class="ma-icon ma-icon-gitee"></i>
      </span>
      <span v-if="config.header.repo !== false" title="Github">
        <i class="ma-icon ma-icon-git"></i>
      </span>
      <span v-if="config.header.qqGroup !== false" title="加入QQ群">
        <i class="ma-icon ma-icon-qq"></i>
      </span>
      <span v-if="config.header.document !== false" title="帮助文档">
        <i class="ma-icon ma-icon-help"></i>
      </span>
    </div> -->
  </div>
</template>

<script>
import bus from '@/scripts/bus.js'

export default {
  name: 'SweetStatusBar',
  props: {
    config: {
      type: Object
    }
  },
  data() {
    return {
      message: ''
    }
  },
  mounted() {
    bus.$on('status', (message) => this.message = message)
  },
  methods: {
    open(url) {
      window.open(url)
    }
  }
}
</script>

<style scoped>
.ma-status-container {
  position: absolute;
  bottom: 0px;
  height: 24px;
  width: 100%;
  border-top: 1px solid var(--footer-border-color);
  padding-left: 20px;
  /* line-height: 24px; */
  background: var(--background);
  flex: none;
  text-align: left;
  color: var(--color);
  display: flex;
}
.ma-status-container > div {
  flex: 1;
}
.ma-status-container .ma-icons{
  flex: none;
  color: var(--header-default-color);
}
.ma-status-container .ma-icons span{
  cursor: pointer;
  padding: 0 4px;
  height: 20px;
  line-height: 20px;
  display: inline-block;
  vertical-align: middle;
  border-radius: 2px;
  text-align: center;
}
.ma-status-container .ma-icons span:hover{
  background: var(--button-hover-background);
}
</style>
