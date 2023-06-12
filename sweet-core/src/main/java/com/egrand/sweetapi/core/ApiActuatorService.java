package com.egrand.sweetapi.core;

import javax.script.ScriptException;
import java.util.Map;

/**
 * API执行器，利用该执行器接口可以执行API脚本，用于扩展API执行器时调用
 */
public interface ApiActuatorService {

    /**
     * 执行指定指定请求方法和路径
     * @param method 请求方法
     * @param path 路径
     * @param <T>
     * @return
     * @throws ScriptException
     */
    <T> T execute(String method, String path) throws ScriptException;

    /**
     * 执行指定指定请求方法和路径
     * @param method 请求方法
     * @param path 路径
     * @param context 参数
     * @param <T>
     * @return
     * @throws ScriptException
     */
    <T> T execute(String method, String path, Map<String, Object> context) throws ScriptException;

    /**
     * 执行指定API
     * @param apiId API ID
     * @param context 参数
     * @param <T>
     * @return
     * @throws ScriptException
     */
    <T> T execute(Long apiId, Map<String, Object> context) throws ScriptException;
}
