# Gunadarma Bank - Java Swing Desktop Banking

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

## Arsitektur & Alur Sistem (System Architecture & Flow)

Bagian ini menjelaskan arsitektur dan alur kerja aplikasi Gunadarma Bank secara visual menggunakan diagram Mermaid. Seluruh diagram disusun berdasarkan analisis langsung terhadap kode sumber (`src/mbanking`) dan skema database (`database/gunadarma_bank.sql`), sehingga mencerminkan logika aplikasi yang sesungguhnya.

### 1. Diagram Use Case (Use Case Diagram)

Diagram ini memetakan seluruh aksi yang dapat dilakukan Nasabah, berdasarkan menu yang tersedia di layar autentikasi (`createAuthPanel`) dan sidebar navigasi (`createSidebar`) pada `GunadarmaBankGUI`.

```mermaid
flowchart LR
    Nasabah((Nasabah))

    subgraph AUTH["Sebelum Login (Layar Autentikasi)"]
        UC1([Login])
        UC2([Daftar Akun Baru])
        UC11([Keluar Aplikasi])
    end

    subgraph APP["Setelah Login (Menu Utama/Sidebar)"]
        UC3([Cek Saldo])
        UC4([Setor Tunai])
        UC5([Tarik Tunai])
        UC6([Transfer])
        UC7([Cek Penerima])
        UC8([Riwayat Transaksi])
        UC9([Ganti PIN])
        UC10([Logout])
    end

    Nasabah --> UC1
    Nasabah --> UC2
    Nasabah --> UC11
    Nasabah --> UC3
    Nasabah --> UC4
    Nasabah --> UC5
    Nasabah --> UC6
    Nasabah --> UC8
    Nasabah --> UC9
    Nasabah --> UC10
    UC6 -.->|"<<include>>"| UC7
```

Keterangan:

- `Login` dan `Daftar Akun Baru` merupakan dua tab pada layar autentikasi (`createLoginPanel`, `createRegisterPanel`).
- `Keluar Aplikasi` adalah tombol "Keluar" pada layar Login yang memanggil `dispose()`, berbeda dengan `Logout` pada sidebar yang memanggil `logout()` (kembali ke layar Login tanpa menutup aplikasi).
- `Cek Penerima` bersifat `<<include>>` terhadap `Transfer`, karena `showTransferView()` mewajibkan nasabah memvalidasi rekening tujuan (`bank.findUserByAccount`) sebelum tombol submit "Transfer" dapat berhasil diproses.

### 2. Diagram Aktivitas Alur Transfer (Activity Diagram)

Diagram ini menelusuri proses transaksi Transfer secara rinci, mulai dari input di `showTransferView()`, validasi bisnis di `Bank.transfer()`, hingga penyimpanan ke database melalui `BankRepository.saveTransfer()`.

