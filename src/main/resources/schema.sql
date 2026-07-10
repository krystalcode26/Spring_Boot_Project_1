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
    firstname VARCHAR(255),
    lastname VARCHAR(255),
    age INTEGER,
    salary NUMERIC,
    email VARCHAR(255),
    department VARCHAR(100),
    password VARCHAR(255),
    role VARCHAR(50)
);

ALTER TABLE students ADD COLUMN IF NOT EXISTS password VARCHAR(255);
ALTER TABLE students ADD COLUMN IF NOT EXISTS role VARCHAR(50);

ALTER TABLE employee ADD COLUMN IF NOT EXISTS firstname VARCHAR(255);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS lastname VARCHAR(255);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS email VARCHAR(255);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS department VARCHAR(100);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS password VARCHAR(255);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS role VARCHAR(50);

CREATE UNIQUE INDEX IF NOT EXISTS uq_employee_email ON employee (email);

-- Ensure empid auto-increments even if the table was created earlier without BIGSERIAL
CREATE SEQUENCE IF NOT EXISTS employee_empid_seq;
SELECT setval(
    'employee_empid_seq',
    COALESCE((SELECT MAX(empid) FROM employee), 0) + 1,
    false
);
ALTER TABLE employee ALTER COLUMN empid SET DEFAULT nextval('employee_empid_seq');
ALTER SEQUENCE employee_empid_seq OWNED BY employee.empid;

CREATE TABLE IF NOT EXISTS department (
    deptid SERIAL PRIMARY KEY,
    deptname VARCHAR(255) NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS department_deptid_seq;
SELECT setval(
    'department_deptid_seq',
    COALESCE((SELECT MAX(deptid) FROM department), 0) + 1,
    false
);
ALTER TABLE department ALTER COLUMN deptid SET DEFAULT nextval('department_deptid_seq');
ALTER SEQUENCE department_deptid_seq OWNED BY department.deptid;

-- Many-to-many join table: employee <-> department
CREATE TABLE IF NOT EXISTS employee_department (
    empid BIGINT NOT NULL REFERENCES employee(empid),
    deptid INTEGER NOT NULL REFERENCES department(deptid),
    PRIMARY KEY (empid, deptid)
);
