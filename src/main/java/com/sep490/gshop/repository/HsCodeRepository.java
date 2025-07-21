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
        COALESCE(:desc, '') = '' 
        OR to_tsvector(unaccent(description::text)) @@ to_tsquery('simple', unaccent(regexp_replace(:desc, '\\s+', ' | ', 'g')))
    """,
            countQuery = """
    SELECT COUNT(*) FROM hs_code
    WHERE 
        COALESCE(:desc, '') = '' 
        OR to_tsvector(unaccent(description::text)) @@ to_tsquery('simple', unaccent(regexp_replace(:desc, '\\s+', ' | ', 'g')))
    """,
            nativeQuery = true)
    Page<HsCode> searchByDescription(
            @Param("desc") String desc,
            Pageable pageable
    );


}
