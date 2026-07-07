CREATE TABLE IF NOT EXISTS staffs (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS students (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    role VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS employee (
    empid BIGSERIAL PRIMARY KEY,
    empname VARCHAR(255) NOT NULL,
    age INTEGER,
    salary NUMERIC,
    email VARCHAR(255),
    password VARCHAR(255),
    role VARCHAR(50)
);

ALTER TABLE students ADD COLUMN IF NOT EXISTS password VARCHAR(255);
ALTER TABLE students ADD COLUMN IF NOT EXISTS role VARCHAR(50);

ALTER TABLE employee ADD COLUMN IF NOT EXISTS email VARCHAR(255);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS password VARCHAR(255);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS role VARCHAR(50);

CREATE UNIQUE INDEX IF NOT EXISTS uq_employee_email ON employee (email);

-- Many-to-many join table: employee <-> department
CREATE TABLE IF NOT EXISTS employee_department (
    empid BIGINT NOT NULL REFERENCES employee(empid),
    deptid INTEGER NOT NULL REFERENCES department(deptid),
    PRIMARY KEY (empid, deptid)
);
