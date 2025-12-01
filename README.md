# Bookstore-WebApp
A group project for SYSC4806.

Project Description:
Bookstore Owner can upload and edit Book information (ISBN, picture, description, author, publisher,...) and inventory. User can search for, and browse through, the books in the bookstore, sort/filter them based on the above information. User can then decide to purchase one or many books by putting them in the Shopping Cart and proceeding to Checkout. The purchase itself will obviously be simulated, but purchases cannot exceed the inventory. User can also view Book Recommendations based on past purchases. This is done by looking for users whose purchases are most similar (using Jaccard distance: Google it!), and then recommending books purchased by those similar users but that the current User hasn't yet purchased.

The current implementation allows users to:
- View all books currently stored in the database.
- Add new books (title, genre, price, ISBN).
- Remove books.
- Login as an administrator or customer
- As a customer you can add/remove books from your cart and view your cart.
- Filter search for books by field
- As an an administrator you can edit the stock of books.
- You cannot add a book with no stock, and even if you could you can't checkout a book with empty stock.
- View purchase history. 

Features Implemented:
- Thymeleaf front-end template
- Single inventory model
- Spring Data JPA with automatic persistence
- H2 Database (file-based, not in-memory) — data persists between runs
- Automatically updates the table after each add/remove via full page reload
- Project deployment on Azure
- Show book reccomendations based on Jaccard distances
- Ability to view purchase history and get reccomendations based on past purchases
- Actual per-book inventories
- Live filtering
  

UML Diagram:
<img width="1142" height="816" alt="image" src="https://github.com/user-attachments/assets/88513386-c9fb-4714-99da-975c81f5e189" />




ORM Diagram:
<img width="596" height="732" alt="image" src="https://github.com/user-attachments/assets/01dd4b5c-c8cb-45d1-8fba-3076b1a1bbdf" />

