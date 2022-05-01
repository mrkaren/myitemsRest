package com.example.myitemsrest.dto;

import com.example.myitemsrest.entity.Role;
import lombok.Data;

@Data
public class UserFilterDto {

    private String name;
    private String surname;
    private String email;
    private Role role;


}
