# 📦 Inventory Management System
**Java Swing Desktop Application**

---

## 📁 Project Structure

```
InventoryApp/
├── src/
│   ├── Main.java                    ← Entry Point
│   ├── model/
│   │   └── Product.java             ← Product entity
│   ├── dao/
│   │   └── ProductDAO.java          ← File-based CRUD (CSV)
│   └── ui/
│       ├── MainFrame.java           ← Main JFrame window
│       ├── DashboardPanel.java      ← Summary & alerts
│       ├── ProductPanel.java        ← Add/Edit/Delete products
│       └── StockPanel.java          ← Restock & sell items
│
├── inventory.csv                    ← Auto-created data file
└── README.md
```

---

## ✅ Features

- **Dashboard** — Total products, total stock, total value, low-stock count
- **Product Management** — Add, Edit, Delete, Search products
- **Stock Management** — Restock (+) or Sell (-) stock with live updates
- **Low Stock Alerts** — Rows highlighted in red when stock ≤ threshold
- **Data Persistence** — All data saved to `inventory.csv` automatically

---

## 🛠️ Requirements

- Java JDK 8 or higher
- No external libraries needed (pure Java Swing)

---

## 🚀 How to Run

### Option 1: Command Line

**Step 1: Compile**
```bash
cd InventoryApp
mkdir out
javac -d out src/Main.java src/model/Product.java src/dao/ProductDAO.java src/ui/DashboardPanel.java src/ui/ProductPanel.java src/ui/StockPanel.java src/ui/MainFrame.java
```

**Step 2: Run**
```bash
java -cp out Main
```

---

### Option 2: IntelliJ IDEA (Recommended)

1. Open IntelliJ IDEA → **File → Open** → Select the `InventoryApp` folder
2. Right-click `src` folder → **Mark Directory As → Sources Root**
3. Open `src/Main.java`
4. Click the ▶ **Run** button (or press `Shift+F10`)

---

### Option 3: Eclipse

1. **File → New → Java Project** → Name it `InventoryApp`
2. Copy all files from `src/` into the project's `src/` folder
3. Right-click `Main.java` → **Run As → Java Application**

---

### Option 4: NetBeans

1. **File → New Project → Java Application**
2. Uncheck "Create Main Class"
3. Add all source files into the project's `src/` folder
4. Right-click project → **Run**

---

## 📸 Screens

| Tab | Description |
|-----|-------------|
| 🏠 Dashboard | Summary cards + low-stock alert table |
| 🗂 Products | Full CRUD + search + colored table |
| 📦 Stock Management | Restock and sell with live stock display |

---

## 💾 Data Storage

Data is stored in `inventory.csv` in the **working directory** (where you run the app from).

Format: `id,name,category,price,quantity,lowStockThreshold`

Example:
```
1,Rice Bag 10kg,Groceries,450.0,5,10
2,Laptop Stand,Electronics,1299.0,20,5
```

---

## 📝 Notes for Submission

- No internet or external libraries needed
- Compatible with JDK 8, 11, 17, 21
- Data persists between sessions via CSV
- Low-stock threshold is configurable per product
