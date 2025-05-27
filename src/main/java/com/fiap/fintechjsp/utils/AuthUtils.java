package com.fiap.fintechjsp.utils;

import com.fiap.fintechjsp.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;

public class AuthUtils {
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    public static User getUserFromSession(HttpServletRequest req) {
        return (User) req.getSession().getAttribute("loggedUser");
    }
}
