package com.sep490.gshop.repository;

import com.sep490.gshop.entity.HsCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HsCodeRepository extends JpaRepository<HsCode, String> {
    @Query(value = """
SELECT * FROM hs_code
WHERE
    (
        COALESCE(:hsCode, '') = ''
        OR hs_code ILIKE CONCAT('%', :hsCode, '%')
    )
    AND
    (
        COALESCE(:desc, '') = ''
        OR to_tsvector('simple', unaccent(description::text)) @@ to_tsquery('simple', unaccent(regexp_replace(:desc, '\\s+', ' | ', 'g')))
    )
""",
            countQuery = """
SELECT COUNT(*) FROM hs_code
WHERE
    (
        COALESCE(:hsCode, '') = ''
        OR hs_code ILIKE CONCAT('%', :hsCode, '%')
    )
    AND
    (
        COALESCE(:desc, '') = ''
        OR to_tsvector('simple', unaccent(description::text)) @@ to_tsquery('simple', unaccent(regexp_replace(:desc, '\\s+', ' | ', 'g')))
    )
""",
            nativeQuery = true)
    Page<HsCode> searchByHsCodeAndDescription(
            @Param("hsCode") String hsCode,
            @Param("desc") String desc,
            Pageable pageable
    );

    @Query("SELECT h FROM HsCode h WHERE LENGTH(h.hsCode) = 2 AND (h.parentCode IS NULL OR h.parentCode = '') ORDER BY h.hsCode")
    Page<HsCode> getAll(Pageable pageable);


}
