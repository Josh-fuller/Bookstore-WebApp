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

Features Implemented:
- Thymeleaf front-end template
- Single inventory model
- Spring Data JPA with automatic persistence
- H2 Database (file-based, not in-memory) — data persists between runs
- Automatically updates the table after each add/remove via full page reload
- Project deployment on Azure

Milestone 3 feature plans:
- Show book reccomendations based on Jaccard distances
- Ability to view purchase history and get reccomendations based on past purchases
- Seperate pages for book details
- Actual per-book inventories
- Comparing user-purchases

UML Diagram:
<img width="1357" height="886" alt="image" src="https://github.com/user-attachments/assets/861aeee5-fea8-4927-bedd-6dfc5eff63ed" />


ORM Diagram:
<img width="771" height="466" alt="image" src="https://github.com/user-attachments/assets/b3443a31-97bd-47e7-87db-0a6f6ef6d4e4" />
