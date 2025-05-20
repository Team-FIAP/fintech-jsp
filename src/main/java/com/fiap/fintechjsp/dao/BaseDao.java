package com.fiap.fintechjsp.dao;

import com.fiap.fintechjsp.exception.DBException;

import java.util.List;

public interface BaseDao<T, ID> {
    public T findById(ID id);
    public List<T> findAll();
    public T insert(T entity) throws DBException;
    public T update(T entity) throws  DBException;
    public void delete(T entity) throws DBException;
}
