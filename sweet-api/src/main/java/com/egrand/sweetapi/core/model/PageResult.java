package com.egrand.sweetapi.core.model;

import com.egrand.sweetapi.core.context.RequestEntity;
import com.egrand.sweetapi.core.IPageResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页执行结果
 *
 */
public class PageResult<T> implements IPageResult<T> {

	/**
	 * 当前页
	 */
	private long page;

	/**
	 * 总页数
	 */
	private long pages;

	/**
	 * 限制条数
	 */
	private long limit;

	/**
	 * 总条数
	 */
	private long total;

	/**
	 * 数据项
	 */
	private List<T> records;

	/**
	 * 正序
	 */
	private String ascs = "";

	/**
	 * 倒序
	 */
	private String descs = "";

	/**
	 * 额外数据
	 */
	private Map<String, Object> extra = new HashMap<>();

	public PageResult(RequestEntity requestEntity, Page page, long total, List<T> list) {
		this.total = total;
		this.records = list;
		this.limit = page.getLimit();
		this.page = page.getPage();
		this.pages = this.total/this.limit + 1;
		if (requestEntity.getRequestBody() instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) requestEntity.getRequestBody();
			if (map.containsKey("ascs")) {
				this.ascs = (String) map.get("ascs");
			}
			if (map.containsKey("descs")) {
				this.descs = (String) map.get("descs");
			}
		}
	}

	public PageResult() {
	}

	@Override
	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	@Override
	public List<T> getRecords() {
		return records;
	}

	public void setRecords(List<T> records) {
		this.records = records;
	}

	@Override
	public long getPage() {
		return page;
	}

	public void setPage(long page) {
		this.page = page;
	}

	@Override
	public long getPages() {
		return pages;
	}

	public void setPages(long pages) {
		this.pages = pages;
	}

	@Override
	public long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	@Override
	public String getAscs() {
		return ascs;
	}

	public void setAscs(String ascs) {
		this.ascs = ascs;
	}

	@Override
	public String getDescs() {
		return descs;
	}

	public void setDescs(String descs) {
		this.descs = descs;
	}

	public Map<String, Object> getExtra() {
		return extra;
	}

	public void setExtra(Map<String, Object> extra) {
		this.extra = extra;
	}

	public void putExtra(String key, Object data) {
		this.extra.put(key, data);
	}
}
