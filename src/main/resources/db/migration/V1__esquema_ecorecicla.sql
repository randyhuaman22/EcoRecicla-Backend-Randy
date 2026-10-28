CREATE TABLE privilegios (id BIGSERIAL PRIMARY KEY, codigo VARCHAR(40) NOT NULL UNIQUE);
CREATE TABLE roles (id BIGSERIAL PRIMARY KEY, codigo VARCHAR(30) NOT NULL UNIQUE);
CREATE TABLE roles_privilegios (rol_id BIGINT NOT NULL REFERENCES roles(id), privilegio_id BIGINT NOT NULL REFERENCES privilegios(id), PRIMARY KEY (rol_id, privilegio_id));
CREATE TABLE usuarios (id BIGSERIAL PRIMARY KEY, username VARCHAR(50) NOT NULL UNIQUE, nombre VARCHAR(120) NOT NULL, password_hash VARCHAR(100) NOT NULL, activo BOOLEAN NOT NULL DEFAULT TRUE, creado_en TIMESTAMPTZ NOT NULL DEFAULT now(), actualizado_en TIMESTAMPTZ);
CREATE TABLE usuarios_roles (usuario_id BIGINT NOT NULL REFERENCES usuarios(id), rol_id BIGINT NOT NULL REFERENCES roles(id), PRIMARY KEY (usuario_id, rol_id));
CREATE TABLE puntos_acopio (id BIGSERIAL PRIMARY KEY, razon_social VARCHAR(160) NOT NULL, direccion VARCHAR(240) NOT NULL, zona VARCHAR(80) NOT NULL, material_habitual VARCHAR(60) NOT NULL, activo BOOLEAN NOT NULL DEFAULT TRUE, creado_por BIGINT REFERENCES usuarios(id), creado_en TIMESTAMPTZ NOT NULL DEFAULT now(), actualizado_en TIMESTAMPTZ);
CREATE INDEX ix_puntos_zona ON puntos_acopio (zona);
CREATE TABLE rutas (id BIGSERIAL PRIMARY KEY, fecha DATE NOT NULL, estado VARCHAR(12) NOT NULL CHECK (estado IN ('ABIERTA','CERRADA')), operario_id BIGINT NOT NULL REFERENCES usuarios(id), cerrada_por BIGINT REFERENCES usuarios(id), cerrada_en TIMESTAMPTZ, creado_en TIMESTAMPTZ NOT NULL DEFAULT now(), actualizado_en TIMESTAMPTZ, UNIQUE(fecha, operario_id));
CREATE INDEX ix_rutas_fecha_operario ON rutas(fecha, operario_id);
CREATE TABLE rutas_puntos (id BIGSERIAL PRIMARY KEY, ruta_id BIGINT NOT NULL REFERENCES rutas(id), punto_id BIGINT NOT NULL REFERENCES puntos_acopio(id), secuencia INTEGER NOT NULL CHECK (secuencia > 0), motivo_omision VARCHAR(500), omitido_por BIGINT REFERENCES usuarios(id), omitido_en TIMESTAMPTZ, UNIQUE(ruta_id,punto_id), UNIQUE(ruta_id,secuencia));
CREATE TABLE pesajes (id BIGSERIAL PRIMARY KEY, ruta_id BIGINT NOT NULL REFERENCES rutas(id), punto_id BIGINT NOT NULL REFERENCES puntos_acopio(id), autor_id BIGINT NOT NULL REFERENCES usuarios(id), fecha_hora TIMESTAMPTZ NOT NULL, material VARCHAR(60) NOT NULL, peso_bruto NUMERIC(12,3) NOT NULL CHECK (peso_bruto > 0), tara NUMERIC(12,3) NOT NULL CHECK (tara >= 0), peso_neto NUMERIC(12,3) NOT NULL CHECK (peso_neto > 0 AND peso_neto = peso_bruto - tara), observacion VARCHAR(500), estado VARCHAR(12) NOT NULL CHECK (estado IN ('REGISTRADO','ANULADO')), motivo_anulacion VARCHAR(500), creado_en TIMESTAMPTZ NOT NULL DEFAULT now(), actualizado_en TIMESTAMPTZ, modificado_por BIGINT REFERENCES usuarios(id));
CREATE INDEX ix_pesajes_punto_fecha ON pesajes(punto_id, fecha_hora DESC);
CREATE INDEX ix_pesajes_ruta ON pesajes(ruta_id);
CREATE INDEX ix_pesajes_autor_fecha ON pesajes(autor_id, fecha_hora);

