package com.sp222kh.investigitor.repositories;

import com.sp222kh.investigitor.models.Status;
import org.springframework.data.repository.CrudRepository;

public interface StatusRepository extends CrudRepository<Status, Long> {
    Status findByName(String name);
}
