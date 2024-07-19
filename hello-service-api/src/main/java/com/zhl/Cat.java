package com.zhl;

import lombok.*;

import java.io.Serializable;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-19 21:15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cat implements Serializable {
    private Integer age;
    private String name;
}
