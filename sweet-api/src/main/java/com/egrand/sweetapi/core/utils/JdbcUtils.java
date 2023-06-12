package com.egrand.sweetapi.core.utils;

import com.egrand.sweetapi.core.exception.APIException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jdbc.DatabaseDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcUtils {

	public static Connection getConnection(String driver, String url, String username, String password) {
		try {
			if (StringUtils.isBlank(driver)) {
				driver = DatabaseDriver.fromJdbcUrl(url).getDriverClassName();
				if (StringUtils.isBlank(driver)) {
					throw new APIException("无法从url中获得驱动类");
				}
			}
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			throw new APIException("找不到驱动：" + driver);
		}
		try {
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			throw new APIException("获取Jdbc链接失败：" + e.getMessage());
		}
	}

	public static void close(Connection connection) {
		try {
			connection.close();
		} catch (Exception ignored) {

		}
	}
}
