package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Register a new user
     *
     * @param username
     * @param password
     * @param email
     * @param role
     * @return Uer if user is successful and null if username or email already exists
     */
    public User registerUser(String username, String password, String email, String role) {
        if (userRepository.existsByUsername(username)) {
            return null;
        }
        if (userRepository.existsByEmail(email)) {
            return null;
        }
        String hashedPassword = hashPassword(password);
        User user = new User(username, hashedPassword, email, role);
        return userRepository.save(user);
    }

    /**
     * Authenticate user login
     *
     * @param username
     * @param password
     * @return User if credentials are valid nad null if not valid
     */
    public User authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String hashedPassword = hashPassword(password);

            if (user.getPassword().equals(hashedPassword)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Hash password using SHA-256, took from (https://medium.com/@AlexanderObregon/what-is-sha-256-hashing-in-java-0d46dfb83888)
     *
     * @param password
     * @return hashed password as hex string
     */
    private String hashPassword(String password) {
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Perform the hash computation
            byte[] encodedhash = digest.digest(password.getBytes());

            // Convert byte array into a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find user by username
     *
     * @param username
     * @return Optional<User>
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Check if username exists
     *
     * @param username
     * @return true if it exists
     */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if email exists
     *
     * @param email
     * @return true if it exists
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
