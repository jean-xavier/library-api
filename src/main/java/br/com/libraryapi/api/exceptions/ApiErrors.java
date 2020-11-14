package br.com.libraryapi.api.exceptions;

import br.com.libraryapi.exceptions.BusinessException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ApiErrors {
    private final List<String> errors;

    public ApiErrors(BindingResult bindingResult) {
        errors = bindingResult.getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
    }

    public ApiErrors(BusinessException e) {
        errors = Arrays.asList(e.getMessage());
    }

    public List<String> getErrors() {
        return errors;
    }
}
