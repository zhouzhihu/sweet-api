package com.egrand.sweetapi.modules.db;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 事务模块
 *
 */
public class Transaction {

	private static final TransactionDefinition TRANSACTION_DEFINITION = new DefaultTransactionDefinition();
	private final DataSourceTransactionManager dataSourceTransactionManager;
	private final TransactionStatus transactionStatus;

	public Transaction(DataSourceTransactionManager dataSourceTransactionManager) {
		this.dataSourceTransactionManager = dataSourceTransactionManager;
		this.transactionStatus = dataSourceTransactionManager.getTransaction(TRANSACTION_DEFINITION);
	}

	/**
	 * 回滚事务
	 */
	public void rollback() {
		this.dataSourceTransactionManager.rollback(this.transactionStatus);
	}

	/**
	 * 提交事务
	 */
	public void commit() {
		this.dataSourceTransactionManager.commit(this.transactionStatus);
	}
}
