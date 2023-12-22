package com.langworthytech.bytebistro.repository;

import com.langworthytech.bytebistro.model.MenuItem;
import org.springframework.data.repository.CrudRepository;

public interface MenuItemRepository extends CrudRepository<MenuItem, Long> {
}
