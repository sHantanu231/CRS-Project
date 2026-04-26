# College Resource Sharing System (CRS)

# Overview
A Java-based desktop application that allows students and admins to share academic resources efficiently.

# Features
- User Authentication (Admin & Student)
- File Upload & Download
- File Validation (PDF, Java, C, C++, DOCX)
- Duplicate Detection using SHA-256 hashing
- Rating System for resources
- Admin Panel for management

## Technologies Used
- Java (Swing)
- MySQL
- JDBC

# Project Structure
- `com.crs.ui` → UI components
- `com.crs.db` → Database connection
- `com.crs.utils` → Utility classes
- `com.crs.admin` → Admin panel

# Security Features
- File type validation
- Duplicate detection using hashing
- Role-based access control

# Database

The database schema is provided in the file `database.sql`.

To set up:
1. Open MySQL Workbench
2. Import the `database.sql` file
3. Run the script to create tables and sample data

Tables included:
- users
- resources
- ratings

## 👨‍💻 Author
Shantanu Devrani
