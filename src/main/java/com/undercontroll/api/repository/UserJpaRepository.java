package com.undercontroll.api.repository;

import com.undercontroll.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Integer> {

    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByPhone(String phone);
    Optional<User> findUserByCpf(String cpf);

    @Query("""
    SELECT u FROM User u WHERE u.userType = "CUSTOMER"
""")
    List<User> findAllCustomers();


    @Query("""
    SELECT u FROM User u
    WHERE u.userType = 'CUSTOMER' AND u.id = :id
""")
    Optional<User> findCustomerById(@Param("id") Integer id);


}
