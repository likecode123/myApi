package com.example.project.common;

import lombok.Data;

import java.io.Serializable;

/**
 * ID请求
 *
 * @author example
 */
@Data
public class DeleteRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}