package com.yupi.project.common;

import lombok.Data;

import java.io.Serializable;

/**
 * ID请求
 *
 * @author yupi
 */
@Data
public class DeleteRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}