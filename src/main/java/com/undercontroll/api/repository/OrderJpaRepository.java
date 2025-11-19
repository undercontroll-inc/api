package com.undercontroll.api.repository;

import com.undercontroll.api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUser_id(Integer userId);


    @Query("SELECT o FROM Order o JOIN o.orderItems oi WHERE oi.id = :orderItemId")
    Optional<Order> findOrderByOrderItemId(@Param("orderItemId") Integer orderItemId);

    // Query responsavel por retornar o total de todos os componentes de todos os items relacionados a um pedido
    @Query(value = """
    SELECT COALESCE(SUM(c.price * d.quantity), 0.0)
    FROM `order` o
    INNER JOIN demand d ON o.id = d.order_id
    INNER JOIN component c ON d.component_id = c.id
    WHERE o.id = :orderId
    """, nativeQuery = true)
    Double calculatePartsTotalByOrderId(@Param("orderId") Integer orderId);

    // Query responsavel por retornar a lista de peças utilizadas em todos os items de um pedido específico
    @Query(value = """
    SELECT
        c.id,
        c.name,
        c.description,
        c.brand,
        c.price,
        c.supplier,
        c.category,
        d.quantity
    FROM `order` o
    INNER JOIN demand d ON o.id = d.order_id
    INNER JOIN component c ON d.component_id = c.id
    WHERE o.id = :orderId
    """, nativeQuery = true)
    List<Object[]> findAllPartsByOrderIdNative(@Param("orderId") Integer orderId);





}
