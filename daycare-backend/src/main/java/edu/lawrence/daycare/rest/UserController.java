package edu.lawrence.daycare.rest;

import edu.lawrence.daycare.data.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins="*")
public class UserController {
    private UserDAO userDAO;
    
    public UserController(UserDAO dao) {
        userDAO = dao;
    }
    
    @PostMapping
    public int save(@RequestBody User user) {
        return userDAO.save(user);
    }
    
    @GetMapping(params={"name","password"})
    public int checkUser(@RequestParam(value="name") String user,@RequestParam(value="password") String password) {
        User theUser = userDAO.login(user, password);
        if(theUser == null)
            return -1;
        return theUser.getId();
    }
    
    @GetMapping("/id")
    public int findId(@RequestParam(value="user") String name) {
        User user = userDAO.findByName(name);
        if(user != null)
            return user.getId();
        return 0;
    }
}
