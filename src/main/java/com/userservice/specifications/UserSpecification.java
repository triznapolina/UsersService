package com.userservice.specifications;


import com.userservice.models.User;
import com.userservice.models.dto.UserParamDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecification {

    public Specification<User> build(UserParamDTO params) {
        return withFirstName(params.getFirstName())
                .and(withSurname(params.getSurname()));
    }

    private Specification<User> withFirstName(String firstName) {
        return (root, query, cb) ->
                firstName == null || firstName.isEmpty() ? cb.conjunction() : cb.equal(root.get("firstName"),
                firstName);
    }

    private Specification<User> withSurname(String surname) {
        return (root, query, cb) ->
                surname == null || surname.isEmpty() ? cb.conjunction() : cb.equal(root.get("surname"),
                surname);
    }


}
