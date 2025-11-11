package com.userservice.mappers;

import com.userservice.models.User;
import com.userservice.models.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDTO convertToDTO(User user);

    User convertToEntity(UserDTO userResponseDTO);


}
