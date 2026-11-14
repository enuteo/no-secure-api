package bes.tcc.nosecureapi.service;

import org.springframework.stereotype.Service;

import bes.tcc.nosecureapi.dto.MyObject;
import bes.tcc.nosecureapi.repository.BaseObjectRepository;
import java.util.Optional;

@Service
public class MyService {
	private final BaseObjectRepository baseObjectRepository;

	public MyService(BaseObjectRepository baseObjectRepository) {
		this.baseObjectRepository = baseObjectRepository;
	}

	public Optional<MyObject> findObjectById(Long id) {
		return baseObjectRepository.findById(id);
	}
}
