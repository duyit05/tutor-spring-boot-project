package com.project.tutor.repository;

import com.project.tutor.dto.UserDTO;
import com.project.tutor.model.User;
import org.apache.tomcat.util.buf.UEncoder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByUsername (String username);
    public User findByEmail (String email);

    public List<User> findByUsernameContaining(String username , Pageable pageable);

}
