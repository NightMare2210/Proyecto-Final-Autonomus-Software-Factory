CREATE TABLE IF NOT EXISTS propiedad (
    id                   VARCHAR(36)   PRIMARY KEY,
    nombre               VARCHAR(150)  NOT NULL,
    descripcion          VARCHAR(2000) NOT NULL,
    precio               DECIMAL(15,2) NOT NULL,
    estado               VARCHAR(20)   NOT NULL CHECK (estado IN ('DISPONIBLE','RESERVADA','VENDIDA')),
    ultima_actualizacion TIMESTAMP     NOT NULL
);
