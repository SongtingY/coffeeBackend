package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
//    查询当前用户的会话列表,返回最新私信
    List<Message> selectConversations(int userId, int offset, int limit);

//    查询当前用户的会话数量
    int selectConversationCount(int userId);

//    查询某个对话所包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

//    私信数量
    int selectLetterCount(String conversationId);

//    查询未读私信
    int selectLettersUnreadCount(int userId, String conversationId);

//    新增消息
    int insertMessage(Message message);

//    更改私信状态
    int updateStatus(List<Integer> ids, int status);
}