```mermaid
stateDiagram-v2
    state cekRekening1 <<choice>>
    state cekRekening2 <<choice>>
    state pilihanKonfirmasi <<choice>>
    state validasiBisnis <<choice>>
    state hasilSimpan <<choice>>

    [*] --> FormTransfer : Nasabah membuka menu "Transfer"

    FormTransfer --> KlikCekPenerima : Isi "No. Rek Tujuan", klik "Cek Penerima"
    KlikCekPenerima --> cekRekening1 : findUserByAccount(noRekTujuan)
    cekRekening1 --> PesanRekeningTidakDitemukan : hasil null
    cekRekening1 --> TampilkanNamaPenerima : rekening ditemukan
    PesanRekeningTidakDitemukan --> FormTransfer

    TampilkanNamaPenerima --> IsiNominalPin : Isi "Nominal" dan "PIN"
    IsiNominalPin --> KlikTombolTransfer : Klik tombol "Transfer"

    KlikTombolTransfer --> cekRekening2 : Validasi ulang findUserByAccount()
    cekRekening2 --> PesanRekeningTidakDitemukan : rekening tidak ditemukan
    cekRekening2 --> DialogKonfirmasi : rekening ditemukan

    DialogKonfirmasi --> pilihanKonfirmasi : Nasabah memilih Yes/No pada JOptionPane
    pilihanKonfirmasi --> PesanDibatalkan : No
    PesanDibatalkan --> IsiNominalPin
    pilihanKonfirmasi --> ProsesBankTransfer : Yes

    ProsesBankTransfer --> validasiBisnis : Bank.transfer() mengecek PIN, nominal, rekening sendiri, dan saldo
    validasiBisnis --> PesanGagalValidasi : tidak valid (PIN salah / nominal tidak valid / rekening sendiri / saldo tidak cukup)
    PesanGagalValidasi --> IsiNominalPin
    validasiBisnis --> UpdateSaldoMemori : valid

    UpdateSaldoMemori --> SimpanKeDatabase : sender.withdraw(), receiver.deposit(), buat objek Transaction txOut & txIn
    SimpanKeDatabase --> hasilSimpan : BankRepository.saveTransfer() dijalankan dalam 1 transaksi JDBC
    hasilSimpan --> RollbackSaldo : SQLException, database di-rollback
    RollbackSaldo --> PesanErrorDatabase : saldo sender & receiver dikembalikan ke nilai semula
    PesanErrorDatabase --> [*]
    hasilSimpan --> CatatTransaksiSukses : commit berhasil
    CatatTransaksiSukses --> TampilkanPesanSukses : sender/receiver.addTransaction(), header saldo diperbarui
    TampilkanPesanSukses --> [*]
```

### 3. Diagram Kelas (Class Diagram)

Diagram ini merepresentasikan struktur kode utama aplikasi: lapisan antarmuka (`gui`), lapisan layanan (`service`), lapisan repository/DAO (`repository`, `dao`), dan model domain (`model`, `enums`). Nama kelas, atribut, dan method ditulis sesuai kode asli; penjelasan setiap kelas diberikan dalam catatan berbahasa Indonesia.

