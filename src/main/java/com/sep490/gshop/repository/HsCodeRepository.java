package com.sep490.gshop.repository;

import com.sep490.gshop.entity.HsCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HsCodeRepository extends JpaRepository<HsCode, String> {


    @Query(value = """
SELECT * FROM hs_code
WHERE LENGTH(hs_code) = 2 AND (parent_code IS NULL OR parent_code = '')
""",
            countQuery = """
SELECT COUNT(*) FROM hs_code
WHERE LENGTH(hs_code) = 2 AND (parent_code IS NULL OR parent_code = '')
""",
            nativeQuery = true)
    Page<HsCode> getAll(Pageable pageable);




    @Query(value = """
SELECT * FROM hs_code
WHERE LENGTH(hs_code) IN (2, 4, 6, 8)
AND (:hsCode IS NULL OR hs_code = :hsCode)
AND (COALESCE(:desc, '') = '' OR to_tsvector('simple', unaccent(description)) @@ to_tsquery('simple', unaccent(regexp_replace(:desc, '\\s+', ' | ', 'g'))))
""",
            countQuery = """
SELECT COUNT(*) FROM hs_code
WHERE LENGTH(hs_code) IN (2, 4, 6, 8)
AND (:hsCode IS NULL OR hs_code = :hsCode)
AND (COALESCE(:desc, '') = '' OR to_tsvector('simple', unaccent(description)) @@ to_tsquery('simple', unaccent(regexp_replace(:desc, '\\s+', ' | ', 'g'))))
""",
            nativeQuery = true)
    Page<HsCode> searchByHsCodeAndDescriptionForRoots(
            @Param("hsCode") String hsCode,
            @Param("desc") String desc,
            Pageable pageable
    );

    boolean existsByHsCode(String hsCode);



}
