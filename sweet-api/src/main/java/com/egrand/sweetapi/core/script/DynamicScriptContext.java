package com.egrand.sweetapi.core.script;


import javax.script.ScriptException;
import java.util.Map;

/**
 * 动态脚本上下文
 */
public interface DynamicScriptContext {

    /**
     * 获取脚本名称
     * @return 脚本名
     */
    String getScriptName();

    /**
     * 设置脚本名称
     * @param scriptName 脚本名
     */
    void setScriptName(String scriptName);

    /**
     * 获取当前作用域内的String变量值
     * @param name 变量名称
     * @return 变量值
     */
    String getString(String name);

    /**
     * 获取当前作用域内的变量值
     *
     * @param name 变量名称
     * @return 变量值
     */
    Object get(String name);

    /**
     * 设置当前作用域内的变量值
     * @param name 变量名
     * @param value 变量值
     * @return
     */
    DynamicScriptContext set(String name, Object value);

    /**
     * 获取调用时传入的变量信息
     * @return
     */
    Map<String, Object> getRootVariables();

    /**
     * 批量设置环境变量
     * @param map
     */
    void putMapIntoContext(Map<String, Object> map);

    /**
     * 添加 .* 的导包
     * @param packageName 包名 如 java.text.
     */
    void addImport(String packageName);

    /**
     * 获取导包类
     * @param simpleClassName 类名
     * @return
     */
    Class<?> getImportClass(String simpleClassName);

    /**
     * 运行脚本
     * @param script 脚本
     * @return
     */
    Object execute(String script) throws ScriptException;

    /**
     * 执行表达式
     * @param script
     * @return
     * @throws ScriptException
     */
    Object executeExpression(String script) throws ScriptException;

}