```mermaid
classDiagram
    class Main {
        +main(String[] args) void
    }

    class GunadarmaBankGUI {
        -Bank bank
        -User currentUser
        -CardLayout rootLayout
        -JPanel contentPanel
        -JButton activeNavButton
        +GunadarmaBankGUI(Bank bank)
        -showApplication() void
        -showBalanceView() void
        -showDepositView() void
        -showWithdrawView() void
        -showTransferView() void
        -showHistoryView() void
        -showPinView() void
        -selectNavigation(String text) void
        -runSafely(Runnable action) void
    }

    class Bank {
        -String bankName
        -BankRepository repository
        +Bank(String bankName)
        -seedDemoAccounts() void
        +registerUser(String, String, String, String, double) String
        +login(String, String) User
        +deposit(User, double, String) boolean
        +withdraw(User, double, String) String
        +transfer(User, String, double, String) String
        +changePin(User, String, String) String
        +findUserByAccount(String) User
        +getBankName() String
    }

    class BankRepository {
        -TransactionDao transactionDao
        -UserDao userDao
        -AccountDao accountDao
        +createUser(User) void
        +saveDeposit(User, Transaction) void
        +saveWithdrawal(User, Transaction) void
        +saveTransfer(User, User, Transaction, Transaction) void
        +savePinChange(User) void
        -executeInTransaction(SqlWork) void
    }

    class UserDao {
        -TransactionDao transactionDao
        +usernameExists(String) boolean
        +userIdExists(String) boolean
        +findByUsername(String) User
        +findByAccountNumber(String) User
        +insert(Connection, User) void
    }

    class AccountDao {
        +accountNumberExists(String) boolean
        +updateBalance(Connection, String, double) void
        +updatePin(Connection, String, String) void
    }

    class TransactionDao {
        +transactionIdExists(String) boolean
        +findByAccountNumber(Connection, String) List~Transaction~
        +insert(Connection, Transaction) void
    }

    class DatabaseConnection {
        +getConnection()$ Connection
    }

    class DatabaseException {
        +DatabaseException(String message, Throwable cause)
    }

    class User {
        -String userId
        -String username
        -String password
        -String fullName
        -Account account
        -List~Transaction~ transactions
        +validatePassword(String) boolean
        +addTransaction(Transaction) void
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
        -String description
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
        +getLabel() String
    }

    class JFrame {
        <<Java Swing>>
    }
    class RuntimeException {
        <<Java Standard Library>>
    }

    JFrame <|-- GunadarmaBankGUI
    RuntimeException <|-- DatabaseException

    Main ..> GunadarmaBankGUI : membuat instance
    Main ..> Bank : membuat instance
    GunadarmaBankGUI --> Bank : memanggil layanan
    GunadarmaBankGUI --> User : menyimpan sesi nasabah aktif
    GunadarmaBankGUI ..> DatabaseException : menangkap error (runSafely)
    Bank *-- BankRepository : membuat & memiliki
    Bank ..> User : memvalidasi & mengembalikan
    Bank ..> Transaction : membuat data transaksi
    Bank ..> DatabaseException : melempar saat SQLException
    BankRepository *-- UserDao : membuat & memiliki
    BankRepository *-- AccountDao : membuat & memiliki
    BankRepository *-- TransactionDao : membuat & memiliki
    UserDao --> TransactionDao : mengambil riwayat transaksi user
    UserDao ..> DatabaseConnection : membuka koneksi
    AccountDao ..> DatabaseConnection : membuka koneksi
    TransactionDao ..> DatabaseConnection : membuka koneksi
    UserDao ..> User : membentuk objek hasil query
    UserDao ..> Account : membentuk objek hasil query
    TransactionDao ..> Transaction : membentuk objek hasil query
    User *-- Account : 1 nasabah memiliki 1 rekening
    User o-- Transaction : memiliki 0..* riwayat transaksi
    Transaction --> TransactionType : bertipe

    note for GunadarmaBankGUI "Kelas antarmuka utama (Java Swing). Mengatur seluruh layar: Login, Cek Saldo, Setor Tunai, Tarik Tunai, Transfer, Riwayat Transaksi, dan Ganti PIN."
    note for Bank "Lapisan business logic/service. Tempat seluruh validasi transaksi dilakukan sebelum data disimpan ke database."
    note for BankRepository "Mengoordinasikan operasi database dan memastikan proses Transfer berjalan dalam satu transaksi JDBC (commit/rollback)."
    note for UserDao "Akses langsung ke tabel users dan accounts (JOIN) menggunakan JDBC PreparedStatement."
    note for AccountDao "Operasi UPDATE saldo dan PIN pada tabel accounts."
    note for TransactionDao "Operasi INSERT dan SELECT pada tabel transactions."
    note for DatabaseConnection "Membuka koneksi JDBC ke MySQL berdasarkan konfigurasi URL, user, dan password."
    note for User "Model domain nasabah; menyimpan data akun dan daftar riwayat transaksi di memori."
    note for Account "Model domain rekening: saldo dan PIN, beserta aturan validasi dasar."
    note for Transaction "Model satu baris riwayat transaksi (setor/tarik/transfer masuk/keluar)."
```

### 4. Diagram Sekuens Alur Transfer (Sequence Diagram)

Diagram ini menunjukkan komunikasi antar objek secara berurutan untuk proses Transfer, dari `GunadarmaBankGUI` hingga MySQL, termasuk jalur gagal (rekening tidak ditemukan, validasi gagal, dan error database).

