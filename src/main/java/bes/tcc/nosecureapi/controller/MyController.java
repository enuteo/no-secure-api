package bes.tcc.nosecureapi.controller;

import bes.tcc.nosecureapi.dto.MyObject;
import bes.tcc.nosecureapi.repository.BaseObjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Objects", description = "Unauthenticated object operations")
public class MyController {
	private final BaseObjectRepository baseObjectRepository;

	public MyController(BaseObjectRepository baseObjectRepository) {
		this.baseObjectRepository = baseObjectRepository;
	}

	@GetMapping("/object/{id}")
	@Operation(
			summary = "Get an object by ID",
			description = "Retrieves an object by its numeric ID.",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Object returned successfully.",
							content = @Content(schema = @Schema(implementation = MyObject.class))),
					@ApiResponse(responseCode = "404", description = "No object exists with the requested ID.")
			})
	public ResponseEntity<MyObject> getObjectById(@PathVariable Long id) {
		Optional<MyObject> object = baseObjectRepository.findById(id);
		return ResponseEntity.of(object);
	}
}
