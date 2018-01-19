package com.teleus.service.base.impl;


import com.teleus.common.util.page.PageQuery;
import com.teleus.common.util.page.PageResult;
import com.teleus.service.base.BaseService;
import com.teleus.mapper.base.BaseMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhourui
 * @param <T>
 */
public abstract class BaseServiceImpl<T> implements BaseService<T> {
	public abstract BaseMapper<T> getBaseMapper();
	
	@Override
	public T getById(Long id) {
		return getBaseMapper().selectByPrimaryKey(id);
	}
	
	@Override
	public T getById(Integer id) {
		return getBaseMapper().selectByPrimaryKey(id);
	}

	@Override
	public T getById(String id) {
		return getBaseMapper().selectByPrimaryKey(id);
	}

	@Override
	public Boolean update(T record) {
		return getBaseMapper().updateByPrimaryKeySelective(record) > -1;
	}

	@Override
	public Boolean deleteById(Long id) {
		return getBaseMapper().deleteByPrimaryKey(id) > -1;
	}
	
	@Override
	public Boolean deleteById(Integer id) {
		return getBaseMapper().deleteByPrimaryKey(id) > -1;
	}

	@Override
	public Boolean deleteById(String id) {
		return getBaseMapper().deleteByPrimaryKey(id) > -1;
	}

	@Override
	public Boolean add(T record) {
		return getBaseMapper().insertSelective(record) > 0;
	}

	@Override
	public PageResult<T> getPageList(PageQuery pageQuery){
		PageResult<T> result = new PageResult();
		result.setPageQuery(pageQuery);
		Map<String,Object> daoParams = new HashMap<>();
		daoParams.put("limit",pageQuery.getLimit());
		daoParams.put("pageOffset",pageQuery.getPageOffset());
		daoParams.putAll(pageQuery.getPageCondition());

		int totalCount = getBaseMapper().getCountByConditions(daoParams);
		result.setCount(totalCount);
		if (totalCount == 0){
			return result;
		}
		List<T> list = getBaseMapper().getListByConditions(daoParams);
		result.setItems(list);
		return result;
	}
	
}