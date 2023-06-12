package com.egrand.sweetapi.core.config;

import com.egrand.sweetapi.core.web.RequestHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.egrand.sweetapi.core.LocalFileService.FILE_SERVICE_TYPE_LOCAL;

/**
 * dynamic-api配置信息
 *
 */
@ConfigurationProperties(prefix = "dynamic-api")
public class DynamicAPIProperties {

	/**
	 * 版本号
	 */
	private final String version = RequestHandler.class.getPackage().getImplementationVersion();
	/**
	 * 是否抛出异常
	 */
	private boolean throwException = false;
	/**
	 * 不接收未经定义的参数
	 */
	private boolean disabledUnknownParameter = false;
	/**
	 * 自动导入的模块,多个用","分隔
	 */
	private String autoImportModule = "db";
	/**
	 * 可自动导入的包（目前只支持以.*结尾的通配符），多个用","分隔
	 */
	private String autoImportPackage;
	/**
	 * SQL列名转换
	 */
	private String sqlColumnCase = "default";

	/**
	 * 是否要打印SQL
	 */
	private boolean showSql = true;

	/**
	 * 是否允许覆盖应用接口，默认为false
	 *
	 */
	private boolean allowOverride = false;

	/**
	 * 文件服务类型（ATM：对接文件管理中心，LOCAL：本地文件）,默认为LOCAL
	 */
	private String fileServiceType = FILE_SERVICE_TYPE_LOCAL;

	/**
	 * 定时拉取数据表达式
	 */
	private String cron = "";

	@NestedConfigurationProperty
	private Crud crud = new Crud();

	public String getSqlColumnCase() {
		return sqlColumnCase;
	}

	public void setSqlColumnCase(String sqlColumnCase) {
		this.sqlColumnCase = sqlColumnCase;
	}

	public List<String> getAutoImportModuleList() {
		return Arrays.asList(autoImportModule.replaceAll("\\s", "").split(","));
	}

	public List<String> getAutoImportPackageList() {
		if (autoImportPackage == null) {
			return Collections.emptyList();
		}
		return Arrays.asList(autoImportPackage.replaceAll("\\s", "").split(","));
	}

	public String getVersion() {
		return version;
	}

	public boolean isThrowException() {
		return throwException;
	}

	public void setThrowException(boolean throwException) {
		this.throwException = throwException;
	}

	public String getAutoImportModule() {
		return autoImportModule;
	}

	public void setAutoImportModule(String autoImportModule) {
		this.autoImportModule = autoImportModule;
	}

	public String getAutoImportPackage() {
		return autoImportPackage;
	}

	public void setAutoImportPackage(String autoImportPackage) {
		this.autoImportPackage = autoImportPackage;
	}

	public boolean isShowSql() {
		return showSql;
	}

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

	public Crud getCrud() {
		return crud;
	}

	public void setCrud(Crud crud) {
		this.crud = crud;
	}

	public boolean isDisabledUnknownParameter() {
		return disabledUnknownParameter;
	}

	public void setDisabledUnknownParameter(boolean disabledUnknownParameter) {
		this.disabledUnknownParameter = disabledUnknownParameter;
	}

	public boolean isAllowOverride() {
		return allowOverride;
	}

	public void setAllowOverride(boolean allowOverride) {
		this.allowOverride = allowOverride;
	}

	public String getFileServiceType() {
		return fileServiceType;
	}

	public void setFileServiceType(String fileServiceType) {
		this.fileServiceType = fileServiceType;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}
}
