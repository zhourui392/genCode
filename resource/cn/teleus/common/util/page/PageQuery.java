package cn.teleus.common.util.page;

import java.util.HashMap;
import java.util.Map;

public class PageQuery {
	public PageQuery(){
		this.pageIndex = 1;
	}
	protected Integer pageIndex;
	private Map<String,Object> pageCondition = new HashMap<>();
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
	public void addCondition(String key,Object value){
		pageCondition.put(key,value);
	}

	public Map<String, Object> getPageCondition() {
		return pageCondition;
	}
}
