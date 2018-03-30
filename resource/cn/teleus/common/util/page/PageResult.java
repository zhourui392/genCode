package cn.teleus.common.util.page;

import java.util.List;

public class PageResult<T> extends PageQuery {
    private List<T> items;
    private int count;
    private boolean lastPageBoolean;
    private int totalPage;
    public boolean isLastPageBoolean() {
    	lastPageBoolean = ( pageIndex == getTotalPage() || getTotalPage() == 0);
		return lastPageBoolean;
	}
	public PageResult() {
    	this.pageIndex = 1;
    }
    public PageResult(List<T> items, int count) {
        this.items = items;
        this.count = count;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public int getTotalPage() {
    	totalPage = (count + getLimit() - 1) / getLimit();
        return totalPage;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    
    public void setPageQuery(PageQuery pageQuery){
    	this.setPageIndex(pageQuery.getPageIndex());
    	this.setLimit(pageQuery.getLimit());
    }
}
