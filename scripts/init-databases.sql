-- Runs once on first container start.
-- Creates one database per microservice — each service owns its own schema.
CREATE DATABASE auth_db;
CREATE DATABASE product_db;
CREATE DATABASE order_db;
CREATE DATABASE notification_db;
