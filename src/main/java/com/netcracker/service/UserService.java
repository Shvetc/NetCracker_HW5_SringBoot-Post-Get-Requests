package com.netcracker.service;

import com.netcracker.dao.UserDAO;
import com.netcracker.model.SearchUser;
import com.netcracker.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.nio.file.Files;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    public User addUser(User user) {
        User newUser = userDAO.add(user);
        userDAO.writeFile(newUser);
        return newUser;
    }

    public List<User> find(SearchUser searchUser) {
        return userDAO.getUsers(searchUser.getSurname(), searchUser.getName());
    }

    public void clear(User user) {
        user.setSurname(null);
        user.setName(null);
        user.setPatronymic(null);
        user.setAge(0);
        user.setSalary(0.0);
        user.setEmail(null);
        user.setWorkAddress(null);
    }

    public boolean uploadFile(MultipartFile multipartFile, User user) {

        if (!multipartFile.isEmpty()) {
            File file = new File(multipartFile.getName() + "_upload");
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(file.toPath()))) {
                byte[] getFile = multipartFile.getBytes();
                bufferedOutputStream.write(getFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file));) {
                String info = reader.readLine();
                returnNewUserWithInfo(info, user);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    private void returnNewUserWithInfo(String info, User user) {
        String[] information = info.split("=");
        user.setId(Integer.parseInt(information[0]));
        user.setSurname(information[1]);
        user.setName(information[2]);
        user.setPatronymic(information[3]);
        user.setAge(Integer.parseInt(information[4]));
        user.setSalary(Double.parseDouble(information[5]));
        user.setEmail(information[6]);
        user.setWorkAddress(information[7]);
    }
}
