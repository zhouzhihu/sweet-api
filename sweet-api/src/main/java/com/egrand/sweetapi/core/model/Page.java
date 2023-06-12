package com.egrand.sweetapi.core.model;

/**
 * 分页对象
 *
 */
public class Page {

	/**
	 * 当前页
	 */
	private long page;

	/**
	 * 限制条数
	 */
	private long limit;

	/**
	 * 跳过条数
	 */
	private long offset;

	public Page() {
	}

	public Page(long limit, long offset) {
		this.limit = limit;
		this.offset = offset;
		this.page = this.offset/this.limit + 1;
		if (this.page > 2000000000)
			throw new RuntimeException("页码超过最大值2000000000");
		if (this.limit > 2000)
			throw new RuntimeException("分页记录超过最大值2000");
	}

	public static Page createPage(long page, long limit) {
		if (page > 2000000000)
			throw new RuntimeException("页码超过最大值2000000000");
		if (limit > 2000)
			throw new RuntimeException("分页记录超过最大值2000");
		Page newPage = new Page();
		newPage.setPage(page);
		newPage.setLimit(limit);
		newPage.setOffset((page - 1) * limit);
		return newPage;
	}

	public long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public long getPage() {
		return page;
	}

	public void setPage(long page) {
		this.page = page;
	}
}
