package com.undercontroll.api.repository;

import com.undercontroll.api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUser_id(Integer userId);

    // Query responsavel por retornar o total de todos os componentes de todos os items relacionados a um pedido
    @Query(value = """
    SELECT COALESCE(SUM(c.price * d.quantity), 0.0)
    FROM orders o
    INNER JOIN order_items oi ON o.id = oi.order_id
    INNER JOIN demands d ON oi.id = d.order_item_id
    INNER JOIN components c ON d.component_id = c.id
    WHERE o.user_id = :userId
    """, nativeQuery = true)
    Double calculatePartsTotalByUserId(@Param("userId") Integer userId);

    @Query("""
            SELECT SUM(o.laborValue)
            FROM Order o
            WHERE o.user.id = :userId
            """)
    Double calculateTotalLaborByUserId(@Param("userId") Integer userId);




}
