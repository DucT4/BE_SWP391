-- =========================================================
-- DATABASE SCRIPT FINAL - Dùng VARCHAR cho enum (giống role)
-- =========================================================

DROP DATABASE IF EXISTS ev_warranty;
CREATE DATABASE ev_warranty;
USE ev_warranty;

/* =========================================================
   1) LOOKUPS - Xóa các bảng lookup không dùng nữa
   ========================================================= */

-- Warehouse types
CREATE TABLE lkp_warehouse_type(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(30) NOT NULL UNIQUE
);

-- Resolution type (vẫn giữ vì dùng FK)
CREATE TABLE lkp_resolution_type(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(40) NOT NULL UNIQUE
);

-- Assignment status
CREATE TABLE lkp_assignment_status(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(30) NOT NULL UNIQUE
);

-- Shipment status
CREATE TABLE lkp_shipment_status(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(40) NOT NULL UNIQUE
);

-- GRN status
CREATE TABLE lkp_grn_status(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(40) NOT NULL UNIQUE
);

-- Issue status
CREATE TABLE lkp_issue_status(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(40) NOT NULL UNIQUE
);

-- RMA status
CREATE TABLE lkp_rma_status(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(40) NOT NULL UNIQUE
);

-- Allocation status
CREATE TABLE lkp_allocation_status(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(40) NOT NULL UNIQUE
);

-- Settlement status
CREATE TABLE lkp_settlement_status(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(40) NOT NULL UNIQUE
);

-- Campaign type
CREATE TABLE lkp_campaign_type(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(40) NOT NULL UNIQUE
);

-- Campaign status
CREATE TABLE lkp_campaign_status(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(40) NOT NULL UNIQUE
);

-- Work order type
CREATE TABLE lkp_work_order_type(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(20) NOT NULL UNIQUE
);

-- Work order status
CREATE TABLE lkp_work_order_status(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(30) NOT NULL UNIQUE
);

-- Work order item type
CREATE TABLE lkp_item_type(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(20) NOT NULL UNIQUE
);

-- Payment method
CREATE TABLE lkp_payment_method(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(20) NOT NULL UNIQUE
);

-- Payment status
CREATE TABLE lkp_payment_status(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(20) NOT NULL UNIQUE
);

/* =========================================================
   2) USERS - ✅ Dùng VARCHAR cho role (lưu enum string)
   ========================================================= */

