package manager;


import dao.*;
import datatypes.announcement.Announcement;
import datatypes.messages.AdminMessage;
import datatypes.messages.AdminReply;
import datatypes.messages.FriendRequest;
import datatypes.messages.TextMessage;
import datatypes.server.Activity;
import datatypes.toast.Toast;
import datatypes.user.Person;
import datatypes.user.User;
import datatypes.user.UserAchievement;
import enums.DaoType;
import enums.RequestStatus;
import mail.ReplySender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class DaoManager {
    private static final Logger logger = LogManager.getLogger(DaoManager.class);
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

    private final Map<DaoType, Dao> map;
    private final AnnouncementDao announcementDao = new AnnouncementDao();
    private final FriendRequestDao friendRequestDao = new FriendRequestDao();
    private final TextMessageDao textMessageDao = new TextMessageDao();
    private final UserDao userDao = new UserDao();
    private final UserAchievementDao userAchievementDao = new UserAchievementDao();
    private final AdminMessageDao adminMessageDao = new AdminMessageDao();
    private final AdminReplyMessageDao adminReplyMessageDao = new AdminReplyMessageDao();
    private final ToastDao toastDao = new ToastDao();
    private final ActivityDao activityDao = new ActivityDao((ThreadPoolExecutor) Executors.newFixedThreadPool(4));
    private CountDownLatch latch;

    public DaoManager() {
        map = new HashMap<>();
        map.put(announcementDao.getDaoType(), announcementDao);
        map.put(userDao.getDaoType(), userDao);
        map.put(friendRequestDao.getDaoType(), friendRequestDao);
        map.put(textMessageDao.getDaoType(), textMessageDao);
        map.put(userAchievementDao.getDaoType(), userAchievementDao);
        map.put(activityDao.getDaoType(), activityDao);
        map.put(adminMessageDao.getDaoType(), adminMessageDao);
        map.put(adminReplyMessageDao.getDaoType(), adminReplyMessageDao);
        map.put(toastDao.getDaoType(), toastDao);
        latch = new CountDownLatch(map.size());
        map.values().forEach(dao -> {
            executor.execute(() -> {
                dao.cache();
                latch.countDown();
            });
        });
        try {
            latch.await();
            setUserFields();
            setTextMessages();
            setAchievements();
        } catch (InterruptedException e) {
            logger.error(e);
        }
    }

    private void setAchievements() {
        Collection<UserAchievement> achievements = userAchievementDao.findAll();
        Map<Integer, List<UserAchievement>> userAchievementMap = new HashMap<>();
        achievements.forEach(userAchievement -> {
            userAchievementMap.putIfAbsent(userAchievement.getUserId(), new ArrayList<>());
            userAchievementMap.get(userAchievement.getUserId()).add(userAchievement);
        });
        userAchievementMap.keySet().forEach(userId -> userDao.findById(userId).setAchievements(userAchievementMap.get(userId)));
    }

    private void setTextMessages() {
        for (TextMessage message : textMessageDao.findAll()) {
            setMessages(message);
        }
    }

    private void setUserFields() {
        for (User user : userDao.findAll()) {
            user.setFriends(getFriendsForUser(user.getId()));
            user.setPendingFriendRequests(getPendingRequestsFor(user.getId()));
        }
    }

    private List<Person> getPendingRequestsFor(int receiverId) {
        List<Integer> pendingRequests = new ArrayList<>();
        friendRequestDao.findAll().stream().filter(s -> s.getReceiverId() == receiverId && s.getStatus() == RequestStatus.Pending).
                forEach(s -> pendingRequests.add(s.getSenderId()));
        List<Person> friendRequests = new ArrayList<>();
        pendingRequests.forEach(request -> friendRequests.add(userDao.findById(request)));
        return friendRequests;
    }

    private List<Person> getFriendsForUser(int id) {
        List<Integer> friendsIds = new ArrayList<>();
        friendRequestDao.findAll().stream().filter(s -> s.getReceiverId() == id && s.getStatus() == RequestStatus.Accepted).forEach(s -> friendsIds.add(s.getSenderId()));
        friendRequestDao.findAll().stream().filter(s -> s.getSenderId() == id && s.getStatus() == RequestStatus.Accepted).forEach(s -> friendsIds.add(s.getReceiverId()));
        List<Person> people = new ArrayList<>();
        friendsIds.forEach(friendId -> people.add(userDao.findById(friendId)));
        return people;
    }

    public <E extends Dao> E getDao(DaoType daoType) {
        return (E) map.get(daoType);
    }

    public void insert(FriendRequest friendRequest) {
        if (friendRequestDao.insert(friendRequest)) {
            Person sender = userDao.findById(friendRequest.getSenderId());
            User receiver = userDao.findById(friendRequest.getReceiverId());
            receiver.getPendingFriendRequests().add(sender);
            activityDao.insert(new Activity(sender.getId(), "sent friend request to " + receiver.getUserName(), LocalDateTime.now()));
        }
    }


    public void delete(User deleteUser) {
        executor.execute(() -> {
            if (userDao.deleteById(deleteUser.getId())) {
                activityDao.insert(new Activity(deleteUser.getId(), "'s account is being removed", LocalDateTime.now()));
                deleteUser.getAchievements().forEach(achievement -> userAchievementDao.deleteById(achievement.getId()));

                friendRequestDao.findAllForUser(deleteUser.getId()).forEach(friendRequest -> friendRequestDao.deleteById(friendRequest.getId()));
                deleteUser.getTextMessages().values().forEach(textMessages -> textMessages.forEach(textMessage -> textMessageDao.deleteById(textMessage.getId())));
                activityDao.findAll().stream().filter(activity -> activity.getUserId() == deleteUser.getId()).forEach(activity -> activityDao.deleteById(activity.getId()));

            } else {
                logger.error("Unable to delete user, {}", deleteUser);
            }
        });
    }

    private void insert(UserAchievement userAchievement) {
        executor.execute(() -> {
            if (userAchievementDao.insert(userAchievement)) {
                userDao.findById(userAchievement.getUserId()).getAchievements().add(userAchievement);
                activityDao.insert(new Activity(userAchievement.getUserId(), "gained achievement " + userAchievement.getAchievement().getAchievementName(), LocalDateTime.now()));
            }
        });
    }

    public void insert(TextMessage mes) {
        if (textMessageDao.insert(mes)) {
            activityDao.insert(new Activity(mes.getSenderId(), "sent message to " + userDao.findById(mes.getReceiverId()), LocalDateTime.now()));
            setMessages(mes);
        }
    }

    private void setMessages(TextMessage mes) {
        User sender = userDao.findById(mes.getSenderId());
        User receiver = userDao.findById(mes.getReceiverId());

        sender.getTextMessages().putIfAbsent(receiver.getUserName(), new ArrayList<>());
        sender.getTextMessages().get(receiver.getUserName()).add(mes);

        receiver.getTextMessages().putIfAbsent(sender.getUserName(), new ArrayList<>());
        receiver.getTextMessages().get(sender.getUserName()).add(mes);
    }

    public void update(FriendRequest request) {
        friendRequestDao.update(request);
        User receiver = userDao.findById(request.getReceiverId());
        User sender = userDao.findById(request.getSenderId());
        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);
        receiver.getPendingFriendRequests().remove(sender);
        activityDao.insert(new Activity(receiver.getId(), "accepted " + sender.getUserName() + "'s friend request", LocalDateTime.now()));
    }

    public void delete(FriendRequest request) {
        if (friendRequestDao.deleteById(request.getId())) {
            User sender = userDao.findById(request.getSenderId());
            User receiver = userDao.findById(request.getReceiverId());
            sender.getFriends().remove(receiver);
            receiver.getFriends().remove(sender);
            receiver.getPendingFriendRequests().remove(sender);
            activityDao.insert(new Activity(receiver.getId(), "rejected friendship with " + sender.getUserName(), LocalDateTime.now()));
        }
    }

    public boolean insert(User user) {
        if (userDao.insert(user)) {
            activityDao.insert(new Activity(user.getId(), "Registered", LocalDateTime.now()));
            return true;
        }
        return false;
    }

    public boolean update(User user) {
        if (userDao.update(user)) {
            activityDao.insert(new Activity(user.getId(), "updated profile", LocalDateTime.now()));
            return true;
        }
        return false;
    }

    public void insert(Announcement announcement) {
        if (announcementDao.insert(announcement)) {
            activityDao.insert(new Activity(announcement.getUserId(), "created announcement " + announcement, LocalDateTime.now()));
        }
    }

    public void update(Announcement announcement) {
        if (announcementDao.update(announcement)) {
            activityDao.insert(new Activity(announcement.getUserId(), "updated announcement " + announcement, LocalDateTime.now()));
        }
    }

    public void delete(Integer deleterId, Announcement announcement) {
        if (announcementDao.deleteById(announcement.getId())) {
            activityDao.insert(new Activity(deleterId, "deleted announcement", LocalDateTime.now()));
        }
    }


    public void shutDown() {
        logger.info("DaoManager is shutting down..");
        executor.execute(() -> {
            activityDao.shutDown();
        });
        awaitTerminationAfterShutdown(executor);
        logger.info("Dao manager has shut down !!");
    }

    private void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void insert(AdminMessage adminMessage) {
        adminMessageDao.insert(adminMessage);
    }

    public boolean insert(AdminReply adminReply) {
        executor.execute(() -> {
            if (adminReplyMessageDao.insert(adminReply)) {
                ReplySender.send(adminMessageDao.findById(adminReply.getMessageId()), adminReply.getReplyText());
                AdminMessage message = adminMessageDao.findById(adminReply.getMessageId());
                message.setSeen(true);
                adminMessageDao.update(message);
            }
        });
        return true;
    }

    public boolean insert(Toast toast) {
        return toastDao.insert(toast);
    }
}
