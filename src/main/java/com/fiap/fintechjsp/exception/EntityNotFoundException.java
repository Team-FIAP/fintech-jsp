package com.fiap.fintechjsp.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Long id) {
        super("Não foi possível encontrar o recurso com o ID: " + id);
    }
}
