package cn.teleus.service.base;

import cn.teleus.common.util.page.PageQuery;
import cn.teleus.common.util.page.PageResult;

public interface BaseService<T> {
	
	public T getById(Long id);
	
	public T getById(Integer id);

	public T getById(String id);

	public Boolean update(T record);

	public Boolean deleteById(Long id);
	
	public Boolean deleteById(Integer id);

	public Boolean deleteById(String id);

	public Boolean add(T record);

	PageResult<T> getPageList(PageQuery pageQuery);
}