CREATE TABLE users(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(120) NOT NULL UNIQUE,
  email VARCHAR(150) NOT NULL UNIQUE,
  phone VARCHAR(20),
  password VARCHAR(255) NOT NULL,
  role VARCHAR(255) NOT NULL,                    -- ✅ Lưu enum: SC_STAFF, SC_TECHNICIAN, SC_MANAGER, EVM_STAFF, EVM_ADMIN
  is_active TINYINT(1) DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE service_centers(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(150) NOT NULL,
  address VARCHAR(200),
  region VARCHAR(100),
  manager_user_id BIGINT,
  FOREIGN KEY (manager_user_id) REFERENCES users(id)
);

CREATE TABLE warehouses(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(150) NOT NULL,
  type_id BIGINT NOT NULL,
  service_center_id BIGINT,
  address VARCHAR(200),
  FOREIGN KEY (type_id) REFERENCES lkp_warehouse_type(id),
  FOREIGN KEY (service_center_id) REFERENCES service_centers(id)
);

/* =========================================================
   3) CUSTOMER / VEHICLE / PARTS
   ========================================================= */

CREATE TABLE customers(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(150) NOT NULL,
  phone VARCHAR(20),
  email VARCHAR(150),
  address VARCHAR(200),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vehicles(
  vin VARCHAR(32) PRIMARY KEY,
  model VARCHAR(80) NOT NULL,
  customer_id BIGINT,
  purchase_date DATE,
  coverage_to DATE,
  FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE parts(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  part_no VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(150) NOT NULL,
  track_serial TINYINT(1) DEFAULT 0,
  track_lot TINYINT(1) DEFAULT 0,
  uom VARCHAR(20) DEFAULT 'EA'
);

CREATE TABLE part_policies(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  part_id BIGINT NOT NULL,
  warranty_months INT,
  limit_km INT,
  notes VARCHAR(200),
  FOREIGN KEY (part_id) REFERENCES parts(id)
);

CREATE TABLE part_substitutions(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  part_id BIGINT NOT NULL,
  substitute_part_id BIGINT NOT NULL,
  UNIQUE(part_id, substitute_part_id),
  FOREIGN KEY (part_id) REFERENCES parts(id),
  FOREIGN KEY (substitute_part_id) REFERENCES parts(id)
);

/* =========================================================
   4) CLAIMS - ✅ Dùng VARCHAR cho status và approval_level (giống role)
   ========================================================= */

CREATE TABLE claims(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  vin VARCHAR(32) NOT NULL,
  opened_by BIGINT NOT NULL,
  service_center_id BIGINT NOT NULL,
  status VARCHAR(40) NOT NULL,                   -- ✅ Lưu enum: SUBMITTED, APPROVED, REJECTED, CLOSED
  failure_desc TEXT,
  approval_level VARCHAR(40),                    -- ✅ Lưu enum: MANAGER, EVM
  resolution_type_id BIGINT NULL,
  resolution_note VARCHAR(300) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (vin) REFERENCES vehicles(vin),
  FOREIGN KEY (opened_by) REFERENCES users(id),
  FOREIGN KEY (service_center_id) REFERENCES service_centers(id),
  FOREIGN KEY (resolution_type_id) REFERENCES lkp_resolution_type(id)
);

CREATE TABLE claim_status_history(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  claim_id BIGINT NOT NULL,
  status VARCHAR(40) NOT NULL,                   -- ✅ Lưu enum ClaimStatus
  changed_by BIGINT NOT NULL,
  changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  note VARCHAR(200),
  FOREIGN KEY (claim_id) REFERENCES claims(id),
  FOREIGN KEY (changed_by) REFERENCES users(id)
);

CREATE TABLE claim_approvals(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  claim_id BIGINT NOT NULL,
  approver_id BIGINT NOT NULL,
  level VARCHAR(40) NOT NULL,                    -- ✅ Lưu enum ApprovalLevel
  decision VARCHAR(20) NOT NULL,
  decision_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  remark VARCHAR(200),
  FOREIGN KEY (claim_id) REFERENCES claims(id),
  FOREIGN KEY (approver_id) REFERENCES users(id)
);

CREATE TABLE claim_parts(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  claim_id BIGINT NOT NULL,
  part_id BIGINT NOT NULL,
  qty DECIMAL(12,2) NOT NULL,
  planned TINYINT(1) DEFAULT 1,
  serial_no VARCHAR(100),
  lot_no VARCHAR(50),
  FOREIGN KEY (claim_id) REFERENCES claims(id),
  FOREIGN KEY (part_id) REFERENCES parts(id)
);

CREATE TABLE claim_labour(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  claim_id BIGINT NOT NULL,
  technician_id BIGINT,
  hours DECIMAL(6,2) NOT NULL,
  rate DECIMAL(10,2) NOT NULL,
  note VARCHAR(200),
  FOREIGN KEY (claim_id) REFERENCES claims(id),
  FOREIGN KEY (technician_id) REFERENCES users(id)
);

CREATE TABLE claim_assignments(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  claim_id BIGINT NOT NULL,
  technician_id BIGINT NOT NULL,
  assigned_by BIGINT NOT NULL,
  status_id BIGINT NOT NULL,
  assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  accepted_at TIMESTAMP NULL,
  started_at TIMESTAMP NULL,
  completed_at TIMESTAMP NULL,
  note VARCHAR(200),
  FOREIGN KEY (claim_id) REFERENCES claims(id),
  FOREIGN KEY (technician_id) REFERENCES users(id),
  FOREIGN KEY (assigned_by) REFERENCES users(id),
  FOREIGN KEY (status_id) REFERENCES lkp_assignment_status(id)
);

/* =========================================================
   5) CAMPAIGNS
   ========================================================= */

CREATE TABLE campaigns(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  type_id BIGINT NOT NULL,
  name VARCHAR(150) NOT NULL,
  description TEXT,
  start_date DATE,
  end_date DATE,
  created_by BIGINT,
  status_id BIGINT NOT NULL,
  FOREIGN KEY (type_id) REFERENCES lkp_campaign_type(id),
  FOREIGN KEY (created_by) REFERENCES users(id),
  FOREIGN KEY (status_id) REFERENCES lkp_campaign_status(id)
);

CREATE TABLE campaign_vins(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  campaign_id BIGINT NOT NULL,
  vin VARCHAR(32) NOT NULL,
  status VARCHAR(30) DEFAULT 'Planned',
  UNIQUE(campaign_id, vin),
  FOREIGN KEY (campaign_id) REFERENCES campaigns(id),
  FOREIGN KEY (vin) REFERENCES vehicles(vin)
);

/* =========================================================
   6) WORK ORDERS
   ========================================================= */

CREATE TABLE work_orders(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  type_id BIGINT NOT NULL,
  campaign_id BIGINT NULL,
  vin VARCHAR(32) NOT NULL,
  service_center_id BIGINT NOT NULL,
  created_by BIGINT NOT NULL,
  assigned_tech_id BIGINT NULL,
  status_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (type_id) REFERENCES lkp_work_order_type(id),
  FOREIGN KEY (campaign_id) REFERENCES campaigns(id),
  FOREIGN KEY (vin) REFERENCES vehicles(vin),
  FOREIGN KEY (service_center_id) REFERENCES service_centers(id),
  FOREIGN KEY (created_by) REFERENCES users(id),
  FOREIGN KEY (assigned_tech_id) REFERENCES users(id),
  FOREIGN KEY (status_id) REFERENCES lkp_work_order_status(id)
);

CREATE TABLE work_order_inspections(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  work_order_id BIGINT NOT NULL,
  inspected_by BIGINT NOT NULL,
  report TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (work_order_id) REFERENCES work_orders(id),
  FOREIGN KEY (inspected_by) REFERENCES users(id)
);

CREATE TABLE work_order_items(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  work_order_id BIGINT NOT NULL,
  item_type_id BIGINT NOT NULL,
  description VARCHAR(200),
  part_id BIGINT NULL,
  qty DECIMAL(10,2) DEFAULT 1,
  unit_price DECIMAL(12,2) NOT NULL DEFAULT 0,
  is_approved TINYINT(1) DEFAULT 0,
  FOREIGN KEY (work_order_id) REFERENCES work_orders(id),
  FOREIGN KEY (item_type_id) REFERENCES lkp_item_type(id),
  FOREIGN KEY (part_id) REFERENCES parts(id)
);

CREATE TABLE customer_payments(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  work_order_id BIGINT NOT NULL,
  method_id BIGINT NOT NULL,
  status_id BIGINT NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  paid_at TIMESTAMP NULL,
  note VARCHAR(200),
  FOREIGN KEY (work_order_id) REFERENCES work_orders(id),
  FOREIGN KEY (method_id) REFERENCES lkp_payment_method(id),
  FOREIGN KEY (status_id) REFERENCES lkp_payment_status(id)
);

/* =========================================================
   7) INVENTORY
   ========================================================= */

CREATE TABLE stock(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  warehouse_id BIGINT NOT NULL,
  part_id BIGINT NOT NULL,
  qty_on_hand DECIMAL(12,2) DEFAULT 0,
  qty_reserved DECIMAL(12,2) DEFAULT 0,
  UNIQUE(warehouse_id, part_id),
  FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
  FOREIGN KEY (part_id) REFERENCES parts(id)
);

CREATE TABLE stock_serials(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  part_id BIGINT NOT NULL,
  serial_no VARCHAR(100) NOT NULL,
  warehouse_id BIGINT NOT NULL,
  status VARCHAR(20) NOT NULL,
  UNIQUE(serial_no, part_id),
  FOREIGN KEY (part_id) REFERENCES parts(id),
  FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)
);

CREATE TABLE parts_requests(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  service_center_id BIGINT NOT NULL,
  requested_by BIGINT NOT NULL,
  part_id BIGINT NOT NULL,
  qty DECIMAL(12,2) NOT NULL,
  status_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (service_center_id) REFERENCES service_centers(id),
  FOREIGN KEY (requested_by) REFERENCES users(id),
  FOREIGN KEY (part_id) REFERENCES parts(id),
  FOREIGN KEY (status_id) REFERENCES lkp_allocation_status(id)
);

CREATE TABLE parts_allocations(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  request_id BIGINT,
  claim_id BIGINT,
  source_wh_id BIGINT NOT NULL,
  dest_wh_id BIGINT NOT NULL,
  part_id BIGINT NOT NULL,
  qty_alloc DECIMAL(12,2) NOT NULL,
  eta_date DATE,
  status_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (request_id) REFERENCES parts_requests(id),
  FOREIGN KEY (claim_id) REFERENCES claims(id),
  FOREIGN KEY (source_wh_id) REFERENCES warehouses(id),
  FOREIGN KEY (dest_wh_id) REFERENCES warehouses(id),
  FOREIGN KEY (part_id) REFERENCES parts(id),
  FOREIGN KEY (status_id) REFERENCES lkp_allocation_status(id)
);

CREATE TABLE shipments(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  do_no VARCHAR(50) NOT NULL UNIQUE,
  source_wh_id BIGINT NOT NULL,
  dest_wh_id BIGINT NOT NULL,
  carrier VARCHAR(100),
  tracking_no VARCHAR(100),
  status_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (source_wh_id) REFERENCES warehouses(id),
  FOREIGN KEY (dest_wh_id) REFERENCES warehouses(id),
  FOREIGN KEY (status_id) REFERENCES lkp_shipment_status(id)
);

CREATE TABLE shipment_lines(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  shipment_id BIGINT NOT NULL,
  part_id BIGINT NOT NULL,
  qty DECIMAL(12,2) NOT NULL,
  lot_no VARCHAR(50),
  FOREIGN KEY (shipment_id) REFERENCES shipments(id),
  FOREIGN KEY (part_id) REFERENCES parts(id)
);

CREATE TABLE grn(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  shipment_id BIGINT NOT NULL,
  warehouse_id BIGINT NOT NULL,
  received_by BIGINT NOT NULL,
  status_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (shipment_id) REFERENCES shipments(id),
  FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
  FOREIGN KEY (received_by) REFERENCES users(id),
  FOREIGN KEY (status_id) REFERENCES lkp_grn_status(id)
);

CREATE TABLE grn_lines(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  grn_id BIGINT NOT NULL,
  part_id BIGINT NOT NULL,
  qty_ok DECIMAL(12,2) DEFAULT 0,
  qty_damaged DECIMAL(12,2) DEFAULT 0,
  FOREIGN KEY (grn_id) REFERENCES grn(id),
  FOREIGN KEY (part_id) REFERENCES parts(id)
);

CREATE TABLE issues(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  claim_id BIGINT NOT NULL,
  warehouse_id BIGINT NOT NULL,
  requested_by BIGINT NOT NULL,
  issued_by BIGINT,
  status_id BIGINT NOT NULL,
  work_order_id BIGINT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (claim_id) REFERENCES claims(id),
  FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
  FOREIGN KEY (requested_by) REFERENCES users(id),
  FOREIGN KEY (issued_by) REFERENCES users(id),
  FOREIGN KEY (status_id) REFERENCES lkp_issue_status(id),
  FOREIGN KEY (work_order_id) REFERENCES work_orders(id)
);

CREATE TABLE issue_lines(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  issue_id BIGINT NOT NULL,
  part_id BIGINT NOT NULL,
  qty DECIMAL(12,2) NOT NULL,
  serial_no VARCHAR(100),
  lot_no VARCHAR(50),
  FOREIGN KEY (issue_id) REFERENCES issues(id),
  FOREIGN KEY (part_id) REFERENCES parts(id)
);

CREATE TABLE rma(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  claim_id BIGINT NOT NULL,
  from_wh_id BIGINT NOT NULL,
  to_wh_id BIGINT NOT NULL,
  status_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (claim_id) REFERENCES claims(id),
  FOREIGN KEY (from_wh_id) REFERENCES warehouses(id),
  FOREIGN KEY (to_wh_id) REFERENCES warehouses(id),
  FOREIGN KEY (status_id) REFERENCES lkp_rma_status(id)
);

CREATE TABLE rma_lines(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  rma_id BIGINT NOT NULL,
  part_id BIGINT NOT NULL,
  qty DECIMAL(12,2) NOT NULL,
  serial_no VARCHAR(100),
  reason VARCHAR(200),
  FOREIGN KEY (rma_id) REFERENCES rma(id),
  FOREIGN KEY (part_id) REFERENCES parts(id)
);

CREATE TABLE settlements(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  claim_id BIGINT NOT NULL UNIQUE,
  status_id BIGINT NOT NULL,
  submitted_by BIGINT,
  submitted_at TIMESTAMP NULL,
  approved_by BIGINT,
  approved_at TIMESTAMP NULL,
  total_parts DECIMAL(12,2) DEFAULT 0,
  total_labour DECIMAL(12,2) DEFAULT 0,
  total_amount DECIMAL(12,2) GENERATED ALWAYS AS (total_parts + total_labour) STORED,
  FOREIGN KEY (claim_id) REFERENCES claims(id),
  FOREIGN KEY (status_id) REFERENCES lkp_settlement_status(id)
);

CREATE TABLE settlement_items(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  settlement_id BIGINT NOT NULL,
  item_type VARCHAR(20) NOT NULL,
  description VARCHAR(200),
  qty DECIMAL(10,2) DEFAULT 1,
  unit_price DECIMAL(12,2) NOT NULL,
  amount DECIMAL(12,2) GENERATED ALWAYS AS (qty*unit_price) STORED,
  FOREIGN KEY (settlement_id) REFERENCES settlements(id)
);

CREATE TABLE audit_logs(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  actor_id BIGINT,
  action VARCHAR(60) NOT NULL,
  entity VARCHAR(60) NOT NULL,
  entity_id BIGINT,
  before_json JSON,
  after_json JSON,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (actor_id) REFERENCES users(id)
);

/* =========================================================
   8) INSERT SAMPLE DATA
   ========================================================= */

-- Insert lookup data
INSERT INTO lkp_warehouse_type (name) VALUES ('EVM'), ('SC');
INSERT INTO lkp_resolution_type (name) VALUES ('REPLACE'), ('REPAIR'), ('INSPECT'), ('NFF');
INSERT INTO lkp_assignment_status (name) VALUES
('ASSIGNED'), ('ACCEPTED'), ('IN_PROGRESS'), ('COMPLETED'), ('CANCELLED');

-- Insert sample users with BCrypt hashed password "password123"
-- Hash: $2a$10$N9qo8uLOickgx2ZMRZoMye8bDmWqLnqiEqgYDnE5jNSZFQLHcFRe6
INSERT INTO users (username, password, email, phone, role, is_active) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye8bDmWqLnqiEqgYDnE5jNSZFQLHcFRe6', 'admin@evwarranty.com', '0909000001', 'EVM_ADMIN', 1),
('scstaff1', '$2a$10$N9qo8uLOickgx2ZMRZoMye8bDmWqLnqiEqgYDnE5jNSZFQLHcFRe6', 'scstaff1@sc.com', '0909000002', 'SC_STAFF', 1),
('sctech1', '$2a$10$N9qo8uLOickgx2ZMRZoMye8bDmWqLnqiEqgYDnE5jNSZFQLHcFRe6', 'sctech1@sc.com', '0909000003', 'SC_TECHNICIAN', 1),
('scmanager1', '$2a$10$N9qo8uLOickgx2ZMRZoMye8bDmWqLnqiEqgYDnE5jNSZFQLHcFRe6', 'scmanager1@sc.com', '0909000004', 'SC_MANAGER', 1),
('evmstaff1', '$2a$10$N9qo8uLOickgx2ZMRZoMye8bDmWqLnqiEqgYDnE5jNSZFQLHcFRe6', 'evmstaff1@evm.com', '0909000005', 'EVM_STAFF', 1);

-- Insert sample service center
INSERT INTO service_centers (code, name, address, region, manager_user_id) VALUES
('SC001', 'Service Center Hanoi', '123 Nguyen Trai, Hanoi', 'North', 4);

-- Insert sample customer
INSERT INTO customers (full_name, phone, email, address) VALUES
('Nguyen Van A', '0901234567', 'nguyenvana@email.com', '456 Le Loi, HCMC');

-- Insert sample vehicle
INSERT INTO vehicles (vin, model, customer_id, purchase_date, coverage_to) VALUES
('VIN1234567890ABCDEFG', 'EV Model X', 1, '2023-01-15', '2026-01-15');

-- Insert sample claim
INSERT INTO claims (vin, opened_by, service_center_id, status, failure_desc) VALUES
('VIN1234567890ABCDEFG', 3, 1, 'SUBMITTED', 'Battery not charging properly');

