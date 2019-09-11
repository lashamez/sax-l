package service;

import dao.AdminMessageDao;
import datatypes.messages.AdminMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@DependsOn("adminMessageDao")
public class InboxService {
    private AdminMessageDao adminMessageDao;
    @Autowired
    public InboxService(AdminMessageDao adminMessageDao) {
        this.adminMessageDao = adminMessageDao;
    }

    public List<AdminMessage> getNotSeenMessages(){
        return  adminMessageDao.findAll().stream().filter(s->!s.isSeen()).collect(Collectors.toList());
    }

    public List<AdminMessage> getSeenMessages(){
        return adminMessageDao.findAll().stream().filter(AdminMessage::isSeen).collect(Collectors.toList());
    }
}
