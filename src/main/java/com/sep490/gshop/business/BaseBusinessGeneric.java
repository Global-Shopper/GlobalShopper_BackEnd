package com.sep490.gshop.business;

import java.util.List;
import java.util.Optional;

public interface BaseBusinessGeneric<T, ID> {
    Optional<T> getById(ID id);
    List<T> getAll();
    T create(T entity);
    T update(T entity);
    boolean delete(ID id);
    boolean existsById(ID id);
    long count();
    List<T> saveAll(List<T> entities);
}
