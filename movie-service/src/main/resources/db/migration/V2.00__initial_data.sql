INSERT INTO movies (id, title, director, genre, duration_minutes, rating, active)
VALUES
    ('5e187eec-c6a1-43f0-a1fc-1f1a9e292da8', 'Inception', 'Christopher Nolan', 'SCI_FI', 148, 'PG-13', TRUE),
    ('4f18ce60-ad60-407e-9caa-84ae19a55469', 'The Dark Knight', 'Christopher Nolan', 'ACTION', 152, 'PG-13', TRUE),
    ('ac4a9b65-3698-4cec-8f03-4e4d511706e4', 'Parásitos', 'Bong Joon-ho', 'DRAMA', 132, 'R', TRUE),
    ('fe099645-691d-4a53-99c8-93796d7c8898', 'Toy Story', 'John Lasseter', 'ANIMATION', 81, 'G', TRUE),
    ('171fff2f-19a8-41d2-bf8d-1e4051fbd180', 'El Conjuro', 'James Wan', 'HORROR', 112, 'R', TRUE),
    ('83444c64-8f75-4c66-8e13-c8230c877a4a', 'Titanic', 'James Cameron', 'ROMANCE', 195, 'PG-13', TRUE),
    ('084b1946-f384-46d1-839c-cc79ac9ef85a', 'Superbad', 'Greg Mottola', 'COMEDY', 113, 'R', TRUE),
    ('847b1fa1-59f5-47f9-8525-ca2fe40105a0', 'Se7en', 'David Fincher', 'THRILLER', 127, 'R', TRUE),
    ('37c68be9-1003-4b02-8e03-ca8c15a0b84e', 'Interstellar', 'Christopher Nolan', 'SCI_FI', 169, 'PG-13', TRUE),
    ('e41937e4-e94b-412c-977d-a22e03214988', 'El Viaje de Chihiro', 'Hayao Miyazaki', 'ANIMATION', 125, 'PG', TRUE),
    ('78dbfd0b-8f83-48e6-8fbc-f98ba0fe7abd', 'Joker', 'Todd Phillips', 'DRAMA', 122, 'R', TRUE),
    ('e224cb53-f300-4d74-bd56-512e72ff53e1', 'Avengers: Endgame', 'Anthony Russo', 'ACTION', 181, 'PG-13', TRUE),
    ('8f265e4f-3b42-41f6-8821-2f6bdae31312', 'Get Out', 'Jordan Peele', 'HORROR', 104, 'R', TRUE),
    ('28e9fa16-63b0-482c-9d2f-546d6006e3b5', 'La La Land', 'Damien Chazelle', 'ROMANCE', 128, 'PG-13', TRUE),
    ('e53f0be1-d2f8-4792-9ddf-c79ec4bc7c10', 'Knives Out', 'Rian Johnson', 'THRILLER', 130, 'PG-13', TRUE),
    ('3442c396-5114-4423-a271-dcbd2b631533', 'La Máscara', 'Chuck Russell', 'COMEDY', 101, 'PG-13', FALSE),
    ('549efc4e-be2a-4126-9025-db222f7734e1', 'El Padrino', 'Francis Ford Coppola', 'DRAMA', 175, 'R', TRUE),
    ('b1db422f-fa8a-4c16-8096-5d9e442a98aa', 'Mad Max: Fury Road', 'George Miller', 'ACTION', 120, 'R', TRUE),
    ('71aeac70-5947-43f3-9f6c-5326519080c6', 'Coco', 'Lee Unkrich', 'ANIMATION', 105, 'PG', TRUE),
    ('804943d7-0461-4fff-a30b-ac8cfc7537f9', 'Everything Everywhere All at Once', 'Daniel Kwan', 'OTHER', 139, 'R', TRUE),
    ('bebc13a6-b8eb-4cf3-82ef-725615e8a9ec', 'El Resplandor', 'Stanley Kubrick', 'HORROR', 146, 'R', TRUE),
    ('d5515f85-8574-404d-a36d-2f848f324f5a', 'Diario de una Pasión', 'Nick Cassavetes', 'ROMANCE', 123, 'PG-13', TRUE),
    ('0df061a2-1cbd-4d17-905b-aad44b8d54c6', '¿Qué Pasó Ayer?', 'Todd Phillips', 'COMEDY', 100, 'R', TRUE),
    ('2b6d2bde-d2fc-4cd1-b166-6714366f47ab', 'Misión Imposible', 'Brian De Palma', 'ACTION', 110, 'PG-13', TRUE),
    ('e0101f34-a3a2-4470-8359-3ad837227bcd', 'Blade Runner', 'Ridley Scott', 'SCI_FI', 117, 'R', TRUE);

INSERT INTO screenings (id, movie_id, room, show_at, total_seats, available_seats, active)
VALUES ('bf22f406-ff9f-4e66-9075-6ba45ace17a8', '4f18ce60-ad60-407e-9caa-84ae19a55469', 'Sala 1', '2026-07-15 20:30:00', 100, 100, TRUE),
       ('bf9fcd41-df69-4118-a487-8259628f9dbd', '78dbfd0b-8f83-48e6-8fbc-f98ba0fe7abd', 'Sala 2', '2026-07-15 18:00:00', 100, 100, TRUE),
       ('ba82db4f-5eaf-46b0-931e-fb8d4282cfbf', '37c68be9-1003-4b02-8e03-ca8c15a0b84e', 'Sala IMAX', '2026-07-16 21:00:00', 100, 100, TRUE),
       ('fe099645-691d-4a53-99c8-93796d7c8898', 'fe099645-691d-4a53-99c8-93796d7c8898', 'Sala 1', '2026-07-16 17:00:00', 100, 100, TRUE);
