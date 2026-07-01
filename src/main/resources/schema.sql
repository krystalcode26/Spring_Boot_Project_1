CREATE TABLE IF NOT EXISTS staffs (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

-- Many-to-many join table: employee <-> department
CREATE TABLE IF NOT EXISTS employee_department (
    empid BIGINT NOT NULL REFERENCES employee(empid),
    deptid INTEGER NOT NULL REFERENCES department(deptid),
    PRIMARY KEY (empid, deptid)
);
