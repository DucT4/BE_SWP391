package com.SWP391_02.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor   // <-- cái này sẽ tự tạo constructor có đủ field
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
}