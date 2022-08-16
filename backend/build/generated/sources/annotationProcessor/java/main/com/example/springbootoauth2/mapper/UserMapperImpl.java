package com.example.springbootoauth2.mapper;

import com.example.springbootoauth2.model.dto.UserDto;
import com.example.springbootoauth2.model.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-08-16T09:42:17+0900",
    comments = "version: 1.5.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.5.jar, environment: Java 17.0.3 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.idx( user.getIdx() );
        userDto.userId( user.getUserId() );
        userDto.username( user.getUsername() );
        userDto.password( user.getPassword() );
        userDto.email( user.getEmail() );
        userDto.isVerifiedEmail( user.getIsVerifiedEmail() );
        userDto.profileImageUrl( user.getProfileImageUrl() );

        return userDto.build();
    }

    @Override
    public User toEntity(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.idx( userDto.getIdx() );
        user.userId( userDto.getUserId() );
        user.username( userDto.getUsername() );
        user.password( userDto.getPassword() );
        user.email( userDto.getEmail() );
        user.isVerifiedEmail( userDto.getIsVerifiedEmail() );
        user.profileImageUrl( userDto.getProfileImageUrl() );

        return user.build();
    }
}
