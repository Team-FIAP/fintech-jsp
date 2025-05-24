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
        //TODO: Remover Antes do deploy
        User testUser = new User(1L, "Will", "will@email.com", "123", "111.111.111-11", LocalDateTime.now());
        req.getSession().setAttribute("loggedUser", testUser);
        // Obtendo o usuário da sessão
        return (User) req.getSession().getAttribute("loggedUser");
    }
}
