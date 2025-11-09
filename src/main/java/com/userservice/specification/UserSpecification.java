package com.userservice.specification;

import com.userservice.models.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecification {

    public static Specification<User> hasFirstName(String firstName) {
        return (root, query, cb) -> cb.equal(root.get("firstName"), firstName);

    }

    private static Specification<User> hasSurname(String surname) {
        return (root, query, cb) -> cb.equal(root.get("surname"), surname);
    }


}
