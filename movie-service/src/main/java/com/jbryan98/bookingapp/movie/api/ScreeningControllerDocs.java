package com.jbryan98.bookingapp.movie.api;

import com.jbryan98.bookingapp.movie.api.dto.MovieRequest;
import com.jbryan98.bookingapp.movie.api.dto.ScreeningRequest;
import com.jbryan98.bookingapp.movie.api.dto.ScreeningResponse;
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
        name = "Screenings",
        description = "Gestión de funciones"
)
public interface ScreeningControllerDocs {
    @Operation(
            summary = "Listar funciones disponibles",
            description = "Retorna una lista paginadas de funciones.",
            security = {}
    )
    @ApiResponse(
            responseCode = "200",
            description = "Funciones listadas exitosamente"
    )
    ResponseEntity<Page<ScreeningResponse>> getScreenings(@ParameterObject Pageable pageable);

    @Operation(
            summary = "Obtener función por ID",
            description = "Retorna los detalles de una función específica por su ID.",
            security = {}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Función encontrada",
                    content = @Content(schema = @Schema(implementation = ScreeningResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Función no encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<ScreeningResponse> getScreeningById(
            @Parameter(description = "UUID de la función", example = "e224cb53-f300-4d74-bd56-512e72ff53e1")
            @PathVariable UUID id);

    @Operation(
            summary = "Crear función",
            description = "Crea una función con estado activo. Se requieren permisos de ADMIN para realizar esta acción.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Función creada exitosamente",
                    headers = @Header(
                            name = "Location",
                            description = "URL del recurso creado (ej: /api/v1/screenings/123e4567-e89b-12d3-a456-426614174000)",
                            schema = @Schema(type = "string", format = "uri")
                    ),
                    content = @Content(schema = @Schema(implementation = ScreeningResponse.class))
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
    ResponseEntity<ScreeningResponse> createScreening(
            @RequestBody(
                    description = "Función de prueba",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = MovieRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Crear una función",
                                            summary = "Ejemplo de función para crear.",
                                            value = """
                                                    {
                                                        "movieId": "549efc4e-be2a-4126-9025-db222f7734e1",
                                                        "room": "Sala 1",
                                                        "showAt": "2026-07-18T16:30:00",
                                                        "totalSeats": 100,
                                                        "availableSeats": 100
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            ScreeningRequest request
    );

    @Operation(
            summary = "Reservar asiento por ID de la función",
            description = "Reserva un asiento en una función específica por su ID. Se requieren permisos de ADMIN para realizar esta acción.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Asiento reservado exitosamente",
                    content = @Content(schema = @Schema(implementation = ScreeningResponse.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "La función no cuenta con asientos disponibles.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Función no encontrada.",
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
    ResponseEntity<ScreeningResponse> reserveScreening(
            @Parameter(description = "UUID de la función", example = "e224cb53-f300-4d74-bd56-512e72ff53e1")
            @PathVariable UUID id) throws InterruptedException;

    @Operation(
            summary = "Liberar asiento por ID de la función",
            description = "Libera un asiento en una función específica por su ID. Se requieren permisos de ADMIN para realizar esta acción.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Asiento liberado exitosamente",
                    content = @Content(schema = @Schema(implementation = ScreeningResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Función no encontrada.",
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
    ResponseEntity<ScreeningResponse> releaseScreening(@PathVariable UUID id);
}
