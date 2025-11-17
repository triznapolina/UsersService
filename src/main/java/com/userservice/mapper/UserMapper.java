package com.userservice.mapper;


import com.userservice.entity.User;
import com.userservice.entity.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDto convertToDTO(User user);

    User convertToEntity(UserDto userResponseDTO);


}
