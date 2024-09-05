package org.geekhub.coursework.delivery.authorization.repository;

import org.geekhub.coursework.delivery.authorization.entities.Role;
import org.geekhub.coursework.delivery.authorization.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findByToken(String token);

    User findByUsername(String username);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r IN :roles")
    List<User> findAllByRoles(@Param("roles") Set<Role> roles);
}
