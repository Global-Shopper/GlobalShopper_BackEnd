package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.BaseBusinessGeneric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public abstract class BaseBusinessGenericImpl<T, ID, R extends JpaRepository<T, ID>> implements BaseBusinessGeneric<T, ID> {
    protected final R repository;

    protected BaseBusinessGenericImpl(R repository) {
        this.repository = repository;
    }

    @Override
    public Optional<T> getById(ID id) {
        return repository.findById(id);
    }

    @Override
    public List<T> getAll() {
        return repository.findAll();
    }

    @Override
    public T create(T entity) {
        return repository.save(entity);
    }

    @Override
    public T update(T entity) {
        return repository.save(entity);
    }

    @Override
    public boolean delete(ID id) {
        try {
            repository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public List<T> saveAll(List<T> entities) {
        return repository.saveAll(entities);
    }
}
