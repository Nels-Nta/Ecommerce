package com.ntabana.backend.cart;

import com.ntabana.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<Cart,Long> {
    boolean existsByCheckedIsTrueAndUser(User user);

    Optional<Cart> findByUser(User user);

    boolean existsByCheckedIsFalseAndUser(User user);

   Optional<Cart> findByUserAndCheckedIsFalse(User user);
   List<Cart> findAllByCheckedIsTrue();

    ;

}
