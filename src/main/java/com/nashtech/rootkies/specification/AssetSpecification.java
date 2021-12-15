package com.nashtech.rootkies.specification;

import com.nashtech.rootkies.model.*;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

@AllArgsConstructor
public class AssetSpecification implements Specification<Asset> {

    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(Root<Asset> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        if(criteria.getKey().equals("category")) {
            Join<Asset, Category> join = root.join("category");
            return builder.equal(join.get("categoryCode"), criteria.getValue());
        }

        if (criteria.getKey().equals("role")) {
            Join<User, Role> join = root.join("role");

            return builder.equal(join.<Long>get("id"), criteria.getValue());
        }

        if(criteria.getKey().equals("location")) {
            Join<Asset, Role> join = root.join("location");
            return builder.equal(join.get("locationId"), criteria.getValue());
        }
        if (criteria.getValue().equals("true")) {
            return builder.isTrue(root.<Boolean>get(criteria.getKey()));
        }
        if (criteria.getValue().equals("false")) {
            return builder.isFalse(root.<Boolean>get(criteria.getKey()));
        }
        if (criteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(
                    root.<String>get(criteria.getKey()), criteria.getValue().toString());
        } else if (criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(
                    root.<String>get(criteria.getKey()), criteria.getValue().toString());
        } else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                        root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }
}
