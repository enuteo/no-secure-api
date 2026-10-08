package bes.tcc.nosecureapi.service;

import org.springframework.stereotype.Service;

import bes.tcc.nosecureapi.dto.MyObject;

@Service
public class MyService {
    
    public MyObject findOBjectById(Long id) {
        return MyObject.builder().id(1L).build();
    }
}
