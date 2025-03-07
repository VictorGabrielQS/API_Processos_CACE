package tech.buildrun.api.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@RestController
public class ApiController {

    //Lista de processos
    private List<String> processos = new ArrayList<>();


    private ObjectMapper objectMapper;

    public ApiController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    //Metodo Get
    @GetMapping(path = "/Processos")
    public ResponseEntity<String> helloWorld() throws JsonProcessingException {
        return ResponseEntity.ok(objectMapper.writeValueAsString(processos));

    }


    //Metodo Post
    @PostMapping(path = "/Processos")
    public ResponseEntity<Void> createProcesso(@RequestBody String processo) {
        processos.add(processo);
        return ResponseEntity.ok().build();

  }


  //Metodo Delete
    @DeleteMapping(path = "/Processos")
    public  ResponseEntity<Void> deleteProcesso(@RequestBody String processoDelete){
        processos.remove(processoDelete);
        return ResponseEntity.ok().build();
    }




}
