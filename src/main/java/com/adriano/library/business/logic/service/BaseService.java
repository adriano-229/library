package com.adriano.library.business.logic.service;

import com.adriano.library.business.domain.entity.BaseEntity;
import com.adriano.library.business.persistence.repository.BaseRepository;

import java.util.List;
import java.util.Optional;


public abstract class BaseService<T extends BaseEntity, ID> implements CrudHooks<T, ID> {

    protected final BaseRepository<T, ID> baseRepository;

    public BaseService(BaseRepository<T, ID> baseRepository) {
        this.baseRepository = baseRepository;
    }

    public List<T> findAll() {
        return baseRepository.findAll();
    }

    public Optional<T> findById(ID id) {
        beforeRead(id);
        Optional<T> entity = baseRepository.findById(id);
        entity.ifPresent(this::afterRead);
        return entity;
    }

    public void save(T entity) {
        beforeSave(entity);
        T saved = baseRepository.save(entity);
        afterSave(saved);
    }

    public void update(ID id, T newEntity) {
        beforeUpdate(id, newEntity);
        baseRepository.findById(id).map(existing -> {
            newEntity.setId(existing.getId()); // ensure ID stays the same
            T updated = baseRepository.save(newEntity);
            afterUpdate(updated);
            return updated;
        });
    }

    public void deleteById(ID id) {
        beforeDelete(id);
        baseRepository.deleteById(id);
        afterDelete(id);
    }
}