package service;

import dao.UserDao;
import datatypes.promise.Promise;
import datatypes.user.User;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

@Service
@DependsOn("userDao")
public class UserService {

    private UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }
    public Promise save(User user) {
        if (userDao.findById(user.getId()) != null) return userDao.update(user);
        else return userDao.insert(user);
    }

    public User findByUserName(String userName) {
        return userDao.findByUserName(userName);
    }

    public User findById(int userId) {
        return userDao.findById(userId);
    }

    public Promise delete(User deleteUser) {
        return userDao.deleteById(deleteUser.getId());
    }

}