```mermaid
sequenceDiagram
    actor Nasabah
    participant GUI as GunadarmaBankGUI
    participant Bank
    participant Repo as BankRepository
    participant UDao as UserDao
    participant ADao as AccountDao
    participant TDao as TransactionDao
    participant DB as MySQL

    Nasabah->>GUI: Isi "No. Rek Tujuan", klik "Cek Penerima"
    GUI->>Bank: findUserByAccount(noRekTujuan)
    Bank->>Repo: findByAccountNumber(noRekTujuan)
    Repo->>UDao: findByAccountNumber(noRekTujuan)
    UDao->>DB: SELECT users JOIN accounts WHERE account_number = ?
    DB-->>UDao: baris data penerima
    UDao->>TDao: findByAccountNumber(conn, noRekTujuan)
    TDao->>DB: SELECT * FROM transactions WHERE account_number = ?
    DB-->>TDao: riwayat transaksi penerima
    TDao-->>UDao: List~Transaction~
    UDao-->>Repo: objek User (penerima)
    Repo-->>Bank: objek User (penerima)
    Bank-->>GUI: objek User (penerima) atau null

    alt Rekening tidak ditemukan
        GUI->>Nasabah: Tampilkan pesan "Nomor rekening tidak ditemukan"
    else Rekening ditemukan
        GUI->>Nasabah: Tampilkan "Penerima: nama lengkap"
        Nasabah->>GUI: Isi "Nominal" dan "PIN", klik "Transfer"
        GUI->>Bank: findUserByAccount(noRekTujuan) - validasi ulang sebelum submit
        Bank->>Repo: findByAccountNumber(noRekTujuan)
        Repo->>UDao: findByAccountNumber(noRekTujuan)
        UDao->>DB: SELECT users JOIN accounts WHERE account_number = ?
        DB-->>UDao: baris data penerima
        UDao-->>Repo: objek User (penerima)
        Repo-->>Bank: objek User (penerima)
        Bank-->>GUI: objek User (penerima)

        GUI->>Nasabah: Tampilkan dialog konfirmasi transfer (JOptionPane)
        Nasabah->>GUI: Konfirmasi "Yes"
        GUI->>Bank: transfer(currentUser, noRekTujuan, nominal, pin)

        Bank->>Bank: validatePin(), cek nominal > 0, cek bukan rekening sendiri, cek saldo cukup

        alt Validasi gagal
            Bank-->>GUI: pesan gagal ("PIN salah" / "Saldo tidak mencukupi" / dst)
            GUI->>Nasabah: Tampilkan pesan gagal
        else Validasi berhasil
            Bank->>Bank: sender.withdraw(nominal), receiver.deposit(nominal)
            Bank->>Bank: buat objek Transaction txOut (TRANSFER_OUT) & txIn (TRANSFER_IN)
            Bank->>Repo: saveTransfer(sender, receiver, txOut, txIn)
            Repo->>DB: setAutoCommit(false)
            Repo->>ADao: updateBalance(conn, noRekSender, saldoBaruSender)
            ADao->>DB: UPDATE accounts SET balance = ? WHERE account_number = ?
            Repo->>ADao: updateBalance(conn, noRekTujuan, saldoBaruPenerima)
            ADao->>DB: UPDATE accounts SET balance = ? WHERE account_number = ?
            Repo->>TDao: insert(conn, txOut)
            TDao->>DB: INSERT INTO transactions (... TRANSFER_OUT ...)
            Repo->>TDao: insert(conn, txIn)
            TDao->>DB: INSERT INTO transactions (... TRANSFER_IN ...)

            alt Semua query berhasil
                Repo->>DB: commit()
                Repo-->>Bank: berhasil
                Bank->>Bank: sender.addTransaction(txOut), receiver.addTransaction(txIn)
                Bank-->>GUI: "OK:" + namaPenerima
                GUI->>Nasabah: Tampilkan pesan sukses & saldo terbaru
            else SQLException
                Repo->>DB: rollback()
                Repo-->>Bank: melempar SQLException
                Bank->>Bank: kembalikan saldo sender & receiver ke nilai semula
                Bank-->>GUI: melempar DatabaseException
                GUI->>Nasabah: Tampilkan dialog error database
            end
        end
    end
```

### 5. Diagram Relasi Entitas / ERD (Entity Relationship Diagram)

Diagram ini memetakan skema database persis seperti yang didefinisikan pada `database/gunadarma_bank.sql`, termasuk primary key, foreign key, dan batasan (constraint) tiap tabel.

