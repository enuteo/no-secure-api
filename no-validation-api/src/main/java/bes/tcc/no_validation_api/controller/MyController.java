package bes.tcc.no_validation_api.controller;

import bes.tcc.no_validation_api.dto.MyObject;
import bes.tcc.no_validation_api.repository.BaseObjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Objects", description = "Bearer-authenticated object operations")
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat = "JWT")
public class MyController {
	private final BaseObjectRepository baseObjectRepository;

	public MyController(BaseObjectRepository baseObjectRepository) {
		this.baseObjectRepository = baseObjectRepository;
	}

	@GetMapping("/object/{id}")
	@Operation(
			summary = "Get an object by ID",
			description = "Requires a valid bearer token with the objectrole role. Object access is not checked.",
			security = @SecurityRequirement(name = "bearerAuth"),
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Object returned successfully.",
							content = @Content(schema = @Schema(implementation = MyObject.class))),
					@ApiResponse(responseCode = "401", description = "The bearer token is invalid or does not have objectrole."),
					@ApiResponse(responseCode = "407", description = "No bearer token was provided."),
					@ApiResponse(responseCode = "404", description = "No object exists with the requested ID.")
			})
	public ResponseEntity<MyObject> getObjectById(@PathVariable Long id) {
		Optional<MyObject> object = baseObjectRepository.findById(id);
		return ResponseEntity.of(object);
	}
}
