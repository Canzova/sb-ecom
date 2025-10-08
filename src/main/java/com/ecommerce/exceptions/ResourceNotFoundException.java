package com.ecommerce.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResourceNotFoundException extends RuntimeException {
    String resourceName;
    String field;
    String fieldName;
    Long filedId;

    public ResourceNotFoundException(String resourceName, String field, String fieldName, Long filedId) {
        super(String.format("%s not found with %s : %s", resourceName, field, fieldName));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
        this.filedId = filedId;
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Long filedId) {
        super(String.format("%s not found with %s : %d", resourceName, fieldName, filedId));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.filedId = filedId;
    }
}
