# Vehicle Rental Service System - OOP Assignment

## 📋 Assignment Overview

**Course:** BMCS2023 Object-Oriented Programming  
**Session:** 202505  
**Assignment Type:** Group Assignment (4 members per team)  
**Submission Deadline:** 12 September 2025 (Week 12, Friday) by 11:59 PM  

### Learning Outcomes
- **CLO2:** Develop an object-oriented program using appropriate programming fundamentals with regards to arrays, methods, and exception handling.
- **CLO3:** Analyse the concepts of encapsulation, inheritance and polymorphism based on programming problems.

## 🎯 Project Description

This project implements a **Vehicle Rental Service System** using Java with object-oriented programming principles. The system manages vehicle rentals, customers, and provides a comprehensive rental management solution.

### Core Features Required
- ✅ Add/register new customers
- ✅ Add new vehicles to the system
- ✅ Display available vehicles
- ✅ Rent a vehicle to a customer
- ✅ Return a rented vehicle
- ✅ Calculate and display rental charges
- ✅ Rental history

### OOP Principles Implemented
- **Encapsulation:** Private fields with public getter/setter methods
- **Inheritance:** User → Admin/Customer hierarchy
- **Polymorphism:** Abstract classes and method overriding
- **Interface Usage:** Weak relationships between objects

## 🏗️ Project Structure

```
OPP-ASSIGNMENT/
├── Main.java                    # Entry point of the application
├── com/
│   └── rentalapp/
│       ├── App.java            # Main application controller with menu systems
│       ├── auth/               # Authentication and user management
│       │   ├── User.java       # Abstract base class for all users
│       │   ├── Admin.java      # Admin user class
│       │   ├── Customer.java   # Abstract customer class
│       │   ├── MemberCustomer.java    # Member customer implementation
│       │   └── NonMemberCustomer.java # Non-member customer implementation
│       ├── vehicle/            # Vehicle management
│       ├── rental/             # Rental operations
│       ├── maintenance/        # Vehicle maintenance
│       └── utils/              # Utility classes
└── README.md                   # This file
```

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Git for version control
- IDE (recommended: VS Code, IntelliJ IDEA, or Eclipse)

### Running the Application

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd OPP-ASSIGNMENT
   ```

2. **Compile the project:**
   ```bash
   javac -cp . Main.java com/rentalapp/App.java
   ```

3. **Run the application:**
   ```bash
   java Main
   ```

### Menu Navigation
The application provides the following menu structure:
- **Main Menu:** Login, Register, Exit
- **Login Menu:** Admin Login, Customer Login
- **Admin Menu:** Vehicle Management, View Rentals, Customer Management, Reports
- **Customer Menu:** Rental Services, Account Management, Rental History
- **Rental Menu:** Browse Vehicles, Rent Vehicle, Return Vehicle, Extend Rental
- **Account Menu:** Profile Management, Password Change, Loyalty Points

## 👥 Team Collaboration Guidelines

### Git Workflow

1. **Branch Strategy:**
   ```bash
   # Create feature branches for each module
   git checkout -b feature/vehicle-management
   git checkout -b feature/rental-system
   git checkout -b feature/user-authentication
   ```

2. **Before Starting Work:**
   ```bash
   git pull origin main
   git checkout -b feature/your-feature-name
   ```

3. **After Completing Work:**
   ```bash
   git add .
   git commit -m "Add: brief description of changes"
   git push origin feature/your-feature-name
   ```

4. **Create Pull Request:**
   - Go to repository on GitHub
   - Create Pull Request from your feature branch to main
   - Request team review before merging

### Module Distribution
Each team member should work on specific modules:
- **Member 1:** User Authentication & Admin Management
- **Member 2:** Vehicle Management & Maintenance
- **Member 3:** Rental Operations & Billing
- **Member 4:** Customer Management & Reports

### Code Standards
- Follow Java naming conventions
- Add JavaDoc comments for all public methods
- Use meaningful variable and method names
- Implement proper error handling with try-catch blocks
- Maintain consistent indentation (4 spaces)

## 📝 Development Guidelines

### Testing Your Code
Before pushing changes, always test:
```bash
# Compile and check for errors
javac -cp . Main.java com/rentalapp/App.java com/rentalapp/auth/*.java

# Run the application
java Main

# Test your specific functionality
```

### Common Git Commands
```bash
# Check status
git status

# Pull latest changes
git pull origin main

# Add files
git add .

# Commit changes
git commit -m "Your commit message"

# Push to your branch
git push origin your-branch-name

# Switch branches
git checkout branch-name

# Merge main into your branch
git merge main
```

### File Naming Convention
- Use PascalCase for class names (e.g., `VehicleManager.java`)
- Use camelCase for method and variable names
- Use UPPERCASE for constants

## 📅 Important Deadlines

### Initial Stage - 1 August 2025 (Week 6)
- [ ] Cover page
- [ ] Team assignment idea description
- [ ] Module distribution among members
- [ ] Draft UML class diagram

### Final Report - 12 September 2025 (Week 12)
- [ ] Amended initial stage report
- [ ] Peer evaluation forms
- [ ] Sample screenshots and reports
- [ ] OOP principles explanation with code screenshots
- [ ] Complete Java source code
- [ ] Final UML class diagram

### Presentation - Week 13 & 14
- [ ] Prepare demonstration
- [ ] Explain OOP implementation
- [ ] Show system functionality

## ⚠️ Important Notes

### Late Submission Policy
- **1-3 days late:** 10 marks deduction
- **4-7 days late:** 20 marks deduction
- **After 7 days:** Zero marks

### Academic Integrity
- Work must be original
- No plagiarism or collusion
- Only collaborate with your team members
- Do not use copyrighted images for GUI

### Development Tips
- Use Git regularly to avoid losing work
- Test your code frequently
- Comment your code thoroughly
- Follow OOP principles strictly
- Each member must contribute equally

## 📞 Support

If you encounter issues:
1. Check this README first
2. Discuss with team members
3. Contact your tutor BEFORE deadlines
4. Use course resources and materials

## 🎓 Team Members
- **Member 1:** [Name] - [Modules]
- **Member 2:** [Name] - [Modules]
- **Member 3:** [Name] - [Modules]
- **Member 4:** [Name] - [Modules]

---

**Good luck with your assignment! Remember to start early and collaborate effectively!** 🚀