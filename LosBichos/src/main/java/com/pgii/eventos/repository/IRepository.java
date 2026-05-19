package com.pgii.eventos.repository;

import java.util.List;

public interface IRepository<T> {
    T findById(String id);
    List<T> findAll();
    void save(T entity);
    void deleteById(String id);
}
