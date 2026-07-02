# Gunadarma Bank - Java Swing Mobile Banking

## Project Overview

Gunadarma Bank is a Java desktop banking application built with Swing and backed by a MySQL database. Customers can register, log in, view account details, deposit funds, withdraw funds, transfer to another account, review recent transactions, and change their PIN.

The application now runs as a GUI-only desktop application through `mbanking.Main`. The banking service, validation rules, transaction sequence, account model, and database schema are preserved.

## Features

- Customer login with username and password.
- New account registration with 6-digit PIN validation.
- Dashboard with customer name, account number, and active balance.
- Balance inquiry.
- Cash deposit with PIN validation.
- Cash withdrawal with PIN and balance validation.
- Transfer with receiver lookup, confirmation dialog, PIN validation, and sender/receiver transaction records.
- Recent transaction history, newest first, limited to the latest 10 transactions.
- PIN change with old PIN validation and new PIN confirmation.
- MySQL persistence for users, accounts, balances, PIN changes, and transaction history.
- Demo users seeded by the database script and guarded by the service initializer.
- Gunadarma Bank logo applied in the authentication and dashboard shell.

## Technologies Used

- Java 17 or newer recommended.
- Java Swing and AWT for the desktop user interface.
- JDBC for database access.
- MySQL 8 or newer.
- MySQL Connector/J at runtime.
- Mermaid for UML documentation in this README.

## Folder Structure

```text
mbanking/
|-- assets/
|   `-- logo.png
|-- database/
|   `-- gunadarma_bank.sql
|-- src/
|   `-- mbanking/
|       |-- Main.java
|       |-- config/
|       |   `-- DatabaseConnection.java
|       |-- dao/
|       |   |-- AccountDao.java
|       |   |-- DatabaseException.java
|       |   |-- TransactionDao.java
|       |   `-- UserDao.java
|       |-- enums/
|       |   `-- TransactionType.java
|       |-- gui/
|       |   `-- GunadarmaBankGUI.java
|       |-- model/
|       |   |-- Account.java
|       |   |-- Transaction.java
|       |   `-- User.java
|       |-- repository/
|       |   `-- BankRepository.java
|       |-- service/
|       |   `-- Bank.java
|       `-- util/
|           |-- Formatter.java
|           `-- IDGenerator.java
`-- README.md
```

Important directories:

- `assets`: visual assets used by the GUI.
- `database`: MySQL schema and sample data.
- `src/mbanking/gui`: Swing screens, layout, event handlers, and GUI styling.
- `src/mbanking/service`: banking business facade.
- `src/mbanking/repository`: transaction-safe persistence coordination.
- `src/mbanking/dao`: direct JDBC operations.
- `src/mbanking/model`: domain objects.

## Installation

1. Install MySQL Server 8 or newer.
2. Create the database and sample data:

```powershell
mysql -u root -p < database\gunadarma_bank.sql
```

3. Place MySQL Connector/J in a local `lib` folder, for example:

```text
lib/mysql-connector-j-9.3.0.jar
```

4. Compile the Java source:

```powershell
$files = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $files
```

5. Run the GUI application:

```powershell
java -cp "out;lib\mysql-connector-j-9.3.0.jar" mbanking.Main
```

Run with explicit database credentials:

```powershell
java -Ddb.user="root" -Ddb.password="your_password" -cp "out;lib\mysql-connector-j-9.3.0.jar" mbanking.Main
```

Database configuration priority:

1. JVM properties: `db.url`, `db.user`, `db.password`
2. Environment variables: `GUNADARMA_DB_URL`, `GUNADARMA_DB_USER`, `GUNADARMA_DB_PASSWORD`
3. Environment variables: `DB_URL`, `DB_USER`, `DB_PASSWORD`
4. Defaults in `DatabaseConnection`

Default JDBC URL:

```text
jdbc:mysql://localhost:3306/gunadarma_bank?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true
```

## Sample Credentials

| Name | Username | Password | PIN | Account Number | Initial Balance |
|---|---|---|---|---|---|
| Budi Santoso | `budi` | `budi123` | `123456` | `10000001` | Rp5,000,000 |
| Siti Rahayu | `siti` | `siti123` | `654321` | `10000002` | Rp3,000,000 |

## Screenshots

Screenshots are not included in this repository yet.

| Screen | Placeholder |
|---|---|
| Login and registration | `docs/screenshots/login.png` |
| Dashboard and balance | `docs/screenshots/dashboard.png` |
| Transfer | `docs/screenshots/transfer.png` |
| Transaction history | `docs/screenshots/history.png` |

## UML Documentation

### 1. Use Case Diagram

```mermaid
flowchart LR
    Customer((Customer))

    Customer --> Login[Login]
    Customer --> Register[Register Account]
    Customer --> CheckBalance[Check Balance]
    Customer --> Deposit[Deposit Cash]
    Customer --> Withdraw[Withdraw Cash]
    Customer --> Transfer[Transfer Funds]
    Customer --> ViewHistory[View Transaction History]
    Customer --> ChangePin[Change PIN]
    Customer --> Logout[Logout]

    Transfer --> ReceiverLookup[Check Receiver Account]
    Transfer --> ConfirmTransfer[Confirm Transfer]
