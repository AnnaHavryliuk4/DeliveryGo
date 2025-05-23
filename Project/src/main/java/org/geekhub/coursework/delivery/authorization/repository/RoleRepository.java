package org.geekhub.coursework.delivery.authorization.repository;

import org.geekhub.coursework.delivery.authorization.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAll();
}
