package com.teleus.service.impl;


import com.teleus.mapper.base.BaseMapper;
import com.teleus.service.BaseService;

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
	
}