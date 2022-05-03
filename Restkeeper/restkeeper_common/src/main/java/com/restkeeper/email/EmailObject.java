package com.restkeeper.email;

import lombok.Data;

import java.io.Serializable;

/**
 * @author dinglei
 * @date 2022/5/3 10:07
 */
@Data
public class EmailObject implements Serializable {

    private static final long serialVersionUID = 4444223830782534704L;

    private String email;

    private String subject ;

    private String content;

}
