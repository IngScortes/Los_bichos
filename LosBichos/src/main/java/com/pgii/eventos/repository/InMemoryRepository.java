package com.pgii.eventos.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class InMemoryRepository<T> implements IRepository<T> {
    protected final Map<String, T> storage = new HashMap<>();
    protected final Function<T, String> idExtractor;

    public InMemoryRepository(Function<T, String> idExtractor) {
        this.idExtractor = idExtractor;
    }

    @Override
    public T findById(String id) {
        return storage.get(id);
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void save(T entity) {
        String id = idExtractor.apply(entity);
        if (id != null) {
            storage.put(id, entity);
        }
    }

    @Override
    public void deleteById(String id) {
        storage.remove(id);
    }
}