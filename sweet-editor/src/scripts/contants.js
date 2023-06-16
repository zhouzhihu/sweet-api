let MAGIC_API_VERSION_TEXT = process.env.VUE_APP_MA_VERSION
let MAGIC_API_VERSION = 'V' + MAGIC_API_VERSION_TEXT.replace(/\./g, '_')

const contants = {

  // ========================================== 不需要登录配置 ===========================
  // 基础路径
  BASE_URL: 'http://localhost:19016',
  BASE_URL_STORE: 'Sweet-Base-URL',
  // 服务名或上下文
  ESB_SERVICE_NAME: 'sweet-api',
  ESB_SERVICE_NAME_STORE: 'Sweet-ESB-Service',
  // WebSocket服务地址
  WEBSOCKET_SERVER: 'http://localhost:19016/sweet-api',
  WEBSOCKET_SERVER_STORE: 'Sweet-Websocket-Service',
  // 认证服务名称(不需要登录，配置为空)
  AUTH_SERVICE_NAME: '',
  AUTH_SERVICE_NAME_STORE: 'Sweet-Auth-Service',
  // 指定运行版本（本地调试时使用）
  EGD_HEADER_VERSION_NAME: 'e-g-d-version',
  EGD_HEADER_VERSION_VALUE: '',
  // 租户标识（多租户下使用）
  EGD_HEADER_TENANT_NAME: 'x-tenant-header',
  EGD_HEADER_TENANT_VALUE: '',

  // ========================================== 登录配置 ===========================
  // // 基础路径（网关地址）
  // BASE_URL: 'http://10.1.10.184:30426',
  // BASE_URL_STORE: 'Sweet-Base-URL',
  // // ESB服务名
  // ESB_SERVICE_NAME: 'egrand-esb',
  // ESB_SERVICE_NAME_STORE: 'Sweet-ESB-Service',
  // // WebSocket服务地址
  // WEBSOCKET_SERVER: 'http://10.1.10.174:31605',
  // WEBSOCKET_SERVER_STORE: 'Sweet-Websocket-Service',
  // // 认证服务名称
  // AUTH_SERVICE_NAME: 'egrand-cloud-yuncang-auth',
  // AUTH_SERVICE_NAME_STORE: 'Sweet-Auth-Service',
  // // 指定运行版本（本地调试时使用）
  // EGD_HEADER_VERSION_NAME: 'e-g-d-version',
  // EGD_HEADER_VERSION_VALUE: '',
  // // EGD_HEADER_VERSION_VALUE: 'zzh',
  // EGD_HEADER_TENANT_NAME: 'x-tenant-header',
  // EGD_HEADER_TENANT_VALUE: 'lyffp',


  // 是否自动保存
  AUTO_SAVE: true,
  DECORATION_TIMEOUT: 10000,
  API_DEFAULT_METHOD: 'GET',
  MAGIC_API_VERSION_TEXT,
  MAGIC_API_VERSION,
  HEADER_REQUEST_SESSION: 'Sweet-Request-Client-Id',
  HEADER_REQUEST_SESSION_VALUE: '',
  HEADER_SWEET_TOKEN: 'Authorization',
  HEADER_SWEET_TOKEN_VALUE: 'unauthorization',
  RECENT_OPENED_TAB: 'recent_opened_tab',
  RECENT_OPENED: 'recent_opened',
  LOG_MAX_ROWS: Infinity,
  DEFAULT_EXPAND: true,
  JDBC_DRIVERS: [],
  DATASOURCE_TYPES: [],
  OPTIONS: [],
  EDITOR_FONT_FAMILY: 'JetBrainsMono, Consolas, "Courier New",monospace, 微软雅黑',
  EDITOR_FONT_SIZE: 14,
  config: {}
}

export default contants
