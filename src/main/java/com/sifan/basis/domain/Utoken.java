package com.sifan.basis.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName utoken
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Utoken implements Serializable {
    private String username;

    private String token;

    private static final long serialVersionUID = 1L;
}