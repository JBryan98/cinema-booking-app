infra-up:
	docker compose -f docker/docker-compose.yml up -d
	@echo ""
	@echo "  Infraestructura levantada. Accesos:"
	@echo "  ------------------------------------"
	@echo "  moviedb:   http://localhost:5432"
	@echo "  bookingdb: http://localhost:5433 (disponible desde modulo 09)"
	@echo "  kafka_ui:  http://localhost:9000  (disponible desde modulo 10)"
	@echo ""

infra-down:
	docker compose -f docker/docker-compose.yml down

# Elimina contenedores Y volúmenes — útil cuando cambia la estructura de la BD
# o se quiere empezar desde cero (re-ejecuta los scripts de init-db)
infra-clean:
	docker compose -f docker/docker-compose.yml down -v
	@echo "Contenedores y volumenes eliminados. Usa make infra-up para empezar desde cero."

infra-restart: infra-down infra-up

ps:
	docker compose -f docker/docker-compose.yml ps

logs:
	docker logs -f $(service)

build:
	cd $(service) && mvn package -DskipTests -q
	@echo "Build de $(service) completado -> $(service)/target/"

# Reconstruye la imagen Docker de un servicio y reinicia solo ese contenedor.
# Usar cuando cambia codigo de un servicio que corre en Docker (ej: payment-gateway-service).
docker-rebuild:
	docker compose -f docker/docker-compose.yml build $(service)
	docker compose -f docker/docker-compose.yml up -d --no-deps $(service)
	@echo "$(service) reconstruido y reiniciado"