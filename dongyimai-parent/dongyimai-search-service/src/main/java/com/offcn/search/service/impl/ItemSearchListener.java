package com.offcn.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class ItemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        TextMessage msg = (TextMessage) message;
        System.out.println("接收到消息了");
        try {
            List itemList = JSON.parseArray(msg.getText(), TbItem.class);
            itemSearchService.importSolr(itemList);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
