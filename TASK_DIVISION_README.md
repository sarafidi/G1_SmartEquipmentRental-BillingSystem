# G1 Smart Equipment Rental & Billing System
## Task Division & Teammate Handoff Notes
**Deadline**: 8 July 2026
**Team**: 4 Members

---

## Project Timeline

> [!CAUTION]
> **Teammates A, B, and C must submit their completed modules by end of Day 4 (6 July) so the Group Leader has a full day to integrate and debug before submission.**

| Day | Date | Who | Milestone |
| :---: | :--- | :--- | :--- |
| **Day 1** | Thu, 3 Jul | **Leader** | ✅ Architecture complete. Project skeleton compiled and handed off. Teammates clone the repository and set up IntelliJ. |
| **Day 2** | Fri, 4 Jul | **A, B, C** | Implement controllers (`RentalController`, `BillingController`, `UserController`). Get all `// TODO` methods calling the Facade correctly. |
| **Day 3** | Sat, 5 Jul | **A, B, C** | Implement all view panels with full GUI layouts and event listeners (`RentalPanel`, `BillPanel`, `LoginDialog`, `UserPanel`). |
| **Day 4** | Sun, 6 Jul | **A, B, C** | 🔴 **HARD DEADLINE**: Push all completed work to the shared repository by **11:59 PM**. No new features after this point. |
| **Day 5** | Mon, 7 Jul | **Leader** | Integration day. Pull all teammates' work, resolve merge conflicts, debug end-to-end flows (login → rent → return → bill), and fix any cross-module issues. |
| **Day 6** | Tue, 8 Jul | **Leader** | Final review, packaging, and **submission**. |

### Daily Checkpoints for Teammates (4 Jul — 6 Jul)

> [!TIP]
> Post a brief update in the group chat at **end of each day** so the Leader can track progress:
> * ✅ Done — "Controller done, working on panel layout"
> * 🔄 In Progress — "Panel half done, login works but table not loading"
> * ❌ Blocked — "Can't figure out how to get current user from session"

---

## Overview

The project is split by **architectural layer**. The Group Leader owns the complete backend (data, models, business logic). Teammates A, B, and C own the GUI panels and MVC controllers for their assigned feature modules.

```
src/main/java/
├── Main.java                        ← YOU
├── util/                            ← YOU (complete)
│   ├── DataStore.java
│   ├── HashUtil.java
│   ├── IDGenerator.java
│   ├── SessionManager.java
│   └── Validator.java
├── model/                           ← YOU (complete)
│   ├── UserType.java
│   ├── RentalStatus.java
│   ├── RentalSystemFacade.java
│   ├── equipment/
│   │   ├── Equipment.java
│   │   ├── ElectronicsEquipment.java
│   │   ├── MediaEquipment.java
│   │   └── LabEquipment.java
│   ├── user/
│   │   ├── User.java
│   │   ├── Student.java
│   │   ├── Staff.java
│   │   └── UserManager.java
│   ├── rental/
│   │   ├── Rental.java
│   │   └── RentalManager.java
│   ├── bill/
│   │   ├── Bill.java
│   │   └── BillingManager.java
│   └── strategy/
│       ├── PricingStrategy.java
│       ├── StandardPricing.java
│       ├── DiscountedPricing.java
│       ├── PenaltyRule.java
│       ├── LatePenalty.java
│       └── DamagePenalty.java
├── controller/
│   ├── EquipmentController.java     ← YOU (complete)
│   ├── RentalController.java        ← TEAMMATE A
│   ├── BillingController.java       ← TEAMMATE B
│   └── UserController.java          ← TEAMMATE C
└── view/
    ├── MainFrame.java               ← YOU (complete)
    ├── EquipmentPanel.java          ← YOU
    ├── RentalPanel.java             ← TEAMMATE A
    ├── BillPanel.java               ← TEAMMATE B
    ├── LoginDialog.java             ← TEAMMATE C
    └── UserPanel.java               ← TEAMMATE C
```

---

## Module Ownership

### 🛠️ GROUP LEADER — Architecture, Backend & Equipment Module
* Full ownership of the `util`, `model`, and `strategy` packages.
* Implements `EquipmentController`, `EquipmentPanel`, and `MainFrame`.
* Responsible for integration testing and final assembly.

### 📅 TEAMMATE A — Rental Transaction Module
* Owns `RentalController.java` and `RentalPanel.java`.
* Implements the checkout form, return form, and active rentals table.

### 💵 TEAMMATE B — Billing Module
* Owns `BillingController.java` and `BillPanel.java`.
* Implements invoice display, bill history, and receipt formatting.

### 👤 TEAMMATE C — User & Session Module
* Owns `UserController.java`, `LoginDialog.java`, and `UserPanel.java`.
* Implements login flow, session control, and user registration forms.

---

## Handoff Notes

### For All Teammates

> [!IMPORTANT]
> **Rule 1 — Never touch the model or util packages.**
> All backend logic is complete. Do not modify files inside `model/`, `util/`, or `strategy/`. If you think something is wrong in the backend, tell the Group Leader.

> [!IMPORTANT]
> **Rule 2 — Only talk to the Facade in your Controller.**
> Your controller must only call `RentalSystemFacade.getInstance()`. Never import or instantiate `EquipmentManager`, `RentalManager`, `BillingManager`, `UserManager`, or `DataStore` directly.

