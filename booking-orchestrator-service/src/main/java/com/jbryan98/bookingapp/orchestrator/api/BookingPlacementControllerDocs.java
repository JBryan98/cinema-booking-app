package com.jbryan98.bookingapp.orchestrator.api;

import com.jbryan98.bookingapp.orchestrator.api.dto.BookingPlacementResponse;
import com.jbryan98.bookingapp.orchestrator.api.dto.PlaceBookingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Tag(name = "Booking Orchestrator")
public interface BookingPlacementControllerDocs {
    @Operation(
            summary = "Crear reserva",
            description = """
                    Crea una reserva para una función en específico. Para realizar esta acción se requieren permisos de ADMIN.
                    Este endpoint realiza el proceso de reserva, que incluye:
                    ```
                    1. Verificar si la función esta disponible
                    2. Crear la reserva con estado PENDING.
                    3. Reservar el asiento en movie-service.
                    4. Confirmar la reserva
                    ```
                    Si el flujo SAGA es exitoso entonces se publica BookingConfirmedEvent al topic `cinema.bookings`
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Reserva creada con exito",
                    headers = @Header(
                            name = "Location",
                            schema = @Schema(type = "string", format = "uri")
                    ),
                    content = @Content(schema = @Schema(implementation = BookingPlacementResponse.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Si hubo error durante la ejecución del SAGA, se devuelve un error 422 con el detalle del error en el body",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos incorrectos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT asuente o inválido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permisos insuficientes para realizar esta acción",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<BookingPlacementResponse> placeBooking(
            @RequestBody(
                    description = "Crear reserva",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = PlaceBookingRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Crear reserva de prueba",
                                            summary = "Reserva de prueba con customerId ficticio",
                                            value = """
                                                    {
                                                      "screeningId": "bf22f406-ff9f-4e66-9075-6ba45ace17a8",
                                                      "customerId": "14f97cf4-1204-47f0-ac9b-cb25fe0ab04c",
                                                      "totalAmount": 12.00
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            PlaceBookingRequest request);

    @Operation(
            summary = "Cancelar reserva",
            description = """
                    Cancela la reserva con el id especificado. Para realizar esta acción se requieren permisos de ADMIN.
                    Este endpoint realiza el proceso de cancelación, que incluye:
                    ```
                    1. Verificar que la reserva existe y puede cancelarse.
                    2. Actualizar el booking a CANCELLED
                    3. Liberar el asiento en movie-service.,
                    ```
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reserva cancelada con exito",
                    content = @Content(schema = @Schema(implementation = BookingPlacementResponse.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Si hubo error durante la ejecución del SAGA, se devuelve un error 422 con el detalle del error en el body",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT asuente o inválido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permisos insuficientes para realizar esta acción",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<BookingPlacementResponse> cancelBooking(
            @Parameter(description = "UUID de la reserva", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable(name = "id") UUID bookingId);
}
