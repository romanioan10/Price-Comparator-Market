
# 🛒 Price Comparator - Market

This project implements the **backend** for a "Price Comparator - Market" application that helps users compare grocery and household product prices across different supermarket chains (e.g., Lidl, Kaufland, Profi). The backend was the core requirement of the project.

Additionally, a **React-based frontend** was developed voluntarily to enhance usability and demonstrate the backend functionality in action.

---

## ⚙️ Technologies Used

### Backend:
- Java 17
- Spring Boot (REST APIs)
- Gradle (build tool)
- CSV file loaders (data input source)

### Frontend (optional, self-developed):
- React.js with Vite
- JavaScript (ES6+)

---

## ✨ Features

The backend provides the following core functionalities:

- **View All Products**  
  Retrieve the full list of available products along with their details: name, brand, store, price, quantity, and category.

- **Basket Optimization**  
  Given a user's desired shopping basket, this feature calculates the most cost-efficient combination of products across multiple stores.

- **Top Active Discounts**  
  Returns the most valuable ongoing discounts based on current price reductions and relevance.

- **New Discounts**  
  Identifies recently introduced discounts that might be of interest to the user.

- **Filtered Price History**  
  Allows filtering historical product prices by category, brand, or store. Useful for analyzing price trends over time.

- **Best Value Products**  
  Computes which products offer the best value per unit (e.g., price per liter or per kilogram) across all entries.

- **Triggered Alerts**  
  Returns a list of products that meet alert criteria — for example, price falling below a specific threshold.


## ✅ Service Layer Testing

The backend services have been tested to validate core business logic. Specifically:

- `optimizeBasket(...)`: tested with different product combinations and price inputs
- `getTopActiveDiscounts(...)`: verified against mock data with time-based filtering
- `getNewDiscounts(...)`: ensured correct recognition of newly introduced discounts
- `getFilteredPriceHistory(...)`: validated price history filtering by category, store, and brand
- `getBestValueProducts(...)`: confirmed correct identification of best value items
- `getTriggeredAlerts(...)`: tested alert conditions and threshold logic

These tests confirmed that the service layer behaves as expected and supports the frontend with consistent and accurate responses.


## 🚀 How to Run the Project

### 1. Clone the repository
```bash
git clone https://github.com/romanioan10/Price-Comparator-Market.git -b development
cd Price-Comparator-Market
```

or open the web page of the github project and clone the repository using the green button "Code" and then "Open with GitHub Desktop" or use version control software of your choice.

### 2. Start the Backend
Make sure you are in the project root directory, then run:
```bash
./gradlew :backend:tasks:application:run
```
This will start the backend of this application and an UI built using buttons will appear.
---

!!! IMPORTANT !!!

Some functionalities, for example getNewDiscounts method checks if newly discounts were added in the last 24h.
For this to work, you will need to add some products by yourself.
I would do it myself but it is hard for me to predict when you will run the application.
Make sure to first add the products in products folder, then add the discounts in discounts folder.
Make sure not to set the same id as another product.
You can also just change the date of the discount to be in the last 24h.
Another example would be activeDiscounts.
This method checks if the discount is still active or not.
If all my products are expired and you want to test if the method works you should add some new discounts with the date being valid. 
By valid it means that the date should be in the future.
eg: i am writing this on 19.05.2025.
If you are running this in june you should add a discount ending in the future of your date.
for example if you are running this in 20.06.2025 you should add a discount that ends in 21.06.2025.
---

### 3. (Optional) Start the Frontend

Make sure you are in the project root directory, then run:
```bash
./gradlew :backend:RestServices:tasks:applicationbootRun
```
This will start the Spring Boot backend and expose the REST APIs.

---

If you'd like to use the custom-built UI:
```bash
cd frontend
npm install
npm install recharts #install recharts library
npm run dev
```

#### 🔧 If the port is already in use (e.g., 5173), run:
```bash
lsof -i :5173           # or the port shown in the error
kill -9 [PID]           # replace [PID] with the actual process ID
npm run dev             # restart the frontend
```

---

## 🧪 Testing the Features

Once the frontend is running in your browser (the link will appear in the terminal after `npm run dev`):

Here is an interesting error that i encountered while testing the application:
If you Click **Show Products** → then click **Execute**
Then click **Optimize Basket**
    - If an error appears: **refresh the page**
    - Then retry **Optimize Basket** → it should now work correctly
Apparently a refresh solves the problem.
---

## 🗂 Project Structure

```
Price-Comparator-Market/
├── backend/
│   ├── Model/           # Data models (e.g., Product, Discount)
│   ├── Services/        # Business logic
│   ├── Persistence/     # Loaders for CSV files
│   ├── RestServices/    # REST controllers
│   └── build.gradle     # Gradle configuration
├── frontend/            # Optional UI
│   ├── src/             # React source code
│   ├── public/          # Static files
│   └── package.json     # Dependencies and scripts
```

---

## 👤 Author

- Roman Raul-Ioan – [romanivancic@yahoo.com](mailto:romanivancic@yahoo.com)
- GitHub: [romanioan10](https://github.com/romanioan10)
- LinkedIn: [Roman Raul-Ioan](https://www.linkedin.com/in/raul-ioan-roman/)

---

## 📌 Note

The backend was the core requirement of this assignment. The frontend was created as a personal addition to demonstrate and enhance the user experience of the API functionality.
