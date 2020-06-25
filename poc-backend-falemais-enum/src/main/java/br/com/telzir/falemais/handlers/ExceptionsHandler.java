package br.com.telzir.falemais.handlers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionsHandler {

    private final static String ATTR_ERROS = "erros";
    private final static String ATTR_CAMPO = "campo";
    private final static String ATTR_MENSAGEM_ERRO = "erro";

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleConstraintViolationException(ConstraintViolationException exception) {

        Map<String, Object> corpoGeralErro = new HashMap<>();
        ArrayList<Map<String, String>> erros = new ArrayList<>();

        exception.getConstraintViolations().forEach(violation -> {
            String path = violation.getPropertyPath().toString();
            String campo = path.substring(path.lastIndexOf(".")+1);

            Map<String, String> itemErro = new HashMap<>();
            itemErro.put(ATTR_CAMPO, campo);
            itemErro.put(ATTR_MENSAGEM_ERRO, violation.getMessage());

            erros.add(itemErro);
        });
        corpoGeralErro.put(ATTR_ERROS, erros);

        return ResponseEntity.badRequest().body(corpoGeralErro);
    }
}
