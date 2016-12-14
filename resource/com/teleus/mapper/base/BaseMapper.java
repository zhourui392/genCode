package com.teleus.mapper.base;

public interface BaseMapper<T> {
	T selectByPrimaryKey( Long id );

	T selectByPrimaryKey( String id );

	T selectByPrimaryKey( Integer id );

	int deleteByPrimaryKey( Long id );

	int deleteByPrimaryKey( String id );

	int deleteByPrimaryKey( Integer id );

	int insert( T record );

	int insertSelective( T record );

	int updateByPrimaryKey( T record );
	
	int updateByPrimaryKeySelective( T record);
}
