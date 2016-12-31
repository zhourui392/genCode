package com.teleus.common.util.page;

public class PageQuery {
	public PageQuery(){
		this.pageIndex = 1;
	}
	protected Integer pageIndex;
	protected int limit = 15;
	public int getPageOffset() {
		return (this.pageIndex-1)<0?0:(this.pageIndex-1)*this.limit;
	}
	public Integer getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}
	public int getLimit() {
        return limit;
    }
    public void setLimit(int limit) {
        this.limit = limit;
    }
}
