package bes.tcc.nosecureapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder 
@Getter 
@Setter 
public class MyObject {
    private String name;
    private Long id;
}
