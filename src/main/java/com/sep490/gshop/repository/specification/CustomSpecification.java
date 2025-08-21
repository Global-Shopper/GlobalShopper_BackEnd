package com.sep490.gshop.repository.specification;

import com.sep490.gshop.common.enums.PurchaseRequestStatus;
import com.sep490.gshop.common.enums.RequestType;
import com.sep490.gshop.entity.Customer;
import com.sep490.gshop.entity.PurchaseRequest;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class CustomSpecification {
    public Specification<Customer> filterCustomer(String search,
                                                 Boolean status,
                                                 Long start,
                                                 Long end) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isEmpty()) {
                String keyword = removeAccents(search).toLowerCase();
                Predicate namePredicate = cb.like(
                        cb.function("unaccent", String.class, cb.lower(root.get("name"))),
                        "%" + keyword + "%"
                );
                Predicate emailPredicate = cb.like(
                        cb.function("unaccent", String.class, cb.lower(root.get("email"))),
                        "%" + keyword + "%"
                );

                predicates.add(cb.or(namePredicate, emailPredicate));
            }

            // filter theo status
            if (status != null) {
                predicates.add(cb.equal(root.get("isActive"), status));
            }

            // filter theo khoảng thời gian
            if (start != null && end != null) {
                predicates.add(cb.between(root.get("createdAt"), start, end));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    public static String removeAccents(String input) {
        if (input == null) return null;
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", ""); // bỏ toàn bộ dấu
    }

    public Specification<PurchaseRequest> filterPurchaseRequest(PurchaseRequestStatus status, RequestType requestType, UUID customerId, UUID adminId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // filter theo status
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            // filter theo requestType
            if (requestType != null) {
                predicates.add(cb.equal(root.get("requestType"), requestType));
            }

            if (customerId != null) {
                predicates.add(cb.equal(root.get("customer").get("id"), customerId));
            } else if (adminId != null) {
                predicates.add(cb.equal(root.get("admin").get("id"), adminId));
            } else {
                predicates.add(cb.isNull(root.get("admin")));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


}
