MERGE INTO propiedad (id, nombre, descripcion, precio, estado, ultima_actualizacion) KEY (id)
VALUES ('3fa85f64-5717-4562-b3fc-2c963f66afa6',
        'Apartamento Sector Norte — Piso 4',
        'Apartamento de 3 habitaciones con vista panorámica, cocina integral y parqueadero cubierto.',
        285000000.00,
        'DISPONIBLE',
        '2026-06-27 10:00:00');

MERGE INTO propiedad (id, nombre, descripcion, precio, estado, ultima_actualizacion) KEY (id)
VALUES ('b2e8c4a1-1234-5678-9abc-def012345678',
        'Casa Conjunto Cerrado Sur',
        'Casa de 4 habitaciones en conjunto cerrado con zonas verdes, piscina y cancha de tenis.',
        520000000.00,
        'RESERVADA',
        '2026-06-27 11:00:00');

MERGE INTO propiedad (id, nombre, descripcion, precio, estado, ultima_actualizacion) KEY (id)
VALUES ('c9f1d2e3-abcd-ef01-2345-6789abcdef01',
        'Local Comercial Centro Histórico',
        'Local de 80 m² en zona de alto tráfico peatonal, ideal para comercio o consultorio.',
        195000000.00,
        'VENDIDA',
        '2026-06-27 12:00:00');
