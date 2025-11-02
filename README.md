# Bookstore-WebApp
A group project for SYSC4806.

Project Description:
Bookstore Owner can upload and edit Book information (ISBN, picture, description, author, publisher,...) and inventory. User can search for, and browse through, the books in the bookstore, sort/filter them based on the above information. User can then decide to purchase one or many books by putting them in the Shopping Cart and proceeding to Checkout. The purchase itself will obviously be simulated, but purchases cannot exceed the inventory. User can also view Book Recommendations based on past purchases. This is done by looking for users whose purchases are most similar (using Jaccard distance: Google it!), and then recommending books purchased by those similar users but that the current User hasn't yet purchased.

The projects currently allows users to:
- View all books currently stored in the database.
- Add new books (title, genre, price, ISBN).
- Remove books.

Features Implemented:
- Thymeleaf front-end template
- Single inventory model
- Spring Data JPA with automatic persistence
- H2 Database (file-based, not in-memory) — data persists between runs
- Automatically updates the table after each add/remove via full page reload
- Project deployment on Azure
