package com.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author chris
 * @create 2022/11/20
 */
@Data
@AllArgsConstructor
public class Folder {
    private String name;
    private Long size;
}
