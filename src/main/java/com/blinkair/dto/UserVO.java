package com.blinkair.dto;

import com.blinkair.entity.User;
import com.blinkair.entity.UserProfile;
import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private Long tgId;
    private String username;
    private String name;
    private Integer age;
    private String gender;
    private String city;
    private String countryCode;
    private String bio;
    private Boolean isVip;

    public static UserVO from(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setTgId(user.getTgId());
        vo.setUsername(user.getUsername());
        vo.setIsVip(user.getIsVip());
        return vo;
    }

    public static UserVO from(User user, UserProfile profile) {
        UserVO vo = from(user);
        if (profile != null) {
            vo.setName(profile.getName());
            vo.setAge(profile.getAge());
            vo.setGender(profile.getGenderStr());
            vo.setCity(profile.getCity());
            vo.setCountryCode(profile.getCountryCode());
            vo.setBio(profile.getBio());
        }
        return vo;
    }
}