```

### 2. Class Diagram

```mermaid
classDiagram
    class Main {
        +main(String[] args)
    }

    class GunadarmaBankGUI {
        -Bank bank
        -User currentUser
        -CardLayout rootLayout
        -JPanel contentPanel
        -showApplication()
        -showBalanceView()
        -showDepositView()
        -showWithdrawView()
        -showTransferView()
        -showHistoryView()
        -showPinView()
    }

    class Bank {
        -String bankName
        -BankRepository repository
        +registerUser(String, String, String, String, double) String
        +login(String, String) User
        +deposit(User, double, String) boolean
        +withdraw(User, double, String) String
        +transfer(User, String, double, String) String
        +changePin(User, String, String) String
        +findUserByAccount(String) User
    }

    class BankRepository {
        -TransactionDao transactionDao
        -UserDao userDao
        -AccountDao accountDao
        +createUser(User)
        +saveDeposit(User, Transaction)
        +saveWithdrawal(User, Transaction)
        +saveTransfer(User, User, Transaction, Transaction)
        +savePinChange(User)
    }

    class UserDao {
        +usernameExists(String) boolean
        +userIdExists(String) boolean
        +findByUsername(String) User
        +findByAccountNumber(String) User
        +insert(Connection, User)
    }

    class AccountDao {
        +accountNumberExists(String) boolean
        +updateBalance(Connection, String, double)
        +updatePin(Connection, String, String)
    }

    class TransactionDao {
        +transactionIdExists(String) boolean
        +findByAccountNumber(Connection, String) List~Transaction~
        +insert(Connection, Transaction)
    }

    class DatabaseConnection {
        +getConnection() Connection
    }

    class User {
        -String userId
        -String username
        -String password
        -String fullName
        -Account account
        -List~Transaction~ transactions
        +validatePassword(String) boolean
        +addTransaction(Transaction)
    }

    class Account {
        -String accountNumber
        -double balance
        -String pin
        +deposit(double) boolean
        +withdraw(double) boolean
        +validatePin(String) boolean
    }

    class Transaction {
        -String transactionId
        -TransactionType type
        -double amount
        -LocalDateTime timestamp
        -String fromAccount
        -String toAccount
        +getFormattedTimestamp() String
    }

    class TransactionType {
        <<enumeration>>
        DEPOSIT
        WITHDRAW
        TRANSFER_OUT
        TRANSFER_IN
    }

    Main --> GunadarmaBankGUI
    GunadarmaBankGUI --> Bank
    GunadarmaBankGUI --> User
    GunadarmaBankGUI --> Transaction
    Bank --> BankRepository
    Bank --> User
    Bank --> Transaction
    BankRepository --> UserDao
    BankRepository --> AccountDao
    BankRepository --> TransactionDao
    UserDao --> DatabaseConnection
    AccountDao --> DatabaseConnection
    TransactionDao --> DatabaseConnection
    User *-- Account
    User o-- Transaction
    Transaction --> TransactionType
