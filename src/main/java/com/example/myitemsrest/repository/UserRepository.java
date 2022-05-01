package com.example.myitemsrest.repository;


import com.example.myitemsrest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>, QuerydslPredicateExecutor<User> {


    Optional<User> findByEmail(String s);
}
