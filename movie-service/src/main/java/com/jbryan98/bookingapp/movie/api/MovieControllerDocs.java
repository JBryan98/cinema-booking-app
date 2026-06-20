package com.jbryan98.bookingapp.movie.api;

import com.jbryan98.bookingapp.movie.api.dto.MovieRequest;
import com.jbryan98.bookingapp.movie.api.dto.MovieResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Tag(
        name = "Movies",
        description = "Gestión de películas"
)
public interface MovieControllerDocs {
    @Operation(
            summary = "Listar películas disponibles",
            description = "Retorna una lista paginadas de películas.",
            security = {}
    )
    @ApiResponse(
            responseCode = "200",
            description = "Películas listadas exitosamente"
    )
    ResponseEntity<Page<MovieResponse>> getAllMovies(@ParameterObject Pageable pageable);


    @Operation(
            summary = "Obtener película por ID",
            description = "Retorna los detalles de una película específica por su ID.",
            security = {}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Película encontrada",
                    content = @Content(schema = @Schema(implementation = MovieResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Película no encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<MovieResponse> getMovieById(
            @Parameter(description = "UUID de la película", example = "e224cb53-f300-4d74-bd56-512e72ff53e1")
            @PathVariable UUID id);

    @Operation(
            summary = "Crear película",
            description = "Crea una película con estado activo. Se requieren permisos de ADMIN para realizar esta acción.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Película creada exitosamente",
                    headers = @Header(
                            name = "Location",
                            description = "URL del recurso creado (ej: /api/v1/movies/123e4567-e89b-12d3-a456-426614174000)",
                            schema = @Schema(type = "string", format = "uri")
                    ),
                    content = @Content(schema = @Schema(implementation = MovieResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos incorrectos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permisos insuficientes para realizar esta acción",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<MovieResponse> createMovie(
            @RequestBody(
                    description = "Película de prueba",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = MovieRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Crear una película",
                                            summary = "Ejemplo de película para crear.",
                                            value = """
                                                    {
                                                      "title": "Gladiator",
                                                      "director": "Ridley Scott",
                                                      "genre": "ACTION",
                                                      "durationMinutes": 155,
                                                      "rating": "R"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            MovieRequest request
    );

    @Operation(
            summary = "Actualizar película por ID",
            description = "Actualiza los detalles de una película específica por su ID. Se requieren permisos de ADMIN para realizar esta acción.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Película actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = MovieResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Película no encontrada.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos incorrectos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permisos insuficientes para realizar esta acción",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<MovieResponse> updateMovie(
            @Parameter(description = "UUID de la película", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable UUID id,
            @RequestBody(
                    description = "Actualización de prueba",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = MovieRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Actualizar una película",
                                            summary = "Ejemplo de película para actualizar.",
                                            value = """
                                                       {
                                                         "title": "Gladiator Updt",
                                                         "director": "Ridley Scott Updt",
                                                         "genre": "ACTION",
                                                         "durationMinutes": 155,
                                                         "rating": "R"
                                                       }
                                                    """
                                    )
                            }
                    )
            )
            MovieRequest request);

    @Operation(
            summary = "Activar película por ID",
            description = "Activa una película específica por su ID. Se requieren permisos de ADMIN para realizar esta acción.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Película activada exitosamente",
                    content = @Content(schema = @Schema(implementation = MovieResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Película no encontrada.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permisos insuficientes para realizar esta acción",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<MovieResponse> activateMovie(
            @Parameter(description = "UUID de la película", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable UUID id);

    @Operation(
            summary = "Desactivar película por ID",
            description = "Desactiva una película específica por su ID. Se requieren permisos de ADMIN para realizar esta acción.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Película desactivada exitosamente",
                    content = @Content(schema = @Schema(implementation = MovieResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Película no encontrada.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permisos insuficientes para realizar esta acción",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<MovieResponse> deactivateMovie(
            @Parameter(description = "UUID de la película", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable UUID id);

    @Operation(
            summary = "Eliminar película por ID",
            description = "Elimina una película específica por su ID. Se requieren permisos de ADMIN para realizar esta acción.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Película eliminada exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Película no encontrada.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente o inválido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permisos insuficientes para realizar esta acción",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<Void> deleteMovie(
            @Parameter(description = "UUID de la película", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable UUID id);
}
