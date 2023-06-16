const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
const path = require('path')
const webpack = require('webpack')
const resolve = dir => {
  return path.join(__dirname, dir)
}
// 设置环境变量，可以在全局使用
process.env.VUE_APP_MA_VERSION = require('./package.json').version

module.exports = {
  publicPath: './',
  productionSourceMap: false,
  configureWebpack: {
    output: {
      libraryExport: 'default'
    },
    module: {
      rules:[{
        test: /\.worker.js$/,
        exclude: /node_modules/,
        use: [{
          loader: 'worker-loader',
          options: {
            inline: 'fallback'
          }
        }]
      }]
    },
    plugins: [
      new MonacoWebpackPlugin()
    ]
  },
  parallel: false,
  chainWebpack: config => {
    config.resolve.alias
      .set('@', resolve('src')) // key,value自行定义，比如.set('@@', resolve('src/components'))
      .set('public', resolve('public'))
    // 移除 prefetch 插件
    config.plugins.delete('prefetch')
    // 移除 preload 插件
    config.plugins.delete('preload')
    config.output.globalObject('this')
    config.output.filename('js/[name].[hash].js').end()
  }
}
