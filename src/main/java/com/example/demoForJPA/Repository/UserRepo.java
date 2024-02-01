package com.example.demoForJPA.Repository;

import com.example.demoForJPA.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo  extends JpaRepository<User,Long> {

}