> [!IMPORTANT]
> **Rule 3 — Only talk to the Controller in your Panel.**
> Your view panel must never import or call `RentalSystemFacade` or any Manager class directly. Panels call their assigned controller only.

> [!TIP]
> **Get the logged-in user anywhere in your panel using:**
> ```java
> User currentUser = SessionManager.getInstance().getCurrentUser();
> ```

> [!TIP]
> **Validate inputs in your controller before calling the facade:**
> ```java
> if (!Validator.isNonEmpty(name)) { /* show error */ }
> if (!Validator.isValidEmail(email)) { /* show error */ }
> ```

> [!WARNING]
> **Default admin login credentials (for testing):**
> * User ID: `USR-001`
> * Password: `admin123`
> * Run `Main.java` first to auto-create the `data/` folder and seed the admin account.

---

### For Teammate A — Rental Module

**Your files:**
* `controller/RentalController.java`
* `view/RentalPanel.java`

**What to implement:**

In `RentalController.java`, fill in the `// TODO` methods:
```java
// For renting:
facade.rentEquipment(userId, equipmentId, days);

// For returning:
facade.returnEquipment(rentalId, condition); // condition = "Excellent", "Good", "Damaged"

// For listing:
facade.listAllRentals();
facade.getUserRentals(userId);
```

In `RentalPanel.java`, build:
1. A **checkout form** — dropdown/field for Equipment ID, user ID input, number of days input, and a **Rent** button.
2. A **return form** — dropdown for active rental ID, condition selector (Excellent/Good/Damaged), and a **Return** button.
3. A **JTable** displaying all active rentals with columns: Rental ID, User, Equipment, Start Date, Due Date, Status.

> [!WARNING]
> When displaying rentals, always check `rental.getReturnDate() != null` before calling any method on `returnDate`. Active rentals will have a `null` return date.

---

### For Teammate B — Billing Module

**Your files:**
* `controller/BillingController.java`
* `view/BillPanel.java`

**What to implement:**

In `BillingController.java`, fill in the `// TODO` methods:
```java
// Generate a bill after a return is processed:
facade.generateBill(rentalId);

// Find an existing bill:
facade.findBillByRental(rentalId);

// Get billing history for a user:
facade.getBillHistory(userId);
```

In `BillPanel.java`, build:
1. A **bill display area** — use a `JTextArea` to render a formatted receipt showing:
   - Bill ID, Rental ID, Date
   - Base Rental Fee
   - User Discount (`-$xx.xx`)
   - Penalties (`+$xx.xx`)
   - Net Payable
2. A **bill history table** — JTable listing all bills for the logged-in user.
3. A **Print/Generate** button to trigger `controller.generateBill(rentalId)`.

> [!TIP]
> Formatting the bill receipt is your responsibility in the View. Use `bill.getBaseRentalFee()`, `bill.getDiscountAmount()`, `bill.getPenaltyAmount()`, and `bill.getNetPayable()` to get the values.

---

### For Teammate C — User & Session Module

**Your files:**
* `controller/UserController.java`
* `view/LoginDialog.java`
* `view/UserPanel.java`

**What to implement:**

In `UserController.java`:
```java
// Authenticate a user:
// 1. Call facade.findUserById(userId) to retrieve the User object.
// 2. Call HashUtil.verify(rawPassword, user.getPassword()) to check credentials.
// 3. If valid, call SessionManager.getInstance().setCurrentUser(user) and return true.

// Register a new user:
// Use IDGenerator.generateUserId() for userId.
// Use IDGenerator.generateStudentId() or generateStaffId() for their card ID.
// Hash the password: HashUtil.sha256(rawPassword) before constructing the User.
```

In `LoginDialog.java`, build:
1. A **User ID field** and **Password field** (`JPasswordField`).
2. A **Login button** that calls `controller.login(userId, password)`.
3. If login succeeds — call `dispose()` to close the dialog.
4. If login fails — show `JOptionPane.showMessageDialog(...)` with an error.
5. A **Cancel/Exit button** that calls `System.exit(0)` after a confirmation dialog.

In `UserPanel.java` (Admin only), build:
1. A **JTable** showing all registered users (User ID, Name, Email, Role).
2. A **registration form** with fields for name, email, password, user type (Student/Staff), and year of study or department.
3. An **Edit** button to update a user's name and email.
4. A **Delete** button to remove a user.

> [!WARNING]
> The `LoginDialog` must handle the case where the user closes the dialog window without clicking Login. Currently, `MainFrame` will keep re-showing the dialog in a loop. Wire the window close button to call `System.exit(0)` with a confirmation to let the user truly exit the app.

---

## MVC Boundary Summary

```
[View Panel]  →  calls  →  [Controller]  →  calls  →  [RentalSystemFacade]
                                                               ↓
                                              [EquipmentManager / RentalManager /
                                               BillingManager / UserManager]
                                                               ↓
                                                        [DataStore (JSON)]
```

**NEVER skip a layer. Views call Controllers only. Controllers call Facade only.**

---

## Quick Build & Run Guide

1. Open the project in **IntelliJ IDEA**.
2. Let Maven auto-download the Gson dependency (check the Maven tool window).
3. Right-click `Main.java` → **Run 'Main'**.
4. The `data/` folder will be auto-created with a seeded admin account.
5. Log in with User ID: `USR-001` and Password: `admin123`.