```mermaid
erDiagram
    users {
        varchar_12 user_id PK "ID unik nasabah, contoh USR0001"
        varchar_50 username UK "Username untuk login (harus unik)"
        varchar_100 password "Password login (disimpan plain text pada kode saat ini, tanpa hashing)"
        varchar_100 full_name "Nama lengkap nasabah"
        timestamp created_at "Waktu akun dibuat (default CURRENT_TIMESTAMP)"
    }

    accounts {
        varchar_20 account_number PK "Nomor rekening, contoh 10000001"
        varchar_12 user_id FK "Relasi ke users.user_id, UNIQUE (1 nasabah = 1 rekening)"
        decimal_15_2 balance "Saldo aktif, CHECK balance >= 0"
        char_6 pin "PIN transaksi 6 digit, CHECK harus 6 digit angka, disimpan plain text"
        timestamp created_at "Waktu rekening dibuat"
    }

    transactions {
        varchar_20 transaction_id PK "ID transaksi, contoh TXN00000001"
        varchar_20 account_number FK "Pemilik baris riwayat (akun yang mencatat transaksi ini), wajib diisi"
        enum transaction_type "DEPOSIT / WITHDRAW / TRANSFER_OUT / TRANSFER_IN"
        decimal_15_2 amount "Nominal transaksi, CHECK amount > 0"
        varchar_20 from_account_number FK "Rekening asal dana; NULL jika bukan transaksi transfer"
        varchar_20 to_account_number FK "Rekening tujuan dana; NULL jika bukan transaksi transfer"
        varchar_255 description "Keterangan transaksi, contoh 'Transfer ke Siti Rahayu'"
        datetime created_at "Waktu transaksi terjadi (default CURRENT_TIMESTAMP)"
    }

    users ||--|| accounts : "1 nasabah memiliki 1 rekening (uk_accounts_user)"
    accounts ||--o{ transactions : "pemilik riwayat, wajib (account_number)"
    accounts |o--o{ transactions : "rekening asal transfer, opsional (from_account_number)"
    accounts |o--o{ transactions : "rekening tujuan transfer, opsional (to_account_number)"
```

Catatan tambahan sesuai skema SQL:

- Semua tabel menggunakan engine `InnoDB` agar mendukung foreign key dan transaksi (COMMIT/ROLLBACK).
- Relasi `users` ke `accounts` bersifat satu-ke-satu karena kolom `accounts.user_id` diberi batasan `UNIQUE` (`uk_accounts_user`), bukan sekadar foreign key biasa.
- Kolom `from_account_number` dan `to_account_number` pada `transactions` bersifat nullable karena hanya diisi untuk transaksi bertipe `TRANSFER_OUT`/`TRANSFER_IN`; untuk `DEPOSIT`/`WITHDRAW`, kedua kolom ini bernilai NULL (lihat `TransactionDao.normalizeAccountNumber`).

### 6. Diagram Alir Data Level 0 / Context Diagram (Data Flow Diagram)

Diagram ini menunjukkan aliran data tingkat tertinggi antara Nasabah (entitas eksternal), Sistem Aplikasi Gunadarma Bank (satu proses tunggal), dan Basis Data MySQL (penyimpanan data), tanpa merinci proses internal (sesuai definisi DFD Level 0/Context Diagram).

```mermaid
flowchart TD
    Nasabah[Nasabah]
    Sistem((Sistem Aplikasi Gunadarma Bank))
    DB[(Basis Data MySQL - gunadarma_bank)]

    Nasabah -->|"Data login/registrasi, perintah Setor Tunai/Tarik Tunai/Transfer, nominal & PIN, permintaan Cek Saldo/Riwayat Transaksi/Ganti PIN"| Sistem
    Sistem -->|"Status login/registrasi, saldo aktif, konfirmasi & pesan error transaksi, daftar riwayat transaksi"| Nasabah
    Sistem -->|"Query SELECT (login, cek rekening, riwayat), INSERT (user/akun/transaksi baru), UPDATE (saldo, PIN)"| DB
    DB -->|"Hasil query: data user, akun, dan riwayat transaksi"| Sistem
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
