# 🚗 Car Rental Management System - Authentication Documentation

## Overview

This car rental management system features a comprehensive authentication and user management system with the following capabilities:

### ✨ Key Features

- **🔐 Secure Login & Registration**: SHA-256 password hashing for security
- **👥 Multiple User Types**: Admin, Member Customer, Non-Member Customer
- **🎮 Interactive CLI**: Beautiful menus with loading bars and Unicode characters
- **💾 Persistent Storage**: User data saved to encrypted files
- **🎁 Loyalty Program**: Points and tier system for members
- **📊 User Dashboards**: Role-specific interfaces for different user types

## 🏗️ System Architecture

### User Hierarchy
```
User (Abstract Base Class)
├── Admin
└── Customer (Abstract)
    ├── MemberCustomer
    └── NonMemberCustomer
```

### Core Components
- **AuthenticationManager**: Handles login, registration, and user validation
- **DashboardManager**: Manages user-specific dashboards and menus
- **App**: Main application controller

## 🚀 Getting Started

### Default Admin Account
- **Username**: `admin`
- **Password**: `admin123`
- **Access Level**: Full system access

### Running the Application
```bash
# Compile
javac -cp . Main.java

# Run
java Main
```

## 👤 User Types & Features

### 🔧 Admin Users
**Features:**
- Vehicle management
- View all rentals
- Customer management
- Generate reports
- System settings
- User account management

**Registration Fields:**
- Username, Password, Name, Email, Phone
- Admin ID
- Department

### ⭐ Member Customers
**Features:**
- Earn loyalty points on rentals
- Tier-based discounts (Bronze: 5%, Silver: 10%, Gold: 15%, Platinum: 20%)
- Priority customer support
- Exclusive promotions
- Rental history tracking

**Loyalty Tiers:**
- **Bronze**: 0-499 points (5% discount)
- **Silver**: 500-1999 points (10% discount)
- **Gold**: 2000-4999 points (15% discount)
- **Platinum**: 5000+ points (20% discount)

**Registration Fields:**
- Username, Password, Name, Email, Phone
- Address, Driving License Number, License Expiry
- Automatic membership ID generation

### 👤 Non-Member Customers
**Features:**
- Basic rental services
- Limited promotions (for high spenders)
- Option to upgrade to membership
- Basic rental history

**Registration Fields:**
- Username, Password, Name, Email, Phone
- Address, Driving License Number, License Expiry

## 🔒 Security Features

### Password Requirements
- Minimum 6 characters
- Must contain at least one letter and one number
- Passwords are hashed using SHA-256
- Confirmation required during registration

### Input Validation
- **Email**: Valid email format required
- **Phone**: International phone number format
- **Username**: 3-20 characters, alphanumeric and underscores only
- **License**: Minimum 5 characters, expiry date validation
- **Names**: Letters and spaces only

### Account Security
- Account activation/deactivation
- Session management
- Secure password storage

## 🎮 User Interface Features

### Visual Elements
- **Loading Bars**: Animated progress indicators
- **Unicode Icons**: Emojis and symbols for better UX
- **Box Drawing**: ASCII art for professional menus
- **Color Coding**: Visual feedback for success/error states

### Navigation
- **Number-based Menus**: Easy option selection
- **Back Navigation**: Return to previous menus
- **Error Handling**: Clear error messages with retry options
- **Help Text**: Contextual guidance

## 💾 Data Storage

### File Structure
```
users.dat - Serialized user data (HashMap<String, User>)
```

### Data Persistence
- Automatic save on user creation/modification
- Automatic load on application startup
- Backup and recovery support

## 🔧 Administrative Functions

### User Management
```java
// Available admin operations
authManager.deleteUser(username);
authManager.deactivateUser(username);
authManager.activateUser(username);
```

### Customer Conversion
```java
// Convert non-member to member
NonMemberCustomer nonMember = // ...
MemberCustomer member = nonMember.convertToMember(membershipId);
```

## 📊 Dashboard Features

### Admin Dashboard
- Vehicle Management
- View All Rentals
- Customer Management
- Generate Reports
- System Settings
- Admin Profile View
- Password Change

### Member Customer Dashboard
- Browse & Rent Vehicles
- Rental History
- Loyalty Points & Benefits
- Account Management
- Payment Methods
- Customer Support
- Password Change

### Non-Member Customer Dashboard
- Browse & Rent Vehicles
- Rental History
- Membership Upgrade Option
- Account Management
- Payment Methods
- Customer Support
- Password Change

## 🎯 Future Enhancements

### Planned Features
- Email verification during registration
- Two-factor authentication
- Password reset functionality
- Social media login integration
- Advanced role-based permissions
- Audit logging
- Real-time notifications

### Integration Points
- **VehicleManager**: For vehicle browsing and rental
- **RentalManager**: For rental operations
- **PaymentManager**: For payment processing
- **LoyaltyPointManager**: For points calculation

## 🔍 Usage Examples

### Creating a New Member Account
1. Select "Create New Account" from main menu
2. Choose "Customer (Member)"
3. Enter personal information
4. Provide address and license details
5. Account created with automatic membership ID

### Admin Login
1. Select "Login to Your Account"
2. Enter username: `admin`
3. Enter password: `admin123`
4. Access admin dashboard

### Viewing Loyalty Information
1. Login as member customer
2. Select "View Loyalty Points & Benefits"
3. See current tier, points, and discount rate
4. View tier progression requirements

## 🛠️ Troubleshooting

### Common Issues
- **Compilation Errors**: Ensure all dependencies are in classpath
- **File Permission Issues**: Check write permissions for user data file
- **Unicode Display**: Ensure terminal supports UTF-8 encoding
- **Clear Screen**: Falls back to newlines if cls command fails

### Error Messages
- **❌ Username already exists**: Choose a different username
- **❌ Invalid email format**: Use proper email format (user@domain.com)
- **❌ Passwords do not match**: Ensure confirmation matches password
- **❌ Account is deactivated**: Contact administrator

## 📝 Development Notes

### Class Relationships
- All user classes implement Serializable for file storage
- Abstract methods ensure consistent interface across user types
- Factory pattern used for user creation based on type

### Design Patterns Used
- **Template Method**: Abstract User class with concrete implementations
- **Factory Method**: User creation based on type selection
- **Singleton**: AuthenticationManager as single point of authentication
- **Observer**: Future implementation for real-time updates

This authentication system provides a solid foundation for the car rental application with room for future expansion and integration with other system components.
