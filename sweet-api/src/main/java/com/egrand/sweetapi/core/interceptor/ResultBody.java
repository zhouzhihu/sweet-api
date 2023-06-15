package com.egrand.sweetapi.core.interceptor;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author admin
 */
public class ResultBody<T> implements Serializable {
    private static final long serialVersionUID = -6190689122701100762L;

    /**
     * 响应编码
     */
    private int code = 0;
    /**
     * 提示消息
     */
    private String message;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 响应数据
     */
    private T data;

    /**
     * http状态码
     */
    private int httpStatus;

    /**
     * 附加数据
     */
    private Map<String, Object> extra;

    /**
     * 响应时间
     */
    private final long timestamp = System.currentTimeMillis();

    @JsonIgnore
    private Boolean defExec = true;

    public ResultBody() {
        super();
        this.defExec = false;
    }

    public ResultBody(int code, T data, String message){
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public ResultBody(int code, T data, String message, boolean defExec){
        this.code = code;
        this.data = data;
        this.message = message;
        this.defExec = defExec;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public T getData() {
        return data;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    public int getHttpStatus() {
        return httpStatus;
    }

    public Boolean getDefExec() {
        return this.defExec;
    }

    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    public boolean isOk() {
        return this.code == ErrorCode.OK.getCode();
    }


    public static <T> ResultBody<T> ok() {
        return new ResultBody<T>().code(ErrorCode.OK.getCode()).msg(ErrorCode.OK.getMessage());
    }

    public static <T> ResultBody<T> failed() {
        return new ResultBody<T>().code(ErrorCode.FAIL.getCode()).msg(ErrorCode.FAIL.getMessage());
    }

    public static <E> ResultBody<E> successDef() {
        return new ResultBody(0, (Object)null, "ok", true);
    }

    public static <E> ResultBody<E> successDef(E data) {
        return new ResultBody(0, data, "ok", true);
    }

    public static <E> ResultBody<E> successDef(E data, String msg) {
        return new ResultBody(0, data, msg, true);
    }

    public ResultBody<T> code(int code) {
        this.code = code;
        return this;
    }

    public ResultBody<T> msg(String message) {
        this.message = message;
        return this;
    }

    public ResultBody<T> msg(String message, Object... args) {
        this.message = String.format(message, args);
        return this;
    }

    public ResultBody<T> data(T data) {
        this.data = data;
        return this;
    }

    public ResultBody<T> path(String path) {
        this.path = path;
        return this;
    }

    public ResultBody<T> httpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public ResultBody<T> defExec(Boolean defExec){
        this.defExec = defExec;
        return this;
    }

    public ResultBody<T> put(String key, Object value) {
        if (this.extra == null) {
            this.extra = new HashMap<>();
        }
        this.extra.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "ResultBody{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", data=" + data +
                ", httpStatus=" + httpStatus +
                ", extra=" + extra +
                ", timestamp=" + timestamp +
                '}';
    }
}
