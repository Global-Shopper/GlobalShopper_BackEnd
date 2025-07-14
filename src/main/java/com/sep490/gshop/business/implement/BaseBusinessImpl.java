package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.BaseBusiness;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class BaseBusinessImpl<T, R extends JpaRepository<T, UUID>> implements BaseBusiness<T> {
    protected final R repository;

    protected BaseBusinessImpl(R repository) {
        this.repository = repository;
    }

    @Override
    public Optional<T> getById(UUID id) {
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
    public boolean delete(UUID id) {
        try {
            repository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean existsById(UUID id) {
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
