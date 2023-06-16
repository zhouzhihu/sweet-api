<template>
  <div id="app">
    <sweet-editor :config="config"/>
  </div>
</template>

<script>
import SweetEditor from '@/components/sweet-editor.vue'

export default {
  name: 'App',
  components: {
    SweetEditor,
  },
  data() {
    let defaultConfig = {};
    try {
      if (parent && parent.SWEET_EDITOR_CONFIG) {
        defaultConfig = {...parent.SWEET_EDITOR_CONFIG};
      }
    } catch (ignored) {
    }
    if (window.SWEET_EDITOR_CONFIG) {
      defaultConfig = {...defaultConfig, ...window.SWEET_EDITOR_CONFIG}
    }
    defaultConfig.inJar = true;
    return {
      config: defaultConfig
    }
  }
}
</script>

<style>
html,
body,
#app {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
}
</style>
