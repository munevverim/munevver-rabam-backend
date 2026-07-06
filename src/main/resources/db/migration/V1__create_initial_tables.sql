CREATE TABLE IF NOT EXISTS cars (
                                    id BIGINT NOT NULL AUTO_INCREMENT,
                                    license_plate VARCHAR(255) NOT NULL,
    brand VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_cars_license_plate UNIQUE (license_plate)
    );

CREATE TABLE IF NOT EXISTS services (
                                        id BIGINT NOT NULL AUTO_INCREMENT,
                                        title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    version BIGINT,
    car_id BIGINT NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_services_car_id FOREIGN KEY (car_id) REFERENCES cars(id)
    );

CREATE INDEX idx_services_car_id ON services(car_id);
CREATE INDEX idx_services_status ON services(status);

CREATE TABLE IF NOT EXISTS audit_logs (
                                          id BIGINT NOT NULL AUTO_INCREMENT,
                                          event_type VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,
    payload TEXT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
    );

CREATE INDEX idx_audit_logs_event_type ON audit_logs(event_type);
CREATE INDEX idx_audit_logs_entity_type ON audit_logs(entity_type);
CREATE INDEX idx_audit_logs_entity_id ON audit_logs(entity_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);