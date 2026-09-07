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

    private static final String PARAM_BUCKET = "bucket";
    private static final String COL_BUCKET_KEY = "bucket_key";
    private static final String COL_BRAND_SLUG = "brand_slug";
    private static final String COL_DOMAIN_ID = "domain_id";
    private static final String COL_PRODUCT_COUNT = "product_count";
    private static final String COL_AVG_PRICE_MEDIAN = "avg_price_median";
    private static final String COL_OFFER_COUNT = "offer_count";
    private static final String COL_SELLER_COUNT = "seller_count";
    private static final String COL_PRICE_DELTA_PCT = "price_delta_pct";

    private static final String SQL_CURRENT_BUCKET =
            "SELECT MAX(bucket_key) FROM vw_market_product_current";
    private static final String SQL_COUNT_PRODUCTS =
            "SELECT COUNT(*) FROM vw_market_product_current";
    private static final String SQL_COUNT_BRANDS =
            "SELECT COUNT(DISTINCT brand_slug) FROM vw_market_product_current WHERE brand_slug IS NOT NULL";
    private static final String SQL_PREVIOUS_BUCKET = """
            SELECT previous_bucket_key
            FROM vw_market_price_movement
            WHERE bucket_key = :bucket
              AND previous_bucket_key IS NOT NULL
            LIMIT 1
            """;
    private static final String SQL_TOP_BRANDS = """
            SELECT brand_slug, bucket_key, brand_name, product_count, domain_count, avg_score, best_rank,
                   avg_price_median, price_floor, price_ceiling, avg_price_delta_pct, avg_discount_pct, rising_count
            FROM vw_market_brand_summary
            WHERE bucket_key = :bucket
            ORDER BY product_count DESC, avg_score DESC
            LIMIT :limit
            """;
    private static final String SQL_TOP_CATEGORIES = """
            SELECT domain_id, bucket_key, category_name_sample, product_count, brand_count, avg_score, max_score,
                   avg_price_median, price_floor, price_ceiling, avg_offer_count, avg_price_delta_pct,
                   avg_discount_pct, rising_count, falling_count, high_confidence_count
            FROM vw_market_category_summary
            WHERE bucket_key = :bucket
            ORDER BY product_count DESC, avg_score DESC
            LIMIT :limit
            """;
    private static final String SQL_ALL_CURRENT_PRODUCTS = """
            SELECT bucket_key, source, external_type, external_id, title, canonical_title, rank, rank_band,
                   rank_delta, trajectory_label, score, priority_label, confidence, domain_id, category_id,
                   category_name, brand, brand_slug, model, product_key, voltage, power_watts, capacity_value,
                   capacity_unit, energy_efficiency, price, price_min, price_median, price_max, price_delta_pct,
                   discount_pct, offer_count, seller_count, relevance, domain_reason, permalink
            FROM vw_market_product_current
            """;
    private static final String SQL_PRICE_MOVEMENTS = """
            SELECT domain_id, bucket_key, previous_bucket_key, product_count, offer_count, seller_count,
                   avg_price_median, previous_price_median, price_delta_pct, offer_delta_pct
            FROM vw_market_price_movement
            WHERE bucket_key = :bucket
              AND previous_price_median IS NOT NULL
              AND product_count >= 2
            ORDER BY price_delta_pct DESC NULLS LAST
            """;
    private static final String SQL_STOCK_OPPORTUNITIES = """
            SELECT domain_id, bucket_key, previous_bucket_key, product_count, offer_count, seller_count,
                   avg_price_median, previous_price_median, price_delta_pct, offer_delta_pct
            FROM vw_market_price_movement
            WHERE bucket_key = :bucket
              AND price_delta_pct < 0
              AND offer_delta_pct > 0
            ORDER BY price_delta_pct ASC
            """;
    private static final String SQL_RISING_PRODUCTS = """
            SELECT bucket_key, external_type, external_id, title, brand, brand_slug, model, product_key,
                   domain_id, category_id, category_name, rank, rank_band, rank_delta, trajectory_label,
                   score, priority_label, confidence, price_median, price_min, price_max, price_delta_pct,
                   discount_pct, offer_count, seller_count, capacity_value, capacity_unit, power_watts,
                   voltage, permalink
            FROM vw_market_rising_products
            WHERE confidence = 'HIGH'
            ORDER BY score DESC
            """;
    private static final String SQL_BRAND_MOMENTUM = """
            SELECT brand_slug, bucket_key, brand_name, product_count, domain_count, avg_score, best_rank,
                   avg_price_median, price_floor, price_ceiling, avg_price_delta_pct, avg_discount_pct, rising_count
            FROM vw_market_brand_summary
            WHERE bucket_key = :bucket
            ORDER BY rising_count DESC, product_count DESC
            """;
    private static final String SQL_CATEGORY_SUMMARY = """
            SELECT domain_id, bucket_key, category_name_sample, product_count, brand_count, avg_score, max_score,
                   avg_price_median, price_floor, price_ceiling, avg_offer_count, avg_price_delta_pct,
                   avg_discount_pct, rising_count, falling_count, high_confidence_count
            FROM vw_market_category_summary
            WHERE bucket_key = :bucket
            """;
    private static final String SQL_PRICE_DISPERSION = """
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
            """;
    private static final String SQL_UNCOVERED_CATEGORIES = """
            SELECT domain_id, bucket_key, category_name_sample, product_count, brand_count, avg_score, max_score,
                   avg_price_median, price_floor, price_ceiling, avg_offer_count, avg_price_delta_pct,
                   avg_discount_pct, rising_count, falling_count, high_confidence_count
            FROM vw_market_category_summary
            WHERE bucket_key = :bucket
              AND rising_count > 0
              AND domain_id NOT IN (:domains)
            ORDER BY avg_score DESC
            """;

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Optional<String> findCurrentBucketKey() {
        return Optional.ofNullable(safe(
                () -> jdbc.getJdbcTemplate().queryForObject(SQL_CURRENT_BUCKET, String.class),
                null));
    }

    @Override
    public Optional<String> findPreviousBucketKey(String currentBucketKey) {
        if (currentBucketKey == null) {
            return Optional.empty();
        }
        String value = safe(
                () -> jdbc.queryForObject(SQL_PREVIOUS_BUCKET, bucketParams(currentBucketKey), String.class),
                null);
        return Optional.ofNullable(value);
    }

    @Override
    public long countCurrentProducts() {
        Long count = safe(
                () -> jdbc.getJdbcTemplate().queryForObject(SQL_COUNT_PRODUCTS, Long.class),
                0L);
        return count == null ? 0L : count;
    }

    @Override
    public long countDistinctBrands() {
        Long count = safe(
                () -> jdbc.getJdbcTemplate().queryForObject(SQL_COUNT_BRANDS, Long.class),
                0L);
        return count == null ? 0L : count;
    }

    @Override
    public List<MarketBrandSummary> findTopBrands(String bucketKey, int limit) {
        return safeList(() -> jdbc.query(
                SQL_TOP_BRANDS,
                bucketAndLimit(bucketKey, limit),
                (rs, rowNum) -> mapBrand(rs)));
    }

    @Override
    public List<MarketCategorySummary> findTopCategories(String bucketKey, int limit) {
        return safeList(() -> jdbc.query(
                SQL_TOP_CATEGORIES,
                bucketAndLimit(bucketKey, limit),
                (rs, rowNum) -> mapCategory(rs)));
    }

    @Override
    public List<MarketProductCurrent> findAllCurrentProducts() {
        return safeList(() -> jdbc.getJdbcTemplate().query(SQL_ALL_CURRENT_PRODUCTS, (rs, rowNum) -> mapProduct(rs)));
    }

    @Override
    public List<MarketPriceMovement> findPriceMovements(String bucketKey) {
        return safeList(() -> jdbc.query(
                SQL_PRICE_MOVEMENTS,
                bucketParams(bucketKey),
                (rs, rowNum) -> mapPriceMovement(rs)));
    }

    @Override
    public List<MarketPriceMovement> findStockOpportunities(String bucketKey) {
        return safeList(() -> jdbc.query(
                SQL_STOCK_OPPORTUNITIES,
                bucketParams(bucketKey),
                (rs, rowNum) -> mapPriceMovement(rs)));
    }

    @Override
    public List<MarketRisingProduct> findRisingProductsHighConfidence() {
        return safeList(() -> jdbc.getJdbcTemplate().query(SQL_RISING_PRODUCTS, (rs, rowNum) -> mapRising(rs)));
    }

    @Override
    public List<MarketBrandSummary> findBrandMomentum(String bucketKey) {
        return safeList(() -> jdbc.query(
                SQL_BRAND_MOMENTUM,
                bucketParams(bucketKey),
                (rs, rowNum) -> mapBrand(rs)));
    }

    @Override
    public List<MarketCategorySummary> findCategorySummary(String bucketKey) {
        return safeList(() -> jdbc.query(
                SQL_CATEGORY_SUMMARY,
                bucketParams(bucketKey),
                (rs, rowNum) -> mapCategory(rs)));
    }

    @Override
    public List<MarketProductCurrent> findPriceDispersion() {
        return safeList(() -> jdbc.getJdbcTemplate().query(SQL_PRICE_DISPERSION, (rs, rowNum) -> mapProduct(rs)));
    }

    @Override
    public List<MarketCategorySummary> findUncoveredCategories(String bucketKey, Collection<String> clientDomainIds) {
        Collection<String> domains = (clientDomainIds == null || clientDomainIds.isEmpty())
                ? List.of("__none__")
                : clientDomainIds;
        MapSqlParameterSource params = bucketParams(bucketKey).addValue("domains", domains);
        return safeList(() -> jdbc.query(SQL_UNCOVERED_CATEGORIES, params, (rs, rowNum) -> mapCategory(rs)));
    }

    private static MapSqlParameterSource bucketParams(String bucketKey) {
        return new MapSqlParameterSource(PARAM_BUCKET, bucketKey);
    }

    private static MapSqlParameterSource bucketAndLimit(String bucketKey, int limit) {
        return bucketParams(bucketKey).addValue("limit", limit);
    }

    private MarketBrandSummary mapBrand(ResultSet rs) throws SQLException {
        return new MarketBrandSummary(
                rs.getString(COL_BRAND_SLUG),
                rs.getString(COL_BUCKET_KEY),
                rs.getString("brand_name"),
                toLong(rs.getObject(COL_PRODUCT_COUNT)),
                toLong(rs.getObject("domain_count")),
                toDouble(rs.getObject("avg_score")),
                toInteger(rs.getObject("best_rank")),
                toDouble(rs.getObject(COL_AVG_PRICE_MEDIAN)),
                toDouble(rs.getObject("price_floor")),
                toDouble(rs.getObject("price_ceiling")),
                toDouble(rs.getObject("avg_price_delta_pct")),
                toDouble(rs.getObject("avg_discount_pct")),
                toLong(rs.getObject("rising_count"))
        );
    }

    private MarketCategorySummary mapCategory(ResultSet rs) throws SQLException {
        return new MarketCategorySummary(
                rs.getString(COL_DOMAIN_ID),
                rs.getString(COL_BUCKET_KEY),
                rs.getString("category_name_sample"),
                toLong(rs.getObject(COL_PRODUCT_COUNT)),
                toLong(rs.getObject("brand_count")),
                toDouble(rs.getObject("avg_score")),
                toDouble(rs.getObject("max_score")),
                toDouble(rs.getObject(COL_AVG_PRICE_MEDIAN)),
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
                rs.getString(COL_DOMAIN_ID),
                rs.getString(COL_BUCKET_KEY),
                rs.getString("previous_bucket_key"),
                toLong(rs.getObject(COL_PRODUCT_COUNT)),
                toLong(rs.getObject(COL_OFFER_COUNT)),
                toLong(rs.getObject(COL_SELLER_COUNT)),
                toDouble(rs.getObject(COL_AVG_PRICE_MEDIAN)),
                toDouble(rs.getObject("previous_price_median")),
                toDouble(rs.getObject(COL_PRICE_DELTA_PCT)),
                toDouble(rs.getObject("offer_delta_pct"))
        );
    }

    private MarketProductCurrent mapProduct(ResultSet rs) throws SQLException {
        return new MarketProductCurrent(
                rs.getString(COL_BUCKET_KEY),
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
                rs.getString(COL_DOMAIN_ID),
                rs.getString("category_id"),
                rs.getString("category_name"),
                rs.getString("brand"),
                rs.getString(COL_BRAND_SLUG),
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
                toDouble(rs.getObject(COL_PRICE_DELTA_PCT)),
                toDouble(rs.getObject("discount_pct")),
                toInteger(rs.getObject(COL_OFFER_COUNT)),
                toInteger(rs.getObject(COL_SELLER_COUNT)),
                toDouble(rs.getObject("relevance")),
                rs.getString("domain_reason"),
                rs.getString("permalink")
        );
    }

    private MarketRisingProduct mapRising(ResultSet rs) throws SQLException {
        return new MarketRisingProduct(
                rs.getString(COL_BUCKET_KEY),
                rs.getString("external_type"),
                rs.getString("external_id"),
                rs.getString("title"),
                rs.getString("brand"),
                rs.getString(COL_BRAND_SLUG),
                rs.getString("model"),
                rs.getString("product_key"),
                rs.getString(COL_DOMAIN_ID),
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
                toDouble(rs.getObject(COL_PRICE_DELTA_PCT)),
                toDouble(rs.getObject("discount_pct")),
                toInteger(rs.getObject(COL_OFFER_COUNT)),
                toInteger(rs.getObject(COL_SELLER_COUNT)),
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
            log.warn("Market view unavailable: {}", ex.getMessage());
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
