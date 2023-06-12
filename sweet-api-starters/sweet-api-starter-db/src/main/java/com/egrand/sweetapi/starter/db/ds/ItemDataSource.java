package com.egrand.sweetapi.starter.db.ds;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ItemDataSource extends AbstractDataSource implements Closeable {

    private String name;

    private DataSource realDataSource;

    private DataSource dataSource;

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return dataSource.getConnection(username, password);
    }

    @Override
    public void close() {
        Class<? extends DataSource> clazz = realDataSource.getClass();
        try {
            Method closeMethod = clazz.getDeclaredMethod("close");
            closeMethod.invoke(dataSource);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.warn("dynamic-datasource close the datasource named [{}] failed,", name, e);
        }
    }
}
