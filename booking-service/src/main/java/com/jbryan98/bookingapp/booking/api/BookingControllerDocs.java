package com.jbryan98.bookingapp.booking.api;

import com.jbryan98.bookingapp.booking.api.dto.BookingRequest;
import com.jbryan98.bookingapp.booking.api.dto.BookingResponse;
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

@Tag(
        name = "Bookings"
)
public interface BookingControllerDocs {
    @Operation(
            summary = "Consultar reserva por ID",
            description = "Retorna los detalles de la reserva"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reserva encontrada",
                    content = @Content(schema = @Schema(implementation = BookingResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reserva no encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<BookingResponse> getBookingById(
            @Parameter(description = "UUID de la reserva", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable UUID id);

    @Operation(
            summary = "Crear reserva",
            description = """
                    Crea una reserva con estado **PENDING**.
                    Se requiere permisos de ADMIN para acceder a este endpoint.
                    El SAGA orquestado (booking-orchestrator-service) llama a este endpoint
                    y luego coordina la reserva con la función.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Reserva creada con estado PENDING",
                    headers = @Header(
                            name = "location",
                            description = "URL del recurso creado (ej: /api/v1/bookings/123e4567-e89b-12d3-a456-426614174000)",
                            schema = @Schema(type = "string", format = "uri")
                    ),
                    content = @Content(schema = @Schema(implementation = BookingResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT asuente o inválido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Permisos insuficientes para crear reservas",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<BookingResponse> createBooking(
            @RequestBody(
                    description = "Datos de la reserva que se desea crear",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = BookingRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Reserva para una función",
                                            summary = "Ejemplo de reserva para la función de Batman The Dark Knight",
                                            value = """
                                                        {
                                                          "screeningId": "bf22f406-ff9f-4e66-9075-6ba45ace17a8",
                                                          "movieTitle": "The Dark Knight",
                                                          "customerId": "14f97cf4-1204-47f0-ac9b-cb25fe0ab04c",
                                                          "totalAmount": "12.00"
                                                        }
                                                    """
                                    )
                            }
                    )
            )
            BookingRequest bookingRequest);

    @Operation(
            summary = "Confirmar reserva",
            description = """
                    Transición de estado **PENDING → CANCELLED**
                    Solo es válido si la orden esta en estado PENDING
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva confirmada (status: CONFIRMED)",
                    content = @Content(schema = @Schema(implementation = BookingResponse.class))),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Transición inválida — la reserva no está en PENDING",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<BookingResponse> confirmBooking(
            @Parameter(description = "UUID de la reserva", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable UUID id);


    @Operation(
            summary = "Cancelar reserva",
            description = """
                    Transición de estado **PENDING | CONFIRMED → CANCELLED**
                    Solo es válido si la orden esta en estado PENDING o CONFIRMED
                    Usado por el SAGA de compensaciones cuando falla el proceso de reserva
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva confirmada (status: CONFIRMED)",
                    content = @Content(schema = @Schema(implementation = BookingResponse.class))),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Transición inválida — la reserva ya se encuentra en estado CANCELLED",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<BookingResponse> cancelBooking(
            @Parameter(description = "UUID de la reserva", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable UUID id);
}