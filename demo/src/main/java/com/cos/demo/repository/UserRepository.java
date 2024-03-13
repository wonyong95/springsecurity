package com.cos.demo.repository;

import com.cos.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

// CRUD 함수를 Jpa
public interface UserRepository extends JpaRepository<User,Integer> {

}
