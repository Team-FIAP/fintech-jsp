package com.fiap.fintechjsp.dao;

import java.util.List;

public interface BaseDao<T, ID> {
    public T findById(ID id);
    public List<T> findAll();
    public T insert(T entity);
    public T update(T entity);
    public void delete(T entity);
}
