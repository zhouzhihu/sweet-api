package com.egrand.sweetapi.modules.plugin;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.egrand.sweetapi.core.exception.APIException;
import com.egrand.sweetapi.core.ModuleService;
import com.egrand.sweetplugin.SpringPluginManager;
import org.pf4j.PluginAlreadyLoadedException;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.List;

import static com.egrand.sweetapi.core.utils.Constants.VAR_NAME_MODULE_PLUGIN;

public class PluginModule implements ModuleService {

    private final SpringPluginManager springPluginManager;

    public PluginModule(SpringPluginManager springPluginManager) {
        this.springPluginManager = springPluginManager;
    }

    /**
     * 获取插件的ApplicationContext
     * @param pluginId 插件ID
     * @return ApplicationContext
     */
    public ApplicationContext getApplicationContext(String pluginId) {
        return this.springPluginManager.getPluginApplicationContext(pluginId);
    }

    /**
     * 调用插件方法
     * @param pluginId 插件ID
     * @param beanName 插件内bean名称
     * @param methodName 插件内bean的方法
     * @param args 插件内bean的方法的参数
     * @return
     */
    public Object invoke(String pluginId, String beanName, String methodName, Object ...args) {
        if (this.springPluginManager.getPlugin(pluginId) == null)
            throw new APIException("不存在[" + pluginId + "]插件");
        if (this.springPluginManager.getPlugin(pluginId).getPluginState() != PluginState.STARTED)
            throw new APIException("插件未启动");
        ApplicationContext applicationContext = this.springPluginManager.getPluginApplicationContext(pluginId);
        Object object = applicationContext.getBean(beanName);
        Method[] methods = object.getClass().getMethods();
        Method method = null;
        for (Method tmp : methods) {
            if(tmp.getName().equalsIgnoreCase(methodName)){
                method = tmp;
                break;
            }
        }
        return ReflectUtil.invoke(object, method, args);
    }

    /**
     * 获取所有插件列表
     * @return
     */
    public JSONArray list() {
        JSONArray jsonArray = new JSONArray();
        List<PluginWrapper> pluginWrapperList = this.springPluginManager.getPlugins();
        for (PluginWrapper pluginWrapper : pluginWrapperList) {
            jsonArray.add(this.parsePluginWrapper(pluginWrapper));
        }
        return jsonArray;
    }

    /**
     * 获取指定插件信息
     * @param pluginId 插件ID
     * @return
     */
        public JSONObject get(String pluginId) {
        return this.parsePluginWrapper(this.springPluginManager.getPlugin(pluginId));
    }

    /**
     * 删除插件
     * @param pluginIds 插件ID
     * @return
     */
    public Boolean delete(String ...pluginIds) {
        for (String pluginId : pluginIds) {
            this.springPluginManager.deletePlugin(pluginId);
        }
        return true;
    }

    /**
     * 加载插件
     * @param pluginIds 插件ID
     * @return
     */
    public Boolean load(String ...pluginIds) {
        try {
            if (pluginIds == null || pluginIds.length == 0) {
                this.springPluginManager.loadPlugins();
            } else {
                List<PluginWrapper> pluginWrapperList = this.springPluginManager.getPlugins();
                for (PluginWrapper pluginWrapper : pluginWrapperList) {
                    for (String id : pluginIds) {
                        if (pluginWrapper.getPluginId().equals(id)) {
                            this.springPluginManager.loadPlugin(pluginWrapper.getPluginPath());
                        }
                    }
                }
            }
            return true;
        } catch (PluginAlreadyLoadedException e) {
            return false;
        }
    }

    /**
     * 卸载插件
     * @param pluginIds 插件ID
     * @return
     */
    public Boolean unload(String ...pluginIds) {
        if (pluginIds == null || pluginIds.length == 0) {
            this.springPluginManager.unloadPlugins();
        } else {
            for (String pluginId : pluginIds) {
                this.springPluginManager.unloadPlugin(pluginId);
            }
        }
        return true;
    }

    /**
     * 启动插件
     * @param pluginIds 插件ID
     * @return
     */
    public Boolean start(String ...pluginIds) {
        if (pluginIds == null || pluginIds.length == 0) {
            this.springPluginManager.startPlugins();
        } else {
            for (String pluginId : pluginIds) {
                this.springPluginManager.startPlugin(pluginId);
            }
        }
        return true;
    }

    /**
     * 停止插件
     * @param pluginIds 插件ID
     * @return
     */
    public Boolean stop(String ...pluginIds) {
        if (pluginIds == null || pluginIds.length == 0) {
            this.springPluginManager.stopPlugins();
        } else {
            for (String pluginId : pluginIds) {
                this.springPluginManager.stopPlugin(pluginId);
            }
        }
        return true;
    }

    /**
     * 上传插件
     * @param file 文件
     * @return
     */
    public String uploadFile(MultipartFile file) {
        return this.springPluginManager.upload(file);
    }

    private JSONObject parsePluginWrapper(PluginWrapper pluginWrapper) {
        if (null == pluginWrapper)
            return new JSONObject();
        JSONObject jsonObject = JSONUtil.parseObj(pluginWrapper.getDescriptor());
        jsonObject.putOnce("pluginState", pluginWrapper.getPluginState().toString());
        jsonObject.putOnce("pluginPath", pluginWrapper.getPluginPath().toUri().getPath());
        return jsonObject;
    }

    @Override
    public String getType() {
        return VAR_NAME_MODULE_PLUGIN;
    }

}
