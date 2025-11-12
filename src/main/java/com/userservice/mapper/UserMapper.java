package com.userservice.mapper;


import com.userservice.entity.User;
import com.userservice.entity.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDTO convertToDTO(User user);

    User convertToEntity(UserDTO userResponseDTO);


}
