package com.langworthytech.bytebistro.repository;

import com.langworthytech.bytebistro.model.MenuItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MenuItemRepository extends CrudRepository<MenuItem, Long> {

    List<MenuItem> findAll();
}
