package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select count(u) > 0 " +
            "from User u " +
            "where lower(u.email) = lower(:email)")
    boolean emailExists(@Param("email") String email);


}
