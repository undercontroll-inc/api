package com.undercontroll.infrastructure.gateway;

import com.undercontroll.domain.gateway.MarketViewGateway;
import com.undercontroll.domain.model.market.MarketBrandSummary;
import com.undercontroll.domain.model.market.MarketCategorySummary;
import com.undercontroll.domain.model.market.MarketPriceMovement;
import com.undercontroll.domain.model.market.MarketProductCurrent;
import com.undercontroll.domain.model.market.MarketRisingProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MarketViewGatewayImpl implements MarketViewGateway {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Optional<String> findCurrentBucketKey() {
        return Optional.ofNullable(safe(
                () -> jdbc.getJdbcTemplate().queryForObject(
                        "SELECT MAX(bucket_key) FROM vw_market_product_current",
                        String.class),
                null));
    }

    @Override
    public Optional<String> findPreviousBucketKey(String currentBucketKey) {
        if (currentBucketKey == null) {
            return Optional.empty();
        }
        MapSqlParameterSource params = new MapSqlParameterSource("bucket", currentBucketKey);
        String value = safe(
                () -> jdbc.queryForObject(
                        """
                        SELECT previous_bucket_key
                        FROM vw_market_price_movement
                        WHERE bucket_key = :bucket
                          AND previous_bucket_key IS NOT NULL
                        LIMIT 1
                        """,
                        params,
                        String.class),
                null);
        return Optional.ofNullable(value);
    }

    @Override
    public long countCurrentProducts() {
        Long count = safe(
                () -> jdbc.getJdbcTemplate().queryForObject(
                        "SELECT COUNT(*) FROM vw_market_product_current",
                        Long.class),
                0L);
        return count == null ? 0L : count;
    }

    @Override
    public long countDistinctBrands() {
        Long count = safe(
                () -> jdbc.getJdbcTemplate().queryForObject(
                        "SELECT COUNT(DISTINCT brand_slug) FROM vw_market_product_current WHERE brand_slug IS NOT NULL",
                        Long.class),
                0L);
        return count == null ? 0L : count;
    }

    @Override
    public List<MarketBrandSummary> findTopBrands(String bucketKey, int limit) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("bucket", bucketKey)
                .addValue("limit", limit);
        return safeList(() -> jdbc.query(
                """
                SELECT brand_slug, bucket_key, brand_name, product_count, domain_count, avg_score, best_rank,
                       avg_price_median, price_floor, price_ceiling, avg_price_delta_pct, avg_discount_pct, rising_count
                FROM vw_market_brand_summary
                WHERE bucket_key = :bucket
                ORDER BY product_count DESC, avg_score DESC
                LIMIT :limit
                """,
                params,
                (rs, rowNum) -> mapBrand(rs)));
    }

    @Override
    public List<MarketCategorySummary> findTopCategories(String bucketKey, int limit) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("bucket", bucketKey)
                .addValue("limit", limit);
        return safeList(() -> jdbc.query(
                """
                SELECT domain_id, bucket_key, category_name_sample, product_count, brand_count, avg_score, max_score,
                       avg_price_median, price_floor, price_ceiling, avg_offer_count, avg_price_delta_pct,
                       avg_discount_pct, rising_count, falling_count, high_confidence_count
                FROM vw_market_category_summary
                WHERE bucket_key = :bucket
                ORDER BY product_count DESC, avg_score DESC
                LIMIT :limit
                """,
                params,
                (rs, rowNum) -> mapCategory(rs)));
    }

    @Override
    public List<MarketProductCurrent> findAllCurrentProducts() {
        return safeList(() -> jdbc.getJdbcTemplate().query(
                """
                SELECT bucket_key, source, external_type, external_id, title, canonical_title, rank, rank_band,
                       rank_delta, trajectory_label, score, priority_label, confidence, domain_id, category_id,
                       category_name, brand, brand_slug, model, product_key, voltage, power_watts, capacity_value,
                       capacity_unit, energy_efficiency, price, price_min, price_median, price_max, price_delta_pct,
                       discount_pct, offer_count, seller_count, relevance, domain_reason, permalink
                FROM vw_market_product_current
                """,
                (rs, rowNum) -> mapProduct(rs)));
    }

    @Override
    public List<MarketPriceMovement> findPriceMovements(String bucketKey) {
        MapSqlParameterSource params = new MapSqlParameterSource("bucket", bucketKey);
        return safeList(() -> jdbc.query(
                """
                SELECT domain_id, bucket_key, previous_bucket_key, product_count, offer_count, seller_count,
                       avg_price_median, previous_price_median, price_delta_pct, offer_delta_pct
                FROM vw_market_price_movement
                WHERE bucket_key = :bucket
                  AND previous_price_median IS NOT NULL
                  AND product_count >= 2
                ORDER BY price_delta_pct DESC NULLS LAST
                """,
                params,
                (rs, rowNum) -> mapPriceMovement(rs)));
    }

    @Override
    public List<MarketPriceMovement> findStockOpportunities(String bucketKey) {
        MapSqlParameterSource params = new MapSqlParameterSource("bucket", bucketKey);
        return safeList(() -> jdbc.query(
                """
                SELECT domain_id, bucket_key, previous_bucket_key, product_count, offer_count, seller_count,
                       avg_price_median, previous_price_median, price_delta_pct, offer_delta_pct
                FROM vw_market_price_movement
                WHERE bucket_key = :bucket
                  AND price_delta_pct < 0
                  AND offer_delta_pct > 0
                ORDER BY price_delta_pct ASC
                """,
                params,
                (rs, rowNum) -> mapPriceMovement(rs)));
    }

    @Override
    public List<MarketRisingProduct> findRisingProductsHighConfidence() {
        return safeList(() -> jdbc.getJdbcTemplate().query(
                """
                SELECT bucket_key, external_type, external_id, title, brand, brand_slug, model, product_key,
                       domain_id, category_id, category_name, rank, rank_band, rank_delta, trajectory_label,
                       score, priority_label, confidence, price_median, price_min, price_max, price_delta_pct,
                       discount_pct, offer_count, seller_count, capacity_value, capacity_unit, power_watts,
                       voltage, permalink
                FROM vw_market_rising_products
                WHERE confidence = 'HIGH'
                ORDER BY score DESC
                """,
                (rs, rowNum) -> mapRising(rs)));
    }

    @Override
    public List<MarketBrandSummary> findBrandMomentum(String bucketKey) {
        MapSqlParameterSource params = new MapSqlParameterSource("bucket", bucketKey);
        return safeList(() -> jdbc.query(
                """
                SELECT brand_slug, bucket_key, brand_name, product_count, domain_count, avg_score, best_rank,
                       avg_price_median, price_floor, price_ceiling, avg_price_delta_pct, avg_discount_pct, rising_count
                FROM vw_market_brand_summary
                WHERE bucket_key = :bucket
                ORDER BY rising_count DESC, product_count DESC
                """,
                params,
                (rs, rowNum) -> mapBrand(rs)));
    }

    @Override
    public List<MarketCategorySummary> findCategorySummary(String bucketKey) {
        MapSqlParameterSource params = new MapSqlParameterSource("bucket", bucketKey);
        return safeList(() -> jdbc.query(
                """
                SELECT domain_id, bucket_key, category_name_sample, product_count, brand_count, avg_score, max_score,
                       avg_price_median, price_floor, price_ceiling, avg_offer_count, avg_price_delta_pct,
                       avg_discount_pct, rising_count, falling_count, high_confidence_count
                FROM vw_market_category_summary
                WHERE bucket_key = :bucket
                """,
                params,
                (rs, rowNum) -> mapCategory(rs)));
    }

    @Override
    public List<MarketProductCurrent> findPriceDispersion() {
        return safeList(() -> jdbc.getJdbcTemplate().query(
                """
                SELECT bucket_key, source, external_type, external_id, title, canonical_title, rank, rank_band,
                       rank_delta, trajectory_label, score, priority_label, confidence, domain_id, category_id,
                       category_name, brand, brand_slug, model, product_key, voltage, power_watts, capacity_value,
                       capacity_unit, energy_efficiency, price, price_min, price_median, price_max, price_delta_pct,
                       discount_pct, offer_count, seller_count, relevance, domain_reason, permalink
                FROM vw_market_product_current
                WHERE external_type = 'PRODUCT'
                  AND offer_count >= 3
                  AND price_min IS NOT NULL
                  AND price_max IS NOT NULL
                """,
                (rs, rowNum) -> mapProduct(rs)));
    }

    @Override
    public List<MarketCategorySummary> findUncoveredCategories(String bucketKey, Collection<String> clientDomainIds) {
        Collection<String> domains = (clientDomainIds == null || clientDomainIds.isEmpty())
                ? List.of("__none__")
                : clientDomainIds;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("bucket", bucketKey)
                .addValue("domains", domains);
        return safeList(() -> jdbc.query(
                """
                SELECT domain_id, bucket_key, category_name_sample, product_count, brand_count, avg_score, max_score,
                       avg_price_median, price_floor, price_ceiling, avg_offer_count, avg_price_delta_pct,
                       avg_discount_pct, rising_count, falling_count, high_confidence_count
                FROM vw_market_category_summary
                WHERE bucket_key = :bucket
                  AND rising_count > 0
                  AND domain_id NOT IN (:domains)
                ORDER BY avg_score DESC
                """,
                params,
                (rs, rowNum) -> mapCategory(rs)));
    }

    private MarketBrandSummary mapBrand(ResultSet rs) throws SQLException {
        return new MarketBrandSummary(
                rs.getString("brand_slug"),
                rs.getString("bucket_key"),
                rs.getString("brand_name"),
                toLong(rs.getObject("product_count")),
                toLong(rs.getObject("domain_count")),
                toDouble(rs.getObject("avg_score")),
                toInteger(rs.getObject("best_rank")),
                toDouble(rs.getObject("avg_price_median")),
                toDouble(rs.getObject("price_floor")),
                toDouble(rs.getObject("price_ceiling")),
                toDouble(rs.getObject("avg_price_delta_pct")),
                toDouble(rs.getObject("avg_discount_pct")),
                toLong(rs.getObject("rising_count"))
        );
    }

    private MarketCategorySummary mapCategory(ResultSet rs) throws SQLException {
        return new MarketCategorySummary(
                rs.getString("domain_id"),
                rs.getString("bucket_key"),
                rs.getString("category_name_sample"),
                toLong(rs.getObject("product_count")),
                toLong(rs.getObject("brand_count")),
                toDouble(rs.getObject("avg_score")),
                toDouble(rs.getObject("max_score")),
                toDouble(rs.getObject("avg_price_median")),
                toDouble(rs.getObject("price_floor")),
                toDouble(rs.getObject("price_ceiling")),
                toDouble(rs.getObject("avg_offer_count")),
                toDouble(rs.getObject("avg_price_delta_pct")),
                toDouble(rs.getObject("avg_discount_pct")),
                toLong(rs.getObject("rising_count")),
                toLong(rs.getObject("falling_count")),
                toLong(rs.getObject("high_confidence_count"))
        );
    }

    private MarketPriceMovement mapPriceMovement(ResultSet rs) throws SQLException {
        return new MarketPriceMovement(
                rs.getString("domain_id"),
                rs.getString("bucket_key"),
                rs.getString("previous_bucket_key"),
                toLong(rs.getObject("product_count")),
                toLong(rs.getObject("offer_count")),
                toLong(rs.getObject("seller_count")),
                toDouble(rs.getObject("avg_price_median")),
                toDouble(rs.getObject("previous_price_median")),
                toDouble(rs.getObject("price_delta_pct")),
                toDouble(rs.getObject("offer_delta_pct"))
        );
    }

    private MarketProductCurrent mapProduct(ResultSet rs) throws SQLException {
        return new MarketProductCurrent(
                rs.getString("bucket_key"),
                rs.getString("source"),
                rs.getString("external_type"),
                rs.getString("external_id"),
                rs.getString("title"),
                rs.getString("canonical_title"),
                toInteger(rs.getObject("rank")),
                rs.getString("rank_band"),
                toInteger(rs.getObject("rank_delta")),
                rs.getString("trajectory_label"),
                toDouble(rs.getObject("score")),
                rs.getString("priority_label"),
                rs.getString("confidence"),
                rs.getString("domain_id"),
                rs.getString("category_id"),
                rs.getString("category_name"),
                rs.getString("brand"),
                rs.getString("brand_slug"),
                rs.getString("model"),
                rs.getString("product_key"),
                rs.getString("voltage"),
                toInteger(rs.getObject("power_watts")),
                toDouble(rs.getObject("capacity_value")),
                rs.getString("capacity_unit"),
                rs.getString("energy_efficiency"),
                toDouble(rs.getObject("price")),
                toDouble(rs.getObject("price_min")),
                toDouble(rs.getObject("price_median")),
                toDouble(rs.getObject("price_max")),
                toDouble(rs.getObject("price_delta_pct")),
                toDouble(rs.getObject("discount_pct")),
                toInteger(rs.getObject("offer_count")),
                toInteger(rs.getObject("seller_count")),
                toDouble(rs.getObject("relevance")),
                rs.getString("domain_reason"),
                rs.getString("permalink")
        );
    }

    private MarketRisingProduct mapRising(ResultSet rs) throws SQLException {
        return new MarketRisingProduct(
                rs.getString("bucket_key"),
                rs.getString("external_type"),
                rs.getString("external_id"),
                rs.getString("title"),
                rs.getString("brand"),
                rs.getString("brand_slug"),
                rs.getString("model"),
                rs.getString("product_key"),
                rs.getString("domain_id"),
                rs.getString("category_id"),
                rs.getString("category_name"),
                toInteger(rs.getObject("rank")),
                rs.getString("rank_band"),
                toInteger(rs.getObject("rank_delta")),
                rs.getString("trajectory_label"),
                toDouble(rs.getObject("score")),
                rs.getString("priority_label"),
                rs.getString("confidence"),
                toDouble(rs.getObject("price_median")),
                toDouble(rs.getObject("price_min")),
                toDouble(rs.getObject("price_max")),
                toDouble(rs.getObject("price_delta_pct")),
                toDouble(rs.getObject("discount_pct")),
                toInteger(rs.getObject("offer_count")),
                toInteger(rs.getObject("seller_count")),
                toDouble(rs.getObject("capacity_value")),
                rs.getString("capacity_unit"),
                toInteger(rs.getObject("power_watts")),
                rs.getString("voltage"),
                rs.getString("permalink")
        );
    }

    private <T> T safe(Supplier<T> query, T fallback) {
        try {
            return query.get();
        } catch (DataAccessException ex) {
            log.debug("Market view unavailable: {}", ex.getMessage());
            return fallback;
        }
    }

    private <T> List<T> safeList(Supplier<List<T>> query) {
        List<T> result = safe(query, Collections.emptyList());
        return result == null ? Collections.emptyList() : result;
    }

    private static Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(value.toString());
    }

    private static Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(value.toString());
    }

    private static Double toDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return Double.parseDouble(value.toString());
    }
}
