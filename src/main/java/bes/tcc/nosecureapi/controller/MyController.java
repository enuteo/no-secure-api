package bes.tcc.nosecureapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bes.tcc.nosecureapi.dto.MyObject;
import bes.tcc.nosecureapi.service.MyService;
import lombok.AllArgsConstructor;

@RestController 
@RequestMapping ("/api")
@AllArgsConstructor 
public class MyController {
    
    private final MyService myService;
    
    @GetMapping ("/object/{id}")
    public ResponseEntity<MyObject> getObjectById(@PathVariable Long id) {
        MyObject myObject = myService.findOBjectById(id);
        return ResponseEntity.ok(myObject);
    }
}
