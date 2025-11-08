package com.userservice.specifications;

import com.userservice.models.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecification {


    private Specification<User> withFirstName(String firstName) {
        return (root, query, cb) ->
                cb.equal(root.get("firstName"), firstName);

    }


    private Specification<User> withSurname(String surname) {
        return (root, query, cb) ->
                cb.equal(root.get("surname"), surname);
    }


}