```

### 3. Activity Diagram

```mermaid
flowchart TD
    Start([Start Application]) --> LoadBank[Create Bank Service]
    LoadBank --> ShowAuth[Show Login/Register Screen]
    ShowAuth --> Choice{Customer action}

    Choice -->|Register| FillRegistration[Enter name, username, password, PIN]
    FillRegistration --> ValidatePin{PIN is 6 digits?}
    ValidatePin -->|No| RegistrationError[Show validation message]
    ValidatePin -->|Yes| SaveUser[Bank.registerUser]
    SaveUser --> RegistrationResult{Username available?}
    RegistrationResult -->|No| RegistrationError
    RegistrationResult -->|Yes| RegistrationSuccess[Show account number]
    RegistrationError --> ShowAuth
    RegistrationSuccess --> ShowAuth

    Choice -->|Login| FillLogin[Enter username and password]
    FillLogin --> Authenticate[Bank.login]
    Authenticate --> LoginResult{Valid credentials?}
    LoginResult -->|No| LoginError[Show login error]
    LoginError --> ShowAuth
    LoginResult -->|Yes| Dashboard[Show dashboard]

    Dashboard --> DashboardChoice{Select menu}
    DashboardChoice --> Balance[Show balance]
    DashboardChoice --> Deposit[Submit deposit amount and PIN]
    DashboardChoice --> Withdraw[Submit withdrawal amount and PIN]
    DashboardChoice --> Transfer[Submit receiver, amount, PIN, confirmation]
    DashboardChoice --> History[Show latest 10 transactions]
    DashboardChoice --> PinChange[Submit old and new PIN]
    DashboardChoice --> Logout[Logout]

    Deposit --> PersistDeposit[Update balance and transaction]
    Withdraw --> PersistWithdraw[Update balance and transaction]
    Transfer --> PersistTransfer[Update both balances and both transaction rows]
    PinChange --> PersistPin[Update account PIN]

    PersistDeposit --> Dashboard
    PersistWithdraw --> Dashboard
    PersistTransfer --> Dashboard
    PersistPin --> Dashboard
    Balance --> Dashboard
    History --> Dashboard
    Logout --> ShowAuth
```

### 4. Sequence Diagram

```mermaid
sequenceDiagram
    actor Customer
    participant GUI as GunadarmaBankGUI
    participant Bank
    participant Repo as BankRepository
    participant UserDao
    participant AccountDao
    participant TransactionDao
    participant DB as MySQL

    Customer->>GUI: Enter transfer target, amount, PIN
    GUI->>Bank: findUserByAccount(targetAccount)
    Bank->>Repo: findByAccountNumber(targetAccount)
    Repo->>UserDao: findByAccountNumber(targetAccount)
    UserDao->>DB: SELECT user, account, transactions
    DB-->>UserDao: Receiver data
    UserDao-->>Repo: Receiver User
    Repo-->>Bank: Receiver User
    Bank-->>GUI: Receiver User
    GUI->>Customer: Show confirmation dialog
    Customer->>GUI: Confirm transfer
    GUI->>Bank: transfer(currentUser, targetAccount, amount, pin)
    Bank->>Bank: Validate PIN, amount, self-transfer, receiver, balance
    Bank->>Repo: saveTransfer(sender, receiver, txOut, txIn)
    Repo->>DB: Begin transaction
    Repo->>AccountDao: updateBalance(sender)
    AccountDao->>DB: UPDATE accounts
    Repo->>AccountDao: updateBalance(receiver)
    AccountDao->>DB: UPDATE accounts
    Repo->>TransactionDao: insert(txOut)
    TransactionDao->>DB: INSERT transactions
    Repo->>TransactionDao: insert(txIn)
    TransactionDao->>DB: INSERT transactions
    Repo->>DB: Commit transaction
    Repo-->>Bank: Saved
    Bank-->>GUI: OK:receiverName
    GUI->>Customer: Show success message and refreshed balance
```

### 5. Package Diagram

```mermaid
flowchart TB
    MainPkg[mbanking.Main] --> GuiPkg[mbanking.gui]
    GuiPkg --> ServicePkg[mbanking.service]
    GuiPkg --> ModelPkg[mbanking.model]
    GuiPkg --> EnumPkg[mbanking.enums]
    GuiPkg --> UtilPkg[mbanking.util]
    ServicePkg --> RepositoryPkg[mbanking.repository]
    ServicePkg --> ModelPkg
    ServicePkg --> EnumPkg
    ServicePkg --> UtilPkg
    RepositoryPkg --> DaoPkg[mbanking.dao]
    RepositoryPkg --> ConfigPkg[mbanking.config]
    DaoPkg --> ConfigPkg
    DaoPkg --> ModelPkg
    DaoPkg --> EnumPkg
```

## Database Notes

The database script creates:

- `users`: user identity and login credentials.
- `accounts`: account number, balance, PIN, and user ownership.
- `transactions`: deposit, withdrawal, transfer-in, and transfer-out history.

Transfer persistence creates two transaction rows in one JDBC transaction:

- `TRANSFER_OUT` owned by the sender account.
- `TRANSFER_IN` owned by the receiver account.

## Troubleshooting

`MySQL JDBC Driver tidak ditemukan`

- Confirm MySQL Connector/J is present in `lib`.
- Confirm the jar filename in the `-cp` command matches the actual file.

`Unknown database gunadarma_bank`

- Run `database/gunadarma_bank.sql` before launching the app.

`Access denied for user`

- Check `db.user`, `db.password`, or the matching environment variables.

`Communications link failure`

- Start MySQL Server and confirm the URL host and port.
