package dao;

import database.CreateConnection;
import database.mapper.DBRowMapper;
import database.mapper.TextMessageMapper;
import datatypes.User;
import datatypes.messages.Message;
import datatypes.messages.TextMessage;
import enums.DaoType;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static dao.helpers.FinalBlockExecutor.executeFinalBlock;
import static dao.helpers.QueryGenerator.*;
import static database.mapper.TextMessageMapper.*;

public class TextMessageDao implements Dao<Integer, TextMessage> {
    private DBRowMapper<TextMessage> mapper = new TextMessageMapper();
    private Cao<Integer, TextMessage> cao = new Cao<>();
    public TextMessageDao() {

    }
    @Override
    public TextMessage findById(Integer id) {
        return cao.findById(id);
    }

    @Override
    public void insert(TextMessage entity) {
        Connection connection = CreateConnection.getConnection();
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String query = getInsertQuery(TABLE_NAME, SENDER_ID, RECEIVER_ID, DATE_SENT, MESSAGE_SENT);
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, entity.getSenderId());
            statement.setInt(2, entity.getReceiverId());
            statement.setTimestamp(3, entity.getTimestamp());
            statement.setString(4, entity.getTextMessage());
            int result = statement.executeUpdate();
            if (result == 1) {
                rs = statement.getGeneratedKeys();
                rs.next();
                entity.setId(rs.getInt(1));
                System.out.println("Text Message inserted successfully");
                getMessages(entity);

            }
            else System.out.println("Error inserting Text Message");

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            executeFinalBlock(connection, statement, rs);
        }
    }

    @Override
    public Collection<TextMessage> findAll() {
        return cao.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        Connection connection = CreateConnection.getConnection();
        PreparedStatement statement = null;
        TextMessage message = findById(id);
        try {
            String query = getDeleteQuery(TABLE_NAME, TEXT_MESSAGE_ID);
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            int result = statement.executeUpdate();
            if(result == 1){
                System.out.println("message Deleted Successfully");
                cao.delete(id);
            }
            else
                System.out.println("Error Deleting message");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            executeFinalBlock(connection, statement);
        }
    }

    @Override
    public void update(TextMessage entity) {

    }

    @Override
    public DaoType getDaoType() {
        return DaoType.TextMessage;
    }

    public void cache() {
        Connection connection = CreateConnection.getConnection();
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String query = getSelectQuery(TABLE_NAME);
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            while(rs.next()){
                TextMessage message = mapper.mapRow(rs);
                cao.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            executeFinalBlock(connection, statement, rs);
        }

    }

    private void getMessages(TextMessage message) {

    }

    public List<TextMessage> getTextMessagesOfGivenUsers(int senderId, int receiverId){
        //not both sides
        return cao.findAll().stream().filter(s->s.getSenderId()==senderId && s.getReceiverId() == receiverId).collect(Collectors.toList());
    }

}
