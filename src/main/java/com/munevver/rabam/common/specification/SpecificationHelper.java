package com.munevver.rabam.common.specification;

import org.springframework.data.jpa.domain.Specification;

public final class SpecificationHelper {

    private SpecificationHelper() {
    }

    public static <T> Specification<T> alwaysTrue() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }
}
