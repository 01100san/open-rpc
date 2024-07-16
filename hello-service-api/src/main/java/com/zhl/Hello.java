package com.zhl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 *
 * @author zhl
 * @since 2024-07-16 9:21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Hello implements Serializable {
    private String message;
    private String description;
}
