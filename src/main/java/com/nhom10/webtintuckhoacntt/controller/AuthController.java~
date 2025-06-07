package com.nhom10.webtintuckhoacntt.controller;

import com.nhom10.webtintuckhoacntt.model.Userr;
import com.nhom10.webtintuckhoacntt.repository.UserrRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserrRepository userrRepository;

    @PostMapping("/login")
    public String login(String email, String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        // Tìm người dùng theo email và password
        Userr user = userrRepository.findByEmailAndPassword(email, password);
        if (user != null) {
            session.setAttribute("loggedInUser", user);
            redirectAttributes.addFlashAttribute("loginSuccess", true);
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("loginError", "Email hoặc mật khẩu không đúng");
            return "redirect:/";
        }
    }
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/"; 
    }

}
